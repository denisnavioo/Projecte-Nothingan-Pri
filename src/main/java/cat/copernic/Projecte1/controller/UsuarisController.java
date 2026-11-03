package cat.copernic.Projecte1.controller;

import cat.copernic.Projecte1.entity.Reserva;
import cat.copernic.Projecte1.entity.Usuaris;
import cat.copernic.Projecte1.entity.Vehicle;
import cat.copernic.Projecte1.logic.UsuarisLogic;
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

    @Autowired
    private UsuarisLogic usuarisService;

    // --- LLISTAT GENERAL / FILTRE DNI (AGENT) ---
    @GetMapping("/all")
    // requestParam se usa para recivir/capturar un parametro y que te lo devuelva, entonces le pide a el logic que busque 
    // ese parametro  con sus metodos i el logic le pide al repositori que busque eso en la bbdd.
    public String findAll(@RequestParam(value = "dni", required = false) String dni, Model model) {

        List<Usuaris> llista;
        if (dni != null && !dni.trim().isEmpty()) {
            llista = usuarisService.findByDni(dni.trim());
        } else {
            llista = usuarisService.findAllUsuaris();
        }

        // Filtra directamente en el controller para dejar solo los del rol CLIENT
        List<Usuaris> nomesClients = llista.stream()
                .filter(u -> u.getRol() != null && "CLIENT".equalsIgnoreCase(u.getRol().toString()))
                .toList();

        model.addAttribute("usuaris", nomesClients);
        model.addAttribute("dniFiltre", dni);
        return "usuaris-list";
    }

    // --- CREAR Y EDITAR USUARIO (ADMIN/AGENT) ---
    @GetMapping("/new")
    public String getForm(Model model) {

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
    public String processarLogin(@RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model) {
        try {
            Usuaris client = usuarisService.autenticarIValidarEstat(email, password);
            return "redirect:/web/usuaris/all";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        } catch (IllegalStateException e) {
            model.addAttribute("alertaPendent", e.getMessage());
            return "login";
        }
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

    @GetMapping("/client/reserves")
    public String mostrarReservesClient(Model model) {
        // 1. Aquí recuperaremos las reservas del usuario cuando conectéis el módulo de reservas
        // List<Reserva> reserves = reservaService.getReservesPerUsuari(usuariLoguejat.getId());
        // model.addAttribute("reserves", reserves);

        // 2. Por ahora pasamos un título o datos de prueba si lo necesitas
        return "client-reserves"; // Nombre del archivo HTML (client-reserves.html)
    }

// FORMULARIO CREACIÓN RESERVA CLIENTE
    @GetMapping("/client/reserves/nova")
    public String mostrarNovaReservaClient(Model model) {
        Reserva reserva = new Reserva();
        reserva.setVehicle(new Vehicle());
        reserva.setUsuari(new Usuaris());

        model.addAttribute("reserva", reserva);
        return "nova-reserva";
    }

// MOSTRAR PERFIL DEL CLIENTE
    @GetMapping("/client/perfil")
    public String mostrarPerfilClient(Model model) {
        // Recuperar el usuario autenticado (ejemplo mock o desde servicio)
        Usuaris usuari = usuarisService.getUsuariById(1L); // Sustituir por el ID del usuario logueado

        model.addAttribute("usuari", usuari);
        return "perfil"; // Nombre de la plantilla: perfil.html
    }

// GUARDAR CAMBIOS DEL PERFIL
    @PostMapping("/client/perfil/save")
    public String guardarPerfilClient(@ModelAttribute("usuari") Usuaris usuariActualitzat, RedirectAttributes redirectAttributes) {
        usuarisService.saveUsuari(usuariActualitzat);
        redirectAttributes.addFlashAttribute("missatge", "Perfil actualitzat amb èxit!");
        return "redirect:/web/usuaris/client/perfil";
    }

}
