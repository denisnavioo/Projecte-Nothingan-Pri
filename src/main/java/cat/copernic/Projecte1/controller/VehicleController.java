package cat.copernic.Projecte1.controller;

import cat.copernic.Projecte1.entity.Enum.Categoria;
import cat.copernic.Projecte1.entity.Enum.EstatVehicle;
import cat.copernic.Projecte1.entity.Vehicle;
import cat.copernic.Projecte1.logic.VehicleLogic;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
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
 * @author facu
 */

@Controller
@RequestMapping("/web/vehicles")
@Scope("request")
public class VehicleController {

    Logger logger = LoggerFactory.getLogger(VehicleController.class);

    @Autowired
    private VehicleLogic vehicleLogic;

   @GetMapping("/all")
    public String findAll(
            @RequestParam(name = "matricula", required = false) String matricula,
            @RequestParam(name = "marca", required = false) String marca,
            @RequestParam(name = "categoria", required = false) Categoria categoria,
            @RequestParam(name = "estatVehicle", required = false) EstatVehicle estatVehicle,
            Model model) {

        logger.debug("Getting all vehicles with filters - Matrícula: " + matricula + ", Marca: " + marca + ", Categoria: " + categoria + ", Estat: " + estatVehicle);

        List<Vehicle> llista = vehicleLogic.findByFiltres(matricula, marca, categoria, estatVehicle);

        model.addAttribute("vehicle", llista);
        model.addAttribute("matricula", matricula);
        model.addAttribute("marca", marca);
        model.addAttribute("categoriaSel", categoria);
        model.addAttribute("estatSel", estatVehicle);

        return "vehicles-list";
    }

    @GetMapping("/new")
    public String getForm(Model model) {
        logger.debug("Creating new vehicle...");

        Vehicle p = new Vehicle(0, "", "", "", 0, Categoria.SUV, BigDecimal.ZERO, "");

        model.addAttribute("vehicle", p);

        return "edit-vehicle";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("vehicle") Vehicle vehicle, RedirectAttributes redirectAttributes) {
        logger.debug("Saving vehicle " + vehicle);

        // 1. Validar si la matrícula ya existe antes de tocar la base de datos
        if (vehicleLogic.existsByMatricula(vehicle.getMatricula(), vehicle.getId())) {
            redirectAttributes.addFlashAttribute("error", "La matrícula '" + vehicle.getMatricula() + "' ja està registrada al sistema!");
            redirectAttributes.addFlashAttribute("vehicle", vehicle);

            // Redirigir según si se estaba creando o editando
            if (vehicle.getId() > 0) {
                return "redirect:/web/vehicles/edit/" + vehicle.getId();
            } else {
                return "redirect:/web/vehicles/new";
            }
        }

        // 2. Intentar guardar y capturar posibles excepciones residuales
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
        logger.debug("Deleting vehicle " + vehicleId);

        vehicleLogic.deleteVehicleById(vehicleId);
        redirectAttributes.addFlashAttribute("missatge", "Vehicle eliminat correctament.");

        return "redirect:/web/vehicles/all";
    }

    @GetMapping("/edit/{id}")
    public String editById(@PathVariable("id") Long vehicleId, Model model) {
        logger.debug("Editing product " + vehicleId);

        Vehicle vehicle = vehicleLogic.getVehicleById(vehicleId);

        if (vehicle == null) {
            return "redirect:/web/vehicles/all";
        }

        model.addAttribute("vehicle", vehicle);
        return "edit-vehicle";
    }
}
