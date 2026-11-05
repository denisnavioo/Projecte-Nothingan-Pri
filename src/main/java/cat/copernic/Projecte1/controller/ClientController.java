package cat.copernic.Projecte1.controller;

import cat.copernic.Projecte1.entity.Reserva;
import cat.copernic.Projecte1.entity.Usuaris;
import cat.copernic.Projecte1.entity.Vehicle;
import cat.copernic.Projecte1.logic.ReservaLogic;
import cat.copernic.Projecte1.logic.UsuarisLogic;
import cat.copernic.Projecte1.logic.VehicleLogic;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 *
 * @author Carolina
 */

@Controller
@RequestMapping("/web/usuaris/client")
public class ClientController {
    
    @Autowired
    private UsuarisLogic usuarisLogic;
    
    @Autowired
    private ReservaLogic reservaLogic;
    
    @Autowired
    private VehicleLogic vehicleLogic;
    
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
    public String mostrarNovaReservaClient(@RequestParam(required = false) String categoria, @RequestParam(required = false) String marcaModel, 
                                                                HttpSession session, Model model) {
        Usuaris usuariLoguejat = (Usuaris) session.getAttribute("usuariLoguejat");
        if (usuariLoguejat == null) {
            return "redirect:/web/usuaris/login";
        }
        
        //Obtenim tots els vehicles de la BD
        List<Vehicle> totsVehicles = vehicleLogic.findAllVehicles();
        // I filtrem només els vehicles disponibles i per els filtres del client.
        List<Vehicle> vehiclesDisponibles = totsVehicles.stream()
            .filter(v -> v.getEstatVehicle() != null && "Disponible".equalsIgnoreCase(v.getEstatVehicle().toString()))
            .filter(v -> (categoria == null || categoria.isBlank() || (v.getCategoria() != null && v.getCategoria().toString().equalsIgnoreCase(categoria))))
            .filter(v -> (marcaModel == null || marcaModel.isBlank() || 
                    (v.getMarca() != null && v.getMarca().toLowerCase().contains(marcaModel.toLowerCase())) ||
                    (v.getModel() != null && v.getModel().toLowerCase().contains(marcaModel.toLowerCase()))))
            .toList();

        Reserva reserva = new Reserva();
        reserva.setVehicle(new Vehicle());
        reserva.setUsuari(usuariLoguejat);
        
        model.addAttribute("vehicles", vehiclesDisponibles);
        model.addAttribute("usuariLoguejat", usuariLoguejat);
        model.addAttribute("reserva", reserva);
        return "nova-reserva";
     }
        

@PostMapping("/client/reserves/save")
    public String guardarReservaClient(@ModelAttribute("reserva") Reserva reserva, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuaris usuariLoguejat = (Usuaris) session.getAttribute("usuariLoguejat");
        if (usuariLoguejat == null) {
            return "redirect:/web/usuaris/login";
        }
        
        boolean faltaVehicle = (reserva.getVehicle() == null || reserva.getVehicle().getId() <= 0);
        boolean faltaDataInici = (reserva.getDataInici() == null);
        boolean faltaDataFi = (reserva.getDataFi() == null);

        if (faltaVehicle || faltaDataInici || faltaDataFi) {
            redirectAttributes.addFlashAttribute("error", "S'han d'omplir tots els camps obligatoris per poder realitzar la reserva (vehicle, dates i hora).");
            return "redirect:/web/usuaris/client/reserves/nova";
        }
        
        try {
            reserva.setUsuari(usuariLoguejat);
            reservaLogic.saveReserva(reserva);
            redirectAttributes.addFlashAttribute("missatge", "Reserva creada amb èxit!");
            return "redirect:/web/usuaris/client/reserves";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/usuaris/client/reserves/nova";
        }
    }

    // MOSTRAR PERFIL DEL CLIENTE
    @GetMapping("/client/perfil")
    public String mostrarPerfilClient(HttpSession session, Model model) {
        Usuaris usuariLoguejat = (Usuaris) session.getAttribute("usuariLoguejat");
        if (usuariLoguejat == null) {
            return "redirect:/web/usuaris/login";
        }

        // Recuperem les dades actualitzades de la base de dades
        Usuaris usuariBD = usuarisLogic.getUsuariById(usuariLoguejat.getId());
        model.addAttribute("usuariLoguejat", usuariBD);
        model.addAttribute("usuari", usuariBD); // Necessari per a th:object="${usuari}" a perfil.html
        return "perfil";
    }

    // GUARDAR CAMBIOS DEL PERFIL
    @PostMapping("/client/perfil/save")
    public String guardarPerfilClient(@ModelAttribute("usuari") Usuaris usuariForm, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuaris usuariLoguejat = (Usuaris) session.getAttribute("usuariLoguejat");
        if (usuariLoguejat == null) {
            return "redirect:/web/usuaris/login";
        }

        // 1. Cargar el usuario real desde la base de datos
        Usuaris usuariBD = usuarisLogic.getUsuariById(usuariLoguejat.getId());

        if (usuariBD != null) {
            // 2. Modificar ÚNICAMENTE el email y el teléfono
            usuariBD.setTelefon(usuariForm.getTelefon());

            // 3. Guardar en la base de datos preservando contraseña, estado, rol, premium, etc.
            usuarisLogic.saveUsuari(usuariBD);

            // 4. Actualizar el usuario en la sesión para mantener la coherencia
            session.setAttribute("usuariLoguejat", usuariBD);

            redirectAttributes.addFlashAttribute("missatge", "Perfil actualitzat amb èxit!");
        } else {
            redirectAttributes.addFlashAttribute("error", "No s'ha pogut trobar el perfil per actualitzar.");
        }
        return "redirect:/web/usuaris/client/perfil";
    }
} 
