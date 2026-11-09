package cat.copernic.Projecte1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * Controlador de la página principal.
 * Se encarga de mostrar la página de inicio pública.
 */
@Controller
public class HomeController {

    // Muestra la página principal pública.
    @GetMapping("/")
    public String home() {
        return "index";
    }
}