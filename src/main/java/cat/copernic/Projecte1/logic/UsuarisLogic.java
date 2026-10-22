package cat.copernic.Projecte1.logic;

import cat.copernic.Projecte1.entity.Usuaris;
import cat.copernic.Projecte1.repository.UsuarisRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UsuarisLogic {

    private final UsuarisRepository usuariRepo;

    // Inyección por constructor limpia (sin necesidad de @Autowired explícito)
    public UsuarisLogic(UsuarisRepository usuariRepo) {
        this.usuariRepo = usuariRepo;
    }

    // --- OPERACIONES BÁSICAS CRUD ---

    public List<Usuaris> findAllUsuaris() {
        return usuariRepo.findAll();
    }

    public Long saveUsuari(Usuaris usuari) {
        if (usuari.getEstatRegistre() == null || usuari.getEstatRegistre().isBlank()) {
            usuari.setEstatRegistre("PENDENT");
        }
        if (usuari.getPremium() == null) {
            usuari.setPremium(false);
        }
        Usuaris ret = usuariRepo.save(usuari);
        return ret.getId();
    }

    public void deleteUsuariById(Long id) {
        usuariRepo.deleteById(id);
    }

    public Usuaris getUsuariById(Long id) {
        return usuariRepo.findById(id).orElse(null);
    }

    public boolean existsById(Long id) {
        return usuariRepo.existsById(id);
    }

    // --- LÓGICA DE NEGOCIO (REQUISITOS FUNCIONALES) ---

    /**
     * RF08 i RF09: Registrar client des de la web pública amb validacions.
     */
    public Long registrarClientWeb(Usuaris client) throws IllegalArgumentException {

        // 1. Validar camps obligatoris
        if (client.getEmail() == null || client.getEmail().isBlank() ||
            client.getNom() == null || client.getNom().isBlank() ||
            client.getDni() == null || client.getDni().isBlank()) {
            throw new IllegalArgumentException("Tots els camps obligatoris s'han d'omplir.");
        }

        // 2. Validar correu duplicat (RF09)
        if (usuariRepo.findByEmail(client.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El correu electrònic ja està registrat.");
        }

        // 3. Validar data de caducitat del DNI (RF09)
        if (client.getDataCaducitatDni() != null && client.getDataCaducitatDni().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("El document d'identificació (DNI/NIE) està caducat.");
        }

        // 4. Validar data de caducitat de la llicència (RF09)
        if (client.getDataCaducitatLlicencia() != null && client.getDataCaducitatLlicencia().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La llicència de conduir està caducada.");
        }

        // Estat inicial per al registre web (RF08)
        client.setEstatRegistre("PENDENT");
        client.setPremium(false);

        Usuaris nouClient = usuariRepo.save(client);
        return nouClient.getId();
    }

    /**
     * Autenticació d'usuaris controlant els estats PENDENT i REBUTJAT.
     */
    public Usuaris autenticarIValidarEstat(String email, String password) {
        Optional<Usuaris> usuariOpt = usuariRepo.findByEmail(email);

        if (usuariOpt.isEmpty() || !usuariOpt.get().getPassword().equals(password)) {
            throw new IllegalArgumentException("El correu o la contrasenya són incorrectes.");
        }

        Usuaris usuari = usuariOpt.get();

        if ("PENDENT".equalsIgnoreCase(usuari.getEstatRegistre())) {
            throw new IllegalStateException("El teu registre encara està pendent de revisió per un agent.");
        }

        if ("REBUTJAT".equalsIgnoreCase(usuari.getEstatRegistre())) {
            throw new IllegalStateException("La teva sol·licitud de registre ha estat rebutjada.");
        }

        return usuari;
    }

    /**
     * RF11: Llistar registres pendents de validació.
     */
    public List<Usuaris> obtenirRegistresPendents() {
        return usuariRepo.findAll().stream()
                .filter(u -> "PENDENT".equalsIgnoreCase(u.getEstatRegistre()))
                .collect(Collectors.toList());
    }

    /**
     * RF12 i RF13: Aprovar o Rebutjar un registre per l'agent.
     */
    public void canviarEstatRegistre(Long idUsuari, String nouEstat) {
        Usuaris usuari = usuariRepo.findById(idUsuari)
                .orElseThrow(() -> new IllegalArgumentException("Usuari no trobat"));

        usuari.setEstatRegistre(nouEstat); // "VALIDAT" o "REBUTJAT"
        usuariRepo.save(usuari);
    }

    /**
     * Actualitzar dades del client per l'agent des del formulari d'edició (RF12, RF13, RF19, RF21, RF22).
     */
    public void actualitzarClientPerAgent(Usuaris usuariEditat) {
        Usuaris usuariExistent = usuariRepo.findById(usuariEditat.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuari no trobat amb ID: " + usuariEditat.getId()));

        // Actualització de dades personals
        usuariExistent.setNom(usuariEditat.getNom());
        usuariExistent.setEmail(usuariEditat.getEmail());
        usuariExistent.setTelefon(usuariEditat.getTelefon());
        usuariExistent.setAdreca(usuariEditat.getAdreca());
        usuariExistent.setNacionalitat(usuariEditat.getNacionalitat());
        usuariExistent.setDni(usuariEditat.getDni());
        usuariExistent.setDataCaducitatDni(usuariEditat.getDataCaducitatDni());
        usuariExistent.setTipusLlicencia(usuariEditat.getTipusLlicencia());
        usuariExistent.setDataCaducitatLlicencia(usuariEditat.getDataCaducitatLlicencia());
        usuariExistent.setTargetaCredit(usuariEditat.getTargetaCredit());

        // Estat i Premium
        usuariExistent.setEstatRegistre(usuariEditat.getEstatRegistre());
        usuariExistent.setPremium(usuariEditat.getPremium() != null ? usuariEditat.getPremium() : false);

        usuariRepo.save(usuariExistent); 
    }
}