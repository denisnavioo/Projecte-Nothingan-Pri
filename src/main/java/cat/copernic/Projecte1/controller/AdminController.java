package cat.copernic.Projecte1.controller;

import cat.copernic.Projecte1.entity.Enum.Categoria;
import cat.copernic.Projecte1.entity.Reserva;
import cat.copernic.Projecte1.entity.Usuaris;
import cat.copernic.Projecte1.entity.Vehicle;
import cat.copernic.Projecte1.logic.ReservaLogic;
import cat.copernic.Projecte1.logic.UsuarisLogic;
import cat.copernic.Projecte1.logic.VehicleLogic;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/admin")
public class AdminController {

    @Autowired
    private UsuarisLogic usuarisService;

    @Autowired
    private VehicleLogic vehicleLogic;

    @Autowired
    private ReservaLogic reservaLogic;

    // Prevencion de error con /web
    // Método de captura predeterminado
    @GetMapping({"", "/"})
    public String indexAdmin() {
        return "redirect:/web/admin/usuaris";
    }

    // ==========================================
    // SECCIÓN: GESTIÓN DE CLIENTES / USUARIOS
    // ==========================================
    @GetMapping("/usuaris")
    public String admin(@RequestParam(value = "dni", required = false) String dni, Model model) {
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

        return "admin-clients";
    }

    @GetMapping("/view/{id}")
    public String viewClientAdmin(@PathVariable("id") Long id, Model model) {
        Usuaris usuari = usuarisService.getUsuariById(id);
        model.addAttribute("usuari", usuari);
        model.addAttribute("modoLectura", true);
        return "admin-edit-clients";
    }

    @GetMapping("/edit/{id}")
    public String editClientAdmin(@PathVariable("id") Long id, Model model) {
        Usuaris usuari = usuarisService.getUsuariById(id);
        model.addAttribute("usuari", usuari);
        model.addAttribute("modoLectura", false);
        return "admin-edit-clients";
    }

    @GetMapping("/new")
    public String newClientAdmin(Model model) {
        Usuaris u = new Usuaris();
        u.setEstatRegistre("PENDENT");
        u.setPremium(false);

        model.addAttribute("usuari", u);
        model.addAttribute("modoLectura", false);
        return "admin-edit-clients";
    }

    @GetMapping("/delete/{id}")
    public String deleteClientAdmin(@PathVariable("id") Long id) {
        usuarisService.deleteUsuariById(id);
        return "redirect:/web/admin/usuaris";
    }

    @PostMapping("/save")
    public String saveClientAdmin(@ModelAttribute("usuari") Usuaris usuari, Model model) {
        try {
            // Mantiene la contraseña si se edita y el campo viene vacío
            if (usuari.getId() != null && (usuari.getPassword() == null || usuari.getPassword().isBlank())) {
                Usuaris usuariExistente = usuarisService.getUsuariById(usuari.getId());
                if (usuariExistente != null) {
                    usuari.setPassword(usuariExistente.getPassword());
                }
            }

            usuarisService.saveUsuari(usuari);
            return "redirect:/web/admin/usuaris";

        } catch (IllegalArgumentException e) {
            // Captura las validaciones de UsuarisLogic (ej. teléfono de 9 dígitos, DNI repetido) y las pasa a la vista
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuari", usuari);
            model.addAttribute("modoLectura", false);
            return "admin-edit-clients";
        }
    }

    // ==========================================
    // SECCIÓN: GESTIÓN DE VEHÍCULOS
    // ==========================================
    @GetMapping("/vehicles")
    public String findAllVehicles(@RequestParam(name = "matricula", required = false) String matricula, Model model) {
        List<Vehicle> llista = vehicleLogic.findByMatricula(matricula);

        model.addAttribute("vehicles", llista);
        model.addAttribute("matricula", matricula);

        return "admin-vehicles";
    }

