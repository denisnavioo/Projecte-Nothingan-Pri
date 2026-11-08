package cat.copernic.Projecte1.controller;

import cat.copernic.Projecte1.entity.Enum.Categoria;
import cat.copernic.Projecte1.entity.Usuaris;
import cat.copernic.Projecte1.entity.Vehicle;
import cat.copernic.Projecte1.logic.VehicleLogic;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.Duration;

@Controller
@RequestMapping("/web/vehicles")
@Scope("request")
public class VehicleController {

    private final Logger logger = LoggerFactory.getLogger(VehicleController.class);

    @Autowired
    private VehicleLogic vehicleLogic;

    @GetMapping("/all")
    public String findAll(
            @RequestParam(name = "matricula", required = false) String matricula,
            @RequestParam(name = "marca", required = false) String marca,
            @RequestParam(name = "model", required = false) String modelParam,
            @RequestParam(name = "anyFabricacio", required = false) Integer anyFabricacio,
            @RequestParam(name = "categoria", required = false) Categoria categoria,
            @RequestParam(name = "dataInici", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInici,
            @RequestParam(name = "dataFi", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFi,
            Model model) {

        String matriculaClean = (matricula != null) ? matricula.trim() : null;
        String marcaClean = (marca != null) ? marca.trim() : null;
        String modelClean = (modelParam != null) ? modelParam.trim() : null;

        logger.debug("Getting all vehicles with filters - Matrícula: {}, Marca: {}, Model: {}, Any: {}, Categoria: {}, Inici: {}, Fi: {}",
                matriculaClean, marcaClean, modelClean, anyFabricacio, categoria, dataInici, dataFi);

        List<Vehicle> llista = vehicleLogic.findByFiltres(
                matriculaClean,
                marcaClean,
                modelClean,
                anyFabricacio,
                categoria,
                dataInici,
                dataFi
        );

        model.addAttribute("vehicle", llista);
        model.addAttribute("matricula", matriculaClean);
        model.addAttribute("marca", marcaClean);
        model.addAttribute("modelParam", modelClean);
        model.addAttribute("anyFabricacio", anyFabricacio);
        model.addAttribute("categoriaSel", categoria);
        model.addAttribute("dataInici", dataInici);
        model.addAttribute("dataFi", dataFi);

        return "vehicles-list";
    } 

    // Busca vehículos disponibles para las fechas seleccionadas.
    @GetMapping("/cercar")
    public String cercarVehicles(
            @RequestParam("dataInici")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInici,
            @RequestParam("dataFi")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFi,
            Model model, HttpSession session) {

        Usuaris usuariLoguejat = (Usuaris) session.getAttribute("usuariLoguejat");
        
        if (!dataFi.isAfter(dataInici)) {
            model.addAttribute("error", "La data final ha de ser posterior a la inicial.");
            return determinarVistaSegonsRol(usuariLoguejat, model);
        }

        List<Vehicle> vehicles = vehicleLogic.findVehiclesDisponibles(dataInici, dataFi);

        model.addAttribute("vehicles", vehicles);
        model.addAttribute("dataInici", dataInici);
        model.addAttribute("dataFi", dataFi);

        long diesLloguer = (long) Math.ceil(
                Duration.between(dataInici, dataFi).toMinutes() / 1440.0
        );

        model.addAttribute("diesLloguer", diesLloguer);

        return determinarVistaSegonsRol(usuariLoguejat, model);
    }
    
            // Mètode auxiliar per determinar quina plantilla carregar segons el rol de la sessió
            private String determinarVistaSegonsRol(Usuaris usuariLoguejat, Model model) {
                if (usuariLoguejat != null) {
                model.addAttribute("usuariLoguejat", usuariLoguejat);

                String rol = (usuariLoguejat.getRol() != null) 
                             ? usuariLoguejat.getRol().trim().toUpperCase() 
                             : "";

                switch (rol) {
                    case "CLIENT":
                        return "index-client"; // O "index-logged"
                    case "ADMIN":
                        // TO DO: RETURN LA VISTA PERTINENT D'ADMIN
                    case "AGENT":
                        // TO DO: RETURN LA VISTA PERTINENT D'AGENT
                }
            }

            // Si no hi ha cap sessió oberta, mostrem la pàgina pública general
            return "index";
            }

    @GetMapping("/new")
    public String getForm(Model model) {
        Vehicle p = new Vehicle(0, "", "", "", 0, Categoria.SUV, BigDecimal.ZERO, "");
        model.addAttribute("vehicle", p);
        return "edit-vehicle";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("vehicle") Vehicle vehicle, RedirectAttributes redirectAttributes) {
        if (vehicleLogic.existsByMatricula(vehicle.getMatricula(), vehicle.getId())) {
            redirectAttributes.addFlashAttribute("error", "La matrícula '" + vehicle.getMatricula() + "' ja està registrada al sistema!");
            redirectAttributes.addFlashAttribute("vehicle", vehicle);
            return (vehicle.getId() > 0) ? "redirect:/web/vehicles/edit/" + vehicle.getId() : "redirect:/web/vehicles/new";
        }

        try {
            vehicleLogic.saveVehicle(vehicle);
            redirectAttributes.addFlashAttribute("missatge", "Vehicle desat correctament!");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("Error de duplicitat a la base de dades", e);
            redirectAttributes.addFlashAttribute("error", "Error: La matrícula introduïda ja existeix.");
            return "redirect:/web/vehicles/new";
        }

        return "redirect:/web/vehicles/all";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable("id") Long vehicleId, RedirectAttributes redirectAttributes) {
        vehicleLogic.deleteVehicleById(vehicleId);
        redirectAttributes.addFlashAttribute("missatge", "Vehicle eliminat correctament.");
        return "redirect:/web/vehicles/all";
    }

    @GetMapping("/edit/{id}")
    public String editById(@PathVariable("id") Long vehicleId, Model model) {
        Vehicle vehicle = vehicleLogic.getVehicleById(vehicleId);
        if (vehicle == null) {
            return "redirect:/web/vehicles/all";
        }
        model.addAttribute("vehicle", vehicle);
        return "edit-vehicle";
    }
}