package cat.copernic.Projecte1.controller;

import cat.copernic.Projecte1.entity.Reserva;
import cat.copernic.Projecte1.entity.Usuaris;
import cat.copernic.Projecte1.entity.Vehicle;
import cat.copernic.Projecte1.logic.ReservaLogic;
import cat.copernic.Projecte1.logic.UsuarisLogic;
import cat.copernic.Projecte1.logic.VehicleLogic;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Alejandro Lorente
 */
@Controller
@RequestMapping("/web/reserves")
@Scope("request")
public class ReservaController {

    private final Logger logger =
            LoggerFactory.getLogger(ReservaController.class);

    @Autowired
    private ReservaLogic reservaLogic;

    @Autowired
    private VehicleLogic vehicleLogic;

    @Autowired
    private UsuarisLogic usuarisLogic;

    // RF40 i RF42: llistar i filtrar reserves
    @GetMapping("/all")
    public String findAll(
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String codiReserva,
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false) String dataReserva,
            Model model) {

        logger.debug("Getting all reserves...");

        List<Reserva> llista = reservaLogic.findAllReservas();

        // Filtre per DNI del client
        if (dni != null && !dni.isBlank()) {
            String filtreDni = dni.trim().toLowerCase();

            llista = llista.stream()
                    .filter(r -> r.getUsuari() != null
                            && r.getUsuari().getDni() != null
                            && r.getUsuari().getDni()
                                    .toLowerCase().contains(filtreDni))
                    .collect(Collectors.toList());
        }

        // Filtre per codi de reserva
        if (codiReserva != null && !codiReserva.isBlank()) {
            String filtreCodi = codiReserva.trim().toLowerCase();

            llista = llista.stream()
                    .filter(r -> r.getCodiReserva() != null
                            && r.getCodiReserva()
                                    .toLowerCase().contains(filtreCodi))
                    .collect(Collectors.toList());
        }

        // Filtre per matrícula del vehicle
        if (matricula != null && !matricula.isBlank()) {
            String filtreMatricula = matricula.trim().toLowerCase();

            llista = llista.stream()
                    .filter(r -> r.getVehicle() != null
                            && r.getVehicle().getMatricula() != null
                            && r.getVehicle().getMatricula()
                                    .toLowerCase().contains(filtreMatricula))
                    .collect(Collectors.toList());
        }

        // Filtre per data de creació de la reserva
        if (dataReserva != null && !dataReserva.isBlank()) {
            LocalDate dataFiltre = LocalDate.parse(dataReserva);

            llista = llista.stream()
                    .filter(r -> r.getDataReserva() != null
                            && r.getDataReserva().toLocalDate()
                                    .equals(dataFiltre))
                    .collect(Collectors.toList());
        }

        model.addAttribute("reserves", llista);
        model.addAttribute("dni", dni);
        model.addAttribute("codiReserva", codiReserva);
        model.addAttribute("matricula", matricula);
        model.addAttribute("dataReserva", dataReserva);

        return "reserves-list";
    }

    // Formulari per crear una reserva
    @GetMapping("/new")
    public String getForm(Model model) {
        logger.debug("Creating new reserva...");

        Reserva reserva = new Reserva();
        reserva.setVehicle(new Vehicle());
        reserva.setUsuari(new Usuaris());

        model.addAttribute("reserva", reserva);
        carregarDadesFormulari(model);

        return "edit-reserva";
    }

    // Crear una reserva
    @PostMapping("/save")
    public String save(
            @ModelAttribute("reserva") Reserva reserva,
            RedirectAttributes redirectAttributes) {

        logger.debug("Saving reserva...");

        try {
            reservaLogic.saveReserva(reserva);

            redirectAttributes.addFlashAttribute(
                    "missatge", "Reserva creada correctament.");

            return "redirect:/web/reserves/all";

        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute(
                    "error", ex.getMessage());

            return "redirect:/web/reserves/new";
        }
    }

    // RF41: consultar els detalls d'una reserva
    @GetMapping("/detall/{id}")
    public String details(@PathVariable("id") Integer id, Model model) {

        Reserva reserva = reservaLogic.getReservaById(id);

        if (reserva == null) {
            return "redirect:/web/reserves/all";
        }

        model.addAttribute("reserves", reservaLogic.findAllReservas());
        model.addAttribute("reservaSeleccionada", reserva);

        return "reserves-list";
    }

    // RF38: anul·lar una reserva
    @PostMapping("/cancelar/{id}")
    public String cancel(
            @PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {

        logger.debug("Canceling reserva: " + id);

        boolean anulada = reservaLogic.anularReserva(id);

        if (anulada) {
            redirectAttributes.addFlashAttribute(
                    "missatge",
                    "Reserva anul·lada correctament.");
        } else {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "No s'ha pogut anul·lar la reserva.");
        }

        return "redirect:/web/reserves/all";
    }

    private void carregarDadesFormulari(Model model) {
        model.addAttribute("vehicles", vehicleLogic.findAllProducts());
        model.addAttribute("usuaris", usuarisLogic.findAllUsuaris());
    }
}
