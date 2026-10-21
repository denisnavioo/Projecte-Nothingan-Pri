package cat.copernic.Projecte1.controller;

import cat.copernic.Projecte1.entity.Usuaris;
import cat.copernic.Projecte1.logic.UsuarisLogic;
import jakarta.annotation.PostConstruct;
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

@Controller
@RequestMapping("/web/usuaris")
@Scope("request")
public class UsuarisController {

    private final Logger logger = LoggerFactory.getLogger(UsuarisController.class);

    @Autowired
    private UsuarisLogic usuarisService;

    @PostConstruct
    private void init() {
        // Inicialització si fos necessària
    }

    // --- LLISTAT GENERAL (AGENT) ---
    @GetMapping("/all")
    public String findAll(Model model) {
        logger.debug("Getting all usuaris...");
        List<Usuaris> llista = usuarisService.findAllUsuaris();
        model.addAttribute("usuaris", llista);
        return "usuaris-list";
    }

    // --- CREAR Y EDITAR USUARIO (ADMIN/AGENT) ---
    @GetMapping("/new")
    public String getForm(Model model) {
        logger.debug("Creating new usuari...");

        Usuaris u = new Usuaris();
        u.setEstatRegistre("PENDENT"); // Evita campos nulos en selectores
        u.setPremium(false);           // Evita errores en checkboxes

        model.addAttribute("usuari", u);
        return "edit-usuari";
    }

    @GetMapping("/edit/{id}")
    public String editById(@PathVariable("id") Long usuariId, Model model) {
        logger.debug("Editing usuari " + usuariId);
        Usuaris usuari = usuarisService.getUsuariById(usuariId);

        if (usuari == null) {
            return "redirect:/web/usuaris/all";
        }

        model.addAttribute("usuari", usuari);
        return "edit-usuari";
    }

    // Guarda o actualiza les dades quan l'agente edita des de edit-usuari (RF12, RF13, RF21, RF22)
    @PostMapping("/save")
    public String save(@ModelAttribute("usuari") Usuaris usuari) {
        logger.debug("Saving usuari " + usuari);
        if (usuari.getId() != null) {
            usuarisService.actualitzarClientPerAgent(usuari);
        } else {
            usuarisService.saveUsuari(usuari);
        }
        return "redirect:/web/usuaris/all";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable("id") Long usuariId) {
        logger.debug("Deleting usuari " + usuariId);
        usuarisService.deleteUsuariById(usuariId);
        return "redirect:/web/usuaris/all";
    }

    // --- AUTENTICACIÓ (LOGIN) ---
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // O "login" segons la carpeta del teu HTML
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

    // --- REGISTRE WEB (PUBLIC - RF08/RF09) ---
    @GetMapping("/registre")
    public String mostrarRegistre(Model model) {
        model.addAttribute("usuari", new Usuaris());
        return "registre"; // O "registre" segons la carpeta del teu HTML
    }

    @PostMapping("/registre")
    public String processarRegistre(@ModelAttribute("usuari") Usuaris usuari, Model model) {
        try {
            // Registra el usuario asignando PENDENT
            usuarisService.registrarClientWeb(usuari);

            // Redirige al login indicando registro pendiente
            return "redirect:/web/usuaris/login?registrePendent=true";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "registre";
        }
    }

}
