package cat.copernic.Projecte1.controller;

import cat.copernic.Projecte1.entity.Reserva;
import cat.copernic.Projecte1.entity.Usuaris;
import cat.copernic.Projecte1.entity.Vehicle;
import cat.copernic.Projecte1.logic.ReservaLogic;
import cat.copernic.Projecte1.logic.UsuarisLogic;
import cat.copernic.Projecte1.logic.VehicleLogic;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/reserves")
@RequestScope
public class ReservaController {

    private final ReservaLogic reservaLogic;
    private final VehicleLogic vehicleLogic;
    private final UsuarisLogic usuarisLogic;

    public ReservaController(ReservaLogic reservaLogic,
            VehicleLogic vehicleLogic, UsuarisLogic usuarisLogic) {
        this.reservaLogic = reservaLogic;
        this.vehicleLogic = vehicleLogic;
        this.usuarisLogic = usuarisLogic;
    }

    // Mostra totes les reserves i aplica els filtres.
    @GetMapping("/all")
    public String findAll(
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String codi,
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false) String data,
            Model model) {

        List<Reserva> reserves = reservaLogic.findAllReservas().stream()
                .filter(r -> dni == null || dni.isBlank()
                        || r.getUsuari().getDni().contains(dni))
                .filter(r -> codi == null || codi.isBlank()
                        || r.getCodiReserva().contains(codi))
                .filter(r -> matricula == null || matricula.isBlank()
                        || r.getVehicle().getMatricula().contains(matricula))
                .filter(r -> data == null || data.isBlank()
                        || (r.getDataReserva() != null
                        && r.getDataReserva().toLocalDate().toString().equals(data)))
                .toList();

        model.addAttribute("reserves", reserves);
        model.addAttribute("dni", dni);
        model.addAttribute("codi", codi);
        model.addAttribute("matricula", matricula);
        model.addAttribute("data", data);

        return "reserves-list";
    }

    // Mostra el formulari per crear una reserva.
    @GetMapping("/new")
    public String getForm(Model model) {
        Reserva reserva = new Reserva();
        reserva.setVehicle(new Vehicle());
        reserva.setUsuari(new Usuaris());

        model.addAttribute("reserva", reserva);
        carregarDadesFormulari(model);

        return "edit-reserva";
    }

    // Busca els vehicles disponibles entre dues dates.
    @GetMapping("/disponibles")
    public String buscarDisponibles(
            @RequestParam String dataInici,
            @RequestParam String dataFi,
            Model model) {

        LocalDateTime inici;
        LocalDateTime fi;

        try {
            inici = LocalDateTime.parse(dataInici);
            fi = LocalDateTime.parse(dataFi);
        } catch (DateTimeParseException e) {
            model.addAttribute("error", "Les dates no són vàlides.");
            Reserva reserva = new Reserva();
            reserva.setVehicle(new Vehicle());
            reserva.setUsuari(new Usuaris());
            model.addAttribute("reserva", reserva);
            model.addAttribute("vehicles", List.of());
            model.addAttribute("usuaris", usuarisLogic.findAllUsuaris());
            model.addAttribute("dataInici", dataInici);
            model.addAttribute("dataFi", dataFi);
            return "edit-reserva";
        }

        if (!fi.isAfter(inici)) {
            model.addAttribute("error",
                    "La data final ha de ser posterior a la data inicial.");
            Reserva reserva = new Reserva();
            reserva.setVehicle(new Vehicle());
            reserva.setUsuari(new Usuaris());
            model.addAttribute("reserva", reserva);
            model.addAttribute("vehicles", List.of());
            model.addAttribute("usuaris", usuarisLogic.findAllUsuaris());
            model.addAttribute("dataInici", dataInici);
            model.addAttribute("dataFi", dataFi);
            return "edit-reserva";
        }

        List<Vehicle> disponibles = vehicleLogic.findAllVehicles().stream()
                .filter(v -> reservaLogic.vehicleDisponible(
                        v.getId(), inici, fi))
                .toList();

        Reserva reserva = new Reserva();
        reserva.setVehicle(new Vehicle());
        reserva.setUsuari(new Usuaris());
        reserva.setDataInici(inici);
        reserva.setDataFi(fi);

        model.addAttribute("reserva", reserva);
        model.addAttribute("vehicles", disponibles);
        model.addAttribute("senseDisponibilitat", disponibles.isEmpty());
        model.addAttribute("usuaris", usuarisLogic.findAllUsuaris());
        model.addAttribute("dataInici", dataInici);
        model.addAttribute("dataFi", dataFi);

        return "edit-reserva";
    }

    // Desa una reserva nova.
    @PostMapping("/save")
    public String save(@ModelAttribute Reserva reserva,
            RedirectAttributes redirectAttributes) {
        try {
            reservaLogic.saveReserva(reserva);
            redirectAttributes.addFlashAttribute(
                    "missatge", "Reserva creada correctament.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/reserves/new";
        }

        return "redirect:/web/reserves/all";
    }

    // Mostra els detalls d'una reserva.
    @GetMapping("/detall/{id}")
    public String details(@PathVariable Integer id, Model model) {
        Reserva reserva = reservaLogic.getReservaById(id);

        if (reserva == null) {
            return "redirect:/web/reserves/all";
        }

        model.addAttribute("reserves", reservaLogic.findAllReservas());
        model.addAttribute("reservaSeleccionada", reserva);

        return "reserves-list";
    }

    // Anul·la una reserva.
    @PostMapping("/cancelar/{id}")
    public String cancel(@PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        if (reservaLogic.anularReserva(id)) {
            redirectAttributes.addFlashAttribute(
                    "missatge", "Reserva anul·lada correctament.");
        } else {
            redirectAttributes.addFlashAttribute(
                    "error", "No s'ha pogut anul·lar la reserva.");
        }

        return "redirect:/web/reserves/all";
    }

    private void carregarDadesFormulari(Model model) {
        model.addAttribute("vehicles", vehicleLogic.findAllVehicles());
        model.addAttribute("usuaris", usuarisLogic.findAllUsuaris());
    }
}