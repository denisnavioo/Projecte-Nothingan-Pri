package cat.copernic.Projecte1.logic;

import cat.copernic.Projecte1.entity.Usuaris;
import cat.copernic.Projecte1.repository.UsuarisRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
<<<<<<< HEAD
<<<<<<< HEAD
import java.util.stream.Collectors;
=======
>>>>>>> develop
=======
>>>>>>> develop
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarisLogic {

<<<<<<< HEAD
<<<<<<< HEAD

    @Autowired
    private UsuarisRepository usuariRepo;
     
=======
    @Autowired
    private UsuarisRepository usuariRepo;
>>>>>>> develop
=======
    @Autowired
    private UsuarisRepository usuariRepo;
>>>>>>> develop

    // --- OPERACIONES BÁSICAS CRUD ---
    public List<Usuaris> findAllUsuaris() {
        return usuariRepo.findAll();
    }

    public Long saveUsuari(Usuaris usuari) {
        if (usuari.getEstatRegistre() == null || usuari.getEstatRegistre().isBlank()) {
            usuari.setEstatRegistre("PENDENT");
        }
        if (usuari.getRol() == null || usuari.getRol().isBlank()) {
            usuari.setRol("CLIENT"); // Rol per defecte si no s'especifica
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
     * RF81: Registrar client des de la web pública amb validacions.
     */
    public Long registrarClientWeb(Usuaris client) throws IllegalArgumentException {

        // 1. Validar camps obligatoris
        if (client.getEmail() == null || client.getEmail().isBlank()
                || client.getNom() == null || client.getNom().isBlank()
                || client.getDni() == null || client.getDni().isBlank()) {
            throw new IllegalArgumentException("Tots els camps obligatoris s'han d'omplir.");
        }

        // 2. Validar correu duplicat (RF25)
        if (usuariRepo.findByEmail(client.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El correu electrònic ja està registrat.");
        }

        // 3. Validar data de caducitat del DNI
        if (client.getDataCaducitatDni() != null && client.getDataCaducitatDni().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("El document d'identificació (DNI/NIE) està caducat.");
        }

        // 4. Validar data de caducitat de la llicència
        if (client.getDataCaducitatLlicencia() != null && client.getDataCaducitatLlicencia().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La llicència de conduir està caducada.");
        }

        // Estat inicial i rol per defecte per al registre web (RF81)
        client.setEstatRegistre("PENDENT");
        client.setRol("CLIENT");
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
            throw new IllegalStateException("El teu registre encara està pendent de revisió per un administrador.");
        }

        if ("REBUTJAT".equalsIgnoreCase(usuari.getEstatRegistre())) {
            throw new IllegalStateException("La teva sol·licitud de registre ha estat rebutjada.");
        }

        return usuari;
    }

    /**
     * RF82: Llistar registres pendents de validació.
     */
    public List<Usuaris> obtenirRegistresPendents() {
        return usuariRepo.findByEstatRegistre("PENDENT");
    }

    /**
     * RF82: Aprovar o Rebutjar un registre per l'administrador/agent.
     */
    public void canviarEstatRegistre(Long idUsuari, String nouEstat) {
        Usuaris usuari = usuariRepo.findById(idUsuari)
                .orElseThrow(() -> new IllegalArgumentException("Usuari no trobat"));

        usuari.setEstatRegistre(nouEstat); // "VALIDAT" o "REBUTJAT"
        usuariRepo.save(usuari);
    }

    /**
     * Actualitzar dades del client per l'agent/admin des del formulari
     * d'edició.
     */
    public void actualitzarClientPerAgent(Usuaris usuariEditat) {
        Usuaris usuariExistent = usuariRepo.findById(usuariEditat.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuari no trobat amb ID: " + usuariEditat.getId()));

        // Mantener la contraseña existente si no se proporciona una nueva
        if (!usuariEditat.getPassword().isBlank()) {
            usuariExistent.setPassword(usuariEditat.getPassword());
        }

        // Actualització de dades personals
        usuariExistent.setNom(usuariEditat.getNom());
        usuariExistent.setEmail(usuariEditat.getEmail());
        usuariExistent.setTelefon(usuariEditat.getTelefon());
        usuariExistent.setAdreca(usuariEditat.getAdreca());
        usuariExistent.setPoblacio(usuariEditat.getPoblacio());
        usuariExistent.setNacionalitat(usuariEditat.getNacionalitat());
        usuariExistent.setDni(usuariEditat.getDni());
        usuariExistent.setDataCaducitatDni(usuariEditat.getDataCaducitatDni());
        usuariExistent.setTipusLlicencia(usuariEditat.getTipusLlicencia());
        usuariExistent.setDataCaducitatLlicencia(usuariEditat.getDataCaducitatLlicencia());
        usuariExistent.setTargetaCredit(usuariEditat.getTargetaCredit());

        // Imàtges
        if (usuariEditat.getImatgedni() != null) {
            usuariExistent.setImatgedni(usuariEditat.getImatgedni());
        }
        if (usuariEditat.getImatgeLlicencia() != null) {
            usuariExistent.setImatgeLlicencia(usuariEditat.getImatgeLlicencia());
        }

        // Estat, Rol i Premium
        if (usuariEditat.getRol() != null) {
            usuariExistent.setRol(usuariEditat.getRol());
        }
        usuariExistent.setEstatRegistre(usuariEditat.getEstatRegistre());
        usuariExistent.setPremium(usuariEditat.getPremium() != null ? usuariEditat.getPremium() : false);

        usuariRepo.save(usuariExistent);
    }
<<<<<<< HEAD
<<<<<<< HEAD
    
    // Mètode per filtrar usuaris per DNI de forma Parcial
=======

    // Mètode per filtrar usuaris per DNI de forma Parcial (RF14)
>>>>>>> develop
=======

    // Mètode per filtrar usuaris per DNI de forma Parcial (RF14)
>>>>>>> develop
    public List<Usuaris> findByDni(String dni) {
        return usuariRepo.findByDniContainingIgnoreCase(dni);
    }
}
