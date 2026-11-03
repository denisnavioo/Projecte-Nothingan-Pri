package cat.copernic.Projecte1.controller;

import cat.copernic.Projecte1.entity.Reserva;
import cat.copernic.Projecte1.entity.Usuaris;
import cat.copernic.Projecte1.entity.Vehicle;
import cat.copernic.Projecte1.logic.ReservaLogic;
import cat.copernic.Projecte1.logic.UsuarisLogic;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//usamos controller ya que estamos usando un modelo vista controllador (MVC) el cual usa tymleaf, osea html y bbdd
@Controller
@RequestMapping("/web/usuaris")
public class UsuarisController {

    private final Logger logger = LoggerFactory.getLogger(UsuarisController.class);

    @Autowired
    private UsuarisLogic usuarisService;
    
    @Autowired
    private ReservaLogic reservaLogic;

    // --- LLISTAT GENERAL / FILTRE DNI (AGENT) ---
    @GetMapping("/all")
    // requestParam se usa para recivir/capturar un parametro y que te lo devuelva, entonces le pide a el logic que busque 
    // ese parametro  con sus metodos i el logic le pide al repositori que busque eso en la bbdd.
    public String findAll(@RequestParam(value = "dni", required = false) String dni, Model model) {
        logger.debug("Getting usuaris with filter DNI: " + dni);

        List<Usuaris> llista;
        if (dni != null && !dni.trim().isEmpty()) {
            llista = usuarisService.findByDni(dni.trim());
        } else {
            llista = usuarisService.findAllUsuaris();
        }

        model.addAttribute("usuaris", llista);
        model.addAttribute("dniFiltre", dni);
        return "usuaris-list";
    }

    // --- CREAR Y EDITAR USUARIO (ADMIN/AGENT) ---
    @GetMapping("/new")
    public String getForm(Model model) {
        logger.debug("Creating new usuari...");

        Usuaris u = new Usuaris();
        u.setEstatRegistre("PENDENT");
        u.setPremium(false);

        model.addAttribute("usuari", u);
        return "edit-usuari";
    }

