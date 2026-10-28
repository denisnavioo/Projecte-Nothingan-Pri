package cat.copernic.Projecte1.entity;

import cat.copernic.Projecte1.entity.Enum.EstatReserva;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codi_reserva", nullable = false, unique = true)
    private String codiReserva;

    @Column(nullable = false)
    private LocalDateTime dataReserva;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(nullable = false)
    private LocalDateTime dataInici;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(nullable = false)
    private LocalDateTime dataFi;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preuLloguer;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal importFianca;

    @Column(precision = 10, scale = 2)
    private BigDecimal fiancaRetinguda;

    @Column(precision = 10, scale = 2)
    private BigDecimal costIncidencia;

    @Column(precision = 10, scale = 2)
    private BigDecimal fiancaRetornada;

    @Column(precision = 10, scale = 2)
    private BigDecimal costFinal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstatReserva estat;

    @Column(length = 1000)
    private String observacions;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "usuari_id", nullable = false)
    private Usuaris usuari;

    public Reserva() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodiReserva() {
        return codiReserva;
    }

    public void setCodiReserva(String codiReserva) {
        this.codiReserva = codiReserva;
    }

    public LocalDateTime getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(LocalDateTime dataReserva) {
        this.dataReserva = dataReserva;
    }

    public LocalDateTime getDataInici() {
        return dataInici;
    }

    public void setDataInici(LocalDateTime dataInici) {
        this.dataInici = dataInici;
    }

    public LocalDateTime getDataFi() {
        return dataFi;
    }

    public void setDataFi(LocalDateTime dataFi) {
        this.dataFi = dataFi;
    }

    public BigDecimal getPreuLloguer() {
        return preuLloguer;
    }

    public void setPreuLloguer(BigDecimal preuLloguer) {
        this.preuLloguer = preuLloguer;
    }

    public BigDecimal getImportFianca() {
        return importFianca;
    }

    public void setImportFianca(BigDecimal importFianca) {
        this.importFianca = importFianca;
    }

    public BigDecimal getFiancaRetinguda() {
        return fiancaRetinguda;
    }

    public void setFiancaRetinguda(BigDecimal fiancaRetinguda) {
        this.fiancaRetinguda = fiancaRetinguda;
    }

    public BigDecimal getCostIncidencia() {
        return costIncidencia;
    }

    public void setCostIncidencia(BigDecimal costIncidencia) {
        this.costIncidencia = costIncidencia;
    }

    public BigDecimal getFiancaRetornada() {
        return fiancaRetornada;
    }

    public void setFiancaRetornada(BigDecimal fiancaRetornada) {
        this.fiancaRetornada = fiancaRetornada;
    }

    public BigDecimal getCostFinal() {
        return costFinal;
    }

    public void setCostFinal(BigDecimal costFinal) {
        this.costFinal = costFinal;
    }

    public EstatReserva getEstat() {
        return estat;
    }

    public void setEstat(EstatReserva estat) {
        this.estat = estat;
    }

    public String getObservacions() {
        return observacions;
    }

    public void setObservacions(String observacions) {
        this.observacions = observacions;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Usuaris getUsuari() {
        return usuari;
    }

    public void setUsuari(Usuaris usuari) {
        this.usuari = usuari;
    }
}