    @GetMapping("/vehicles/new")
    public String getFormVehicle(Model model) {
        Vehicle p = new Vehicle(0, "", "", "", 0, Categoria.SUV, BigDecimal.ZERO, "");
        model.addAttribute("vehicle", p);
        return "admin-edit-vehicle";
    }

    @PostMapping("/vehicles/save")
    public String saveVehicle(@ModelAttribute("vehicle") Vehicle vehicle, RedirectAttributes redirectAttributes) {
        if (vehicleLogic.existsByMatricula(vehicle.getMatricula(), vehicle.getId())) {
            redirectAttributes.addFlashAttribute("error", "La matrícula '" + vehicle.getMatricula() + "' ja està registrada al sistema!");
            redirectAttributes.addFlashAttribute("vehicle", vehicle);

            if (vehicle.getId() > 0) {
                return "redirect:/web/admin/vehicles/edit/" + vehicle.getId();
            } else {
                return "redirect:/web/admin/vehicles/new";
            }
        }

        try {
            vehicleLogic.saveVehicle(vehicle);
            redirectAttributes.addFlashAttribute("missatge", "Vehicle desat correctament!");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Error: La matrícula introduïda ja existeix.");
            return "redirect:/web/admin/vehicles/new";
        }

        return "redirect:/web/admin/vehicles";
    }

    @GetMapping("/vehicles/edit/{id}")
    public String editVehicleById(@PathVariable("id") Long vehicleId, Model model) {
        Vehicle vehicle = vehicleLogic.getVehicleById(vehicleId);

        if (vehicle == null) {
            return "redirect:/web/admin/vehicles";
        }

        model.addAttribute("vehicle", vehicle);
        return "admin-edit-vehicle";
    }

    @GetMapping("/vehicles/delete/{id}")
    public String deleteVehicleById(@PathVariable("id") Long vehicleId, RedirectAttributes redirectAttributes) {
        vehicleLogic.deleteVehicleById(vehicleId);
        redirectAttributes.addFlashAttribute("missatge", "Vehicle eliminat correctament.");

        return "redirect:/web/admin/vehicles";
    }

