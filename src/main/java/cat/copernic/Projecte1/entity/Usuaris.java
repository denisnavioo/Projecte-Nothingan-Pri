package cat.copernic.Projecte1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.Column;

@Entity
public class Usuaris {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dades bàsiques i accés
    private String nom;
    private String email;
    private String password;
    private int telefon;
    private String adreca;
    private String nacionalitat;

    // Document d'identitat (DNI/Passaport)
    private String dni;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataCaducitatDni;
    private String imatgedni;

    // Llicència de conduir
    private String tipusLlicencia;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataCaducitatLlicencia;
    private String imatgeLlicencia;

    // Dades de pagament
    private String targetaCredit;

    // Estats i perfil
    private String estatRegistre; // "PENDENT", "VALIDAT", "REBUTJAT", "BAIXA"
    @Column(name = "is_premium")
    private boolean premium = false;// Nombre estándar para el atributo

    public Usuaris() {
    }

    public Usuaris(Long id, String nom, String email, String password, int telefon, String adreca, String nacionalitat, String dni, LocalDate dataCaducitatDni, String imatgedni, String tipusLlicencia, LocalDate dataCaducitatLlicencia, String imatgeLlicencia, String targetaCredit, String estatRegistre) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.telefon = telefon;
        this.adreca = adreca;
        this.nacionalitat = nacionalitat;
        this.dni = dni;
        this.dataCaducitatDni = dataCaducitatDni;
        this.imatgedni = imatgedni;
        this.tipusLlicencia = tipusLlicencia;
        this.dataCaducitatLlicencia = dataCaducitatLlicencia;
        this.imatgeLlicencia = imatgeLlicencia;
        this.targetaCredit = targetaCredit;
        this.estatRegistre = estatRegistre;
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTelefon() {
        return telefon;
    }

    public void setTelefon(int telefon) {
        this.telefon = telefon;
    }

    public String getAdreca() {
        return adreca;
    }

    public void setAdreca(String adreca) {
        this.adreca = adreca;
    }

    public String getNacionalitat() {
        return nacionalitat;
    }

    public void setNacionalitat(String nacionalitat) {
        this.nacionalitat = nacionalitat;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public LocalDate getDataCaducitatDni() {
        return dataCaducitatDni;
    }

    public void setDataCaducitatDni(LocalDate dataCaducitatDni) {
        this.dataCaducitatDni = dataCaducitatDni;
    }

    public String getImatgedni() {
        return imatgedni;
    }

    public void setImatgedni(String imatgedni) {
        this.imatgedni = imatgedni;
    }

    public String getTipusLlicencia() {
        return tipusLlicencia;
    }

    public void setTipusLlicencia(String tipusLlicencia) {
        this.tipusLlicencia = tipusLlicencia;
    }

    public LocalDate getDataCaducitatLlicencia() {
        return dataCaducitatLlicencia;
    }

    public void setDataCaducitatLlicencia(LocalDate dataCaducitatLlicencia) {
        this.dataCaducitatLlicencia = dataCaducitatLlicencia;
    }

    public String getImatgeLlicencia() {
        return imatgeLlicencia;
    }

    public void setImatgeLlicencia(String imatgeLlicencia) {
        this.imatgeLlicencia = imatgeLlicencia;
    }

    public String getTargetaCredit() {
        return targetaCredit;
    }

    public void setTargetaCredit(String targetaCredit) {
        this.targetaCredit = targetaCredit;
    }

    public String getEstatRegistre() {
        return estatRegistre;
    }

    public void setEstatRegistre(String estatRegistre) {
        this.estatRegistre = estatRegistre;
    }

    // Métodos para 'premium' (para compatibilidad con 'isPremium' y 'getPremium')
    public boolean isPremium() {
        return premium;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}
