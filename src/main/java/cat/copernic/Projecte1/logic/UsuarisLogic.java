package cat.copernic.Projecte1.logic;

import cat.copernic.Projecte1.entity.Usuaris;
import cat.copernic.Projecte1.repository.UsuarisRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarisLogic {

    @Autowired
    private UsuarisRepository usuariRepo;

    // --- OPERACIONES BÁSICAS CRUD ---
    public List<Usuaris> findAllUsuaris() {
        return usuariRepo.findAll();
    }

    public Long saveUsuari(Usuaris usuari) {
        validarCamposUnicos(usuari);
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

    // Mètode per filtrar usuaris per DNI de forma Parcial
    public List<Usuaris> findByDni(String dni) {
        return usuariRepo.findByDniContainingIgnoreCase(dni);
    }

    //METODO BIEN DOCUMENTADO
    
    // Usuaris usuaris trae el objeto de usuaris de la entity usuaris
    //throw illegalargumentexception indica que este metodo puede devolver excepciones osea lo de los errores
    public void validarCamposUnicos(Usuaris usuari) throws IllegalArgumentException {

        // 1. Validar si l'Email ja existeix
        
        //primero comprueba que lo que mete el usuario en email no este vacio ni en blanco, después hace la misma comprobación en la bbdd,
        if (usuari.getEmail() != null && !usuari.getEmail().isBlank()) {
            //dentro del if envia una petición a la bbdd comprobanco si el email ya existe
            Optional<Usuaris> existent = usuariRepo.findByEmail(usuari.getEmail());
            // si encuentra un usuario con ese mimso mail devuelve solo ese usuario
            
            //si el mail ya existe verifica por la id para saber si es de otro usuario y salta el error dato ya existe 
            //si dice que nadie esta con ese email ya esta todo verificado y permite guardar 

            if (existent.isPresent() && (usuari.getId() == null || !existent.get().getId().equals(usuari.getId()))) {
                //si el email ya existe salta el siguiente error
                throw new IllegalArgumentException("El correu electrònic '" + usuari.getEmail() + "' ja està registrat.");
            }
            // si todas las comprobaciones estan guays actualiza o crea un usuario 
        }

        // 2. Validar si el DNI ja existeix
        if (usuari.getDni() != null && !usuari.getDni().isBlank()) {
            Optional<Usuaris> existent = usuariRepo.findByDni(usuari.getDni());
            if (existent.isPresent() && (usuari.getId() == null || !existent.get().getId().equals(usuari.getId()))) {
                throw new IllegalArgumentException("El DNI '" + usuari.getDni() + "' ja està registrat.");
            }
        }

        // 3. Validar si el Telèfon ja existeix
        // en el priemr if valida si es cero ya que es un entero y no tiene el isblank, entonces solo  
        // comprueba si ha ingresado algo y no lo ha dejado en blanco 
        if (usuari.getTelefon() > 0) {

            // crea un string y le pasa el entero para saber la mida de el telefono
            String telefonStr = String.valueOf(usuari.getTelefon());
            // ja con la mida comprueba si es de 9 digitos osea logica != 9 (si no es de 9 digitos salta error)
            if (telefonStr.length() != 9) {
                throw new IllegalArgumentException("El número de telèfon ha de tenir exactament 9 dígits.");
            }

            //dentro del if envia una petición a la bbdd comprobanco si el numero ya existe
            List<Usuaris> existent = usuariRepo.findByTelefon(usuari.getTelefon());
            // devuelve una lista de usuarios con el mismo telefono 
            // si no hay usuarios con el mismo telefono lo devuelve vacio

            // crea una variable para saber si esta duplicado
            boolean duplicat = false;
            
            // el bucle pasa por los usuarios que tienen el mismo numero de tel
            for (int i = 0; i < existent.size(); i++) {                
                Usuaris u = existent.get(i);
                
                // comprueba si esta duplicado y si es asi duplicat se vuelve true y hace un break para no comprobar mas
                //si no false
                if (usuari.getId() == null || !u.getId().equals(usuari.getId())) {
                    duplicat = true;
                    break;
                }
            }
            // si el duplicado es true salta el error si no todas las comprobaciones estan bien echas 
            if (duplicat) {
                throw new IllegalArgumentException("El telèfon '" + usuari.getTelefon() + "' ja està registrat.");
            }
        }

    }
}