    // ==========================================
    // SECCIÓN: GESTIÓN DE RESERVAS
    // ==========================================
    @GetMapping("/reserves")
    public String findAllReserves(
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String codi,
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false) String data,
            @RequestParam(required = false) Integer detall,
            Model model) {

        List<Reserva> reserves = reservaLogic.findAllReservas().stream()
                .filter(r -> dni == null || dni.isBlank()
                || (r.getUsuari() != null && r.getUsuari().getDni() != null && r.getUsuari().getDni().contains(dni)))
                .filter(r -> codi == null || codi.isBlank()
                || (r.getCodiReserva() != null && r.getCodiReserva().contains(codi)))
                .filter(r -> matricula == null || matricula.isBlank()
                || (r.getVehicle() != null && r.getVehicle().getMatricula() != null && r.getVehicle().getMatricula().contains(matricula)))
                .filter(r -> data == null || data.isBlank()
                || (r.getDataReserva() != null && r.getDataReserva().toLocalDate().toString().equals(data)))
                .toList();

        model.addAttribute("reserves", reserves);
        model.addAttribute("dni", dni);
        model.addAttribute("codi", codi);
        model.addAttribute("matricula", matricula);
        model.addAttribute("data", data);

        if (detall != null) {
            Reserva seleccionada = reservaLogic.getReservaById(detall);
            if (seleccionada != null) {
                model.addAttribute("reservaSeleccionada", seleccionada);
            }
        }

        return "admin-reserves";
    }

    @GetMapping("/reserves/new")
    public String getFormReserva(Model model) {
        Reserva reserva = new Reserva();
        reserva.setVehicle(new Vehicle());
        reserva.setUsuari(new Usuaris());

        model.addAttribute("reserva", reserva);
        carregarDadesFormulari(model);

        return "admin-edit-reserva";
    }

    @GetMapping("/reserves/disponibles")
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
            model.addAttribute("usuaris", usuarisService.findAllUsuaris());
            model.addAttribute("dataInici", dataInici);
            model.addAttribute("dataFi", dataFi);

            return "admin-edit-reserva";
        }

        if (!fi.isAfter(inici)) {
            model.addAttribute("error", "La data final ha de ser posterior a la data inicial.");

            Reserva reserva = new Reserva();
            reserva.setVehicle(new Vehicle());
            reserva.setUsuari(new Usuaris());

            model.addAttribute("reserva", reserva);
            model.addAttribute("vehicles", List.of());
            model.addAttribute("usuaris", usuarisService.findAllUsuaris());
            model.addAttribute("dataInici", dataInici);
            model.addAttribute("dataFi", dataFi);

            return "admin-edit-reserva";
        }

        List<Vehicle> disponibles = vehicleLogic.findAllVehicles().stream()
                .filter(v -> reservaLogic.vehicleDisponible(v.getId(), inici, fi))
                .toList();

        Reserva reserva = new Reserva();
        reserva.setVehicle(new Vehicle());
        reserva.setUsuari(new Usuaris());
        reserva.setDataInici(inici);
        reserva.setDataFi(fi);

        model.addAttribute("reserva", reserva);
        model.addAttribute("vehicles", disponibles);
        model.addAttribute("senseDisponibilitat", disponibles.isEmpty());
        model.addAttribute("usuaris", usuarisService.findAllUsuaris());
        model.addAttribute("dataInici", dataInici);
        model.addAttribute("dataFi", dataFi);

        return "admin-edit-reserva";
    }

    @PostMapping("/reserves/save")
    public String saveReserva(@ModelAttribute Reserva reserva, RedirectAttributes redirectAttributes) {
        try {
            reservaLogic.saveReserva(reserva);
            redirectAttributes.addFlashAttribute("missatge", "Reserva creada correctament.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/admin/reserves/new";
        }

        return "redirect:/web/admin/reserves";
    }

    @GetMapping("/reserves/detall/{id}")
    public String detailsReserva(
            @PathVariable Integer id,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String codi,
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false) String data,
            RedirectAttributes attributes) {

        attributes.addAttribute("detall", id);

        if (dni != null && !dni.isBlank()) {
            attributes.addAttribute("dni", dni);
        }
        if (codi != null && !codi.isBlank()) {
            attributes.addAttribute("codi", codi);
        }
        if (matricula != null && !matricula.isBlank()) {
            attributes.addAttribute("matricula", matricula);
        }
        if (data != null && !data.isBlank()) {
            attributes.addAttribute("data", data);
        }

        return "redirect:/web/admin/reserves";
    }

    @PostMapping("/reserves/cancelar/{id}")
    public String cancelReserva(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (reservaLogic.anularReserva(id)) {
            redirectAttributes.addFlashAttribute("missatge", "Reserva anul·lada correctament.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No s'ha pogut anul·lar la reserva.");
        }

        return "redirect:/web/admin/reserves";
    }

    private void carregarDadesFormulari(Model model) {
        model.addAttribute("vehicles", vehicleLogic.findAllVehicles());
        model.addAttribute("usuaris", usuarisService.findAllUsuaris());
    }

    // ==========================================
    // SECCIÓN: GESTIÓN DE AGENTES
    // ==========================================
    @GetMapping("/agents")
    public String findAllAgents(@RequestParam(value = "dni", required = false) String dni, Model model) {
        List<Usuaris> llista;

        if (dni != null && !dni.trim().isEmpty()) {
            llista = usuarisService.findByDni(dni.trim());
        } else {
            llista = usuarisService.findAllUsuaris();
        }

        // Dejar únicamente los usuarios con rol AGENT
        List<Usuaris> nomesAgents = llista.stream()
                .filter(u -> u.getRol() != null && "AGENT".equalsIgnoreCase(u.getRol().toString()))
                .toList();

        model.addAttribute("agents", nomesAgents);
        model.addAttribute("dniFiltre", dni);

        return "admin-agent";
    }

    @GetMapping("/agents/new")
    public String newAgentAdmin(Model model) {
        Usuaris agent = new Usuaris();
        agent.setEstatRegistre("VALIDAT"); // Un agente creado por el admin ya nace validado
        agent.setPremium(false);

        model.addAttribute("agent", agent);
        model.addAttribute("modoLectura", false);
        return "admin-agent-edit";
    }

    @GetMapping("/agents/edit/{id}")
    public String editAgentAdmin(@PathVariable("id") Long id, Model model) {
        Usuaris agent = usuarisService.getUsuariById(id);

        if (agent == null) {
            return "redirect:/web/admin/agents";
        }

        model.addAttribute("agent", agent);
        model.addAttribute("modoLectura", false);
        return "admin-agent-edit";
    }

    @GetMapping("/agents/view/{id}")
    public String viewAgentAdmin(@PathVariable("id") Long id, Model model) {
        Usuaris agent = usuarisService.getUsuariById(id);

        if (agent == null) {
            return "redirect:/web/admin/agents";
        }

        model.addAttribute("agent", agent);
        model.addAttribute("modoLectura", true);
        return "admin-agent-edit";
    }

    @PostMapping("/agents/save")
    public String saveAgentAdmin(@ModelAttribute("agent") Usuaris agent, Model model, RedirectAttributes redirectAttributes) {
        try {

            agent.setRol("AGENT");
            // Asignar o forzar siempre el rol AGENT
            // Si usas Enum: agent.setRol(cat.copernic.Projecte1.entity.Enum.Rol.AGENT);

            // Si la contraseña viene vacía al editar, mantener la contraseña previa
            if (agent.getId() != null && (agent.getPassword() == null || agent.getPassword().isBlank())) {
                Usuaris usuariExistente = usuarisService.getUsuariById(agent.getId());
                if (usuariExistente != null) {
                    agent.setPassword(usuariExistente.getPassword());
                }
            }

            usuarisService.saveUsuari(agent);
            redirectAttributes.addFlashAttribute("missatge", "Agent desat correctament!");
            return "redirect:/web/admin/agents";

        } catch (IllegalArgumentException e) {
            // Captura las validaciones de UsuarisLogic (teléfono de 9 dígitos, DNI repetido, etc.)
            model.addAttribute("error", e.getMessage());
            model.addAttribute("agent", agent);
            model.addAttribute("modoLectura", false);
            return "admin-agent-edit";
        }
    }

    @GetMapping("/agents/delete/{id}")
    public String deleteAgentAdmin(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        usuarisService.deleteUsuariById(id);
        redirectAttributes.addFlashAttribute("missatge", "Agent eliminat correctament.");
        return "redirect:/web/admin/agents";
    }

    // ==========================================
    // SECCIÓ: INFORMES (RF91)
    // ==========================================
    @GetMapping({"/informes", "/informes/reserves"})
    public String mostrarInformeReserves(
            @RequestParam(value = "clientId", required = false) String clientIdStr,
            Model model) {

        Integer clientId = null;
        if (clientIdStr != null && !clientIdStr.trim().isEmpty()) {
            try {
                clientId = Integer.parseInt(clientIdStr.trim());
            } catch (NumberFormatException ignored) {
                clientId = null;
            }
        }

        List<Reserva> reserves = reservaLogic.obtenirInformeReserves(clientId);

        List<Usuaris> totsClients = usuarisService.findAllUsuaris().stream()
                .filter(u -> u.getRol() != null && "CLIENT".equalsIgnoreCase(u.getRol().toString()))
                .toList();

        BigDecimal importTotal = reserves.stream()
                .map(r -> r.getPreuLloguer() != null ? r.getPreuLloguer() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("reserves", reserves);
        model.addAttribute("clients", totsClients);
        model.addAttribute("clientIdSeleccionat", clientId);
        model.addAttribute("totalReserves", reserves.size());
        model.addAttribute("importTotal", importTotal);

        return "admin-informes";
    }
}