    @GetMapping("/edit/{id}")
    //path variable  es anotacion corresponiente en identificar un id de un usuario por ejemplo en este caso para 
    //editar exactamente al usuario que devuelva con su informacion, el controller llama al logic i el logic al repository
    public String editById(@PathVariable("id") Long usuariId, Model model) {
        Usuaris usuari = usuarisService.getUsuariById(usuariId);

        if (usuari == null) {
            return "redirect:/web/usuaris/all";
        }

        model.addAttribute("usuari", usuari);
        return "edit-usuari";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("usuari") Usuaris usuari, Model model) {
        try {
            if (usuari.getId() != null) {
                usuarisService.actualitzarClientPerAgent(usuari);
            } else {
                usuarisService.saveUsuari(usuari);
            }
            return "redirect:/web/usuaris/all";
        } catch (IllegalArgumentException e) {
            // Captura l'error de duplicat o validació i el mostra a la plantilla HTML
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuari", usuari); // Manté les dades introduïdes al formulari
            return "edit-usuari";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable("id") Long usuariId) {
        usuarisService.deleteUsuariById(usuariId);
        return "redirect:/web/usuaris/all";
    }

    // --- AUTENTICACIÓ (LOGIN) ---
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String processarLogin(@RequestParam("email") String email, HttpSession session,
            @RequestParam("password") String password,
            Model model) {
        try {
            Usuaris usuari = usuarisService.autenticarIValidarEstat(email, password);
            session.setAttribute("usuariLoguejat", usuari);
            
            String rol = (usuari.getRol() != null) ? usuari.getRol().trim().toUpperCase() : "";
            
            if ("CLIENT".equals(rol)) {
                return "redirect:/web/usuaris/client/reserves";
            } else if ("ADMIN".equals(rol)) {
                return "redirect:/web/usuaris/admin";
            } else {
                return "redirect:/web/usuaris/all";
            }
           
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        } catch (IllegalStateException e) {
            model.addAttribute("alertaPendent", e.getMessage());
            return "login";
        }
    }
    
    // Tancar sessió
    @GetMapping("/logout")
    public String tancarSession(HttpSession session) {
        session.invalidate(); //Destrueix la sessió
        return "redirect:/web/usuaris/login?logout=true";
    }

    // --- REGISTRE WEB (PUBLIC - RF81) ---
    @GetMapping("/registre")
    public String mostrarRegistre(Model model) {
        model.addAttribute("usuari", new Usuaris());
        return "registre";
    }

    @PostMapping("/registre")
    public String processarRegistre(@ModelAttribute("usuari") Usuaris usuari,
            @RequestParam("fitxerDni") MultipartFile fitxerDni,
            @RequestParam("fitxerLlicencia") MultipartFile fitxerLlicencia,
            Model model) {
        try {
            // Registra el client assignant estat PENDENT i rol CLIENT (executa la validació)
            usuarisService.registrarClientWeb(usuari);
            return "redirect:/web/usuaris/login?registrePendent=true";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuari", usuari);
            return "registre";
        }
    }

    // --- VALIDAR REGISTRES (ADMINISTRADOR - RF82) ---
    @GetMapping("/pendents")
    public String llistarPendents(Model model) {
        List<Usuaris> pendents = usuarisService.obtenirRegistresPendents();
        model.addAttribute("usuarisPendents", pendents);
        return "validar-registres";
    }

    @PostMapping("/validar/{id}")
    public String validarRegistre(@PathVariable("id") Long id, @RequestParam("estat") String nouEstat) {
        usuarisService.canviarEstatRegistre(id, nouEstat);
        return "redirect:/web/usuaris/pendents";
    }

    /*@GetMapping("/admin")
    public String admin(Model model) {
        return "admin clients";
    }*/

@GetMapping("/client/reserves")
public String mostrarReservesClient(HttpSession session, Model model) {
    //Obtenir usuari autenticat desde la sessió http
    Usuaris usuariLoguejat = (Usuaris) session.getAttribute("usuariLoguejat");
    
    // si no hi ha sessió, redirigir al login.
    if (usuariLoguejat == null) {
        return "redirect:/web/usuaris/login";
    }
    
    //passar l'usuari al model per mostrar el nom en el header
    model.addAttribute("usuariLoguejat", usuariLoguejat);
    
    List<Reserva> reserves = reservaLogic.getReservesPerUsuari(usuariLoguejat.getId());
    if (reserves == null) {
        reserves = List.of();
    }
    model.addAttribute("reserves", reserves);
    return "client-reserves";
}

// FORMULARIO CREACIÓN RESERVA CLIENTE
@GetMapping("/client/reserves/nova")
public String mostrarNovaReservaClient(HttpSession session, Model model) {
    Usuaris usuariLoguejat = (Usuaris) session.getAttribute("usuariLoguejat");
    
    if (usuariLoguejat == null) {
        return "redirect:/web/usuaris/login";
    }
    
    Reserva reserva = new Reserva();
    reserva.setVehicle(new Vehicle());
    reserva.setUsuari(usuariLoguejat);

    model.addAttribute("usuariLoguejat", usuariLoguejat);
    model.addAttribute("reserva", reserva);
    return "nova-reserva";
}

@PostMapping("/client/reserves/save")
    public String guardarReservaClient(@ModelAttribute("reserva") Reserva reserva) {
        reservaLogic.saveReserva(reserva); // Invocació sobre la instància 'reservaLogic'
        return "redirect:/web/usuaris/client/reserves";
    }
    
// MOSTRAR PERFIL DEL CLIENTE
@GetMapping("/client/perfil")
public String mostrarPerfilClient(HttpSession session, Model model) {
    Usuaris usuariLoguejat = (Usuaris) session.getAttribute("usuariLoguejat");
    if (usuariLoguejat == null) {
        return "redirect:/web/usuaris/login";
    }
    
    Usuaris usuariBD = usuarisService.getUsuariById(usuariLoguejat.getId());
    model.addAttribute("usuariLoguejat", usuariBD);
    model.addAttribute("usuari", usuariBD); // Necesario para th:object="${usuari}" en perfil.html
    return "perfil"; // Nombre de la plantilla: perfil.html
}

// GUARDAR CAMBIOS DEL PERFIL
    @PostMapping("/client/perfil/save")
    public String guardarPerfilClient(@ModelAttribute("usuari") Usuaris usuariActualitzat, RedirectAttributes redirectAttributes) {
        usuarisService.saveUsuari(usuariActualitzat);
        redirectAttributes.addFlashAttribute("missatge", "Perfil actualitzat amb èxit!");
        return "redirect:/web/usuaris/client/perfil";
    }

// PAGINAS DE ADMIN
    @GetMapping("/admin")
    public String admin(@RequestParam(value = "dni", required = false) String dni, Model model) {

        List<Usuaris> llista;
        if (dni != null && !dni.trim().isEmpty()) {
            llista = usuarisService.findByDni(dni.trim());
        } else {
            llista = usuarisService.findAllUsuaris();
        }

        model.addAttribute("usuaris", llista);
        model.addAttribute("dniFiltre", dni);

        return "admin-clients";
    }

    @GetMapping("/admin/view/{id}")
    public String viewClientAdmin(@PathVariable("id") Long id, Model model) {
        Usuaris usuari = usuarisService.getUsuariById(id);
        model.addAttribute("usuari", usuari);
        model.addAttribute("modoLectura", true);
        return "admin-edit-clients";
    }


    
// Mode d'edició per a Administrador
    @GetMapping("/admin/edit/{id}")
    public String editClientAdmin(@PathVariable("id") Long id, Model model) {
        Usuaris usuari = usuarisService.getUsuariById(id);
        model.addAttribute("usuari", usuari);
        model.addAttribute("modoLectura", false);
        return "admin-edit-clients";
    }
    
        /**
     * REUTILIZACIÓN DE PLANTILLAS EN THYMELEAF (Principio DRY - Don't Repeat Yourself):
     * Ambos métodos devuelven la misma vista HTML ("admin-edit-clients").
     * 
     * Pasamos la variable 'modoLectura' al Model:
     * - En viewClientAdmin es TRUE  -> En Thymeleaf se usa (th:readonly="${modoLectura}") para bloquear los campos.
     * - En editClientAdmin es FALSE -> Los campos del formulario se pueden editar normalmente.
     * 
     * De esta forma reutilizamos el mismo archivo HTML sin duplicar código.
     */

        @GetMapping("/admin/new")
    public String newClientAdmin(Model model) {
        model.addAttribute("usuari", new Usuaris());
        model.addAttribute("modoLectura", false);
        return "admin-edit-clients";
    }
    
// Donar de baixa per a Administrador i redreçar al panell admin
    @GetMapping("/admin/delete/{id}")
    public String deleteClientAdmin(@PathVariable("id") Long id) {
        usuarisService.deleteUsuariById(id); // o canviar estat a 'BAIXA'
        return "redirect:/web/usuaris/admin";
    }
// 1. Mostrar el formulario para crear un NUEVO cliente como Admin



// 2. Procesar el guardado desde el formulario de Admin
    @PostMapping("/admin/save")
    public String saveClientAdmin(@ModelAttribute("usuari") Usuaris usuari) {
        // Si la contraseña viene vacía al editar, mantenemos la contraseña guardada
        if (usuari.getId() != null && (usuari.getPassword() == null || usuari.getPassword().isBlank())) {
            Usuaris usuariExistente = usuarisService.getUsuariById(usuari.getId());
            if (usuariExistente != null) {
                usuari.setPassword(usuariExistente.getPassword());
            }
        }

        usuarisService.saveUsuari(usuari);

        // Redirige directamente al listado de clientes de Admin
        return "redirect:/web/usuaris/admin";
    }
}
