package cat.copernic.Projecte1.controller;

import cat.copernic.Projecte1.entity.Enum.Categoria;
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

/**
 *
 * @author facu
 */
@Controller
@RequestMapping("/web/vehicles")
@Scope("request")
public class VehicleController {

    Logger logger = LoggerFactory.getLogger(VehicleController.class);

    @PostConstruct
    private void init() {

    }

    @Autowired
    private VehicleLogic vehicleLogic;

    @GetMapping("/all")
    public String findAll(@RequestParam(name = "matricula", required = false) String matricula, Model model) {

        logger.debug("Getting all vehicles with filter: " + matricula);

        List<Vehicle> llista = vehicleLogic.findByMatricula(matricula);

        model.addAttribute("vehicle", llista);
        model.addAttribute("matricula", matricula); // Reté la cerca al quadre de text

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
    public String save(@ModelAttribute("vehicle") Vehicle vehicle) {

        logger.debug("Saving vehicle " + vehicle);

        vehicleLogic.saveVehicle(vehicle);

        return "redirect:/web/vehicles/all";

    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable("id") Long vehicleId) {

        logger.debug("Deleting vehicle " + vehicleId);

        vehicleLogic.deleteVehicleById(vehicleId);

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
