package cat.copernic.Projecte1.entity;

import cat.copernic.Projecte1.entity.Enum.Categoria;
import cat.copernic.Projecte1.entity.Enum.EstatVehicle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;

/**
 *
 * @author facu
 */
@Entity

public class Vehicle {
    

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Id
    @Column(name = "Matricula", nullable = false, unique = true, length = 15)
    private String matricula;

    @Column(name = "Marca", nullable = false, length = 50)
    private String marca;

    @Column(name = "Model", nullable = false, length = 50)
    private String model;

    @Column(name = "Any_Fabricacio")
    private int anyFabricacio;

    @Enumerated(EnumType.STRING)
    @Column(name = "Categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "Preu_Dia", nullable = false, precision = 5, scale = 2)
    private BigDecimal preuDia;

    @Enumerated(EnumType.STRING)
    @Column(name = "Estat_Vehicle", nullable = false)
    private EstatVehicle estatVehicle = EstatVehicle.Disponible;

    @Column(name = "Actiu")
    private Boolean actiu = true;

    @Column(name = "Foto_vehicle", length = 255)
    private String fotoVehicle;

    public Vehicle() {
    }

    public Vehicle(long id, String matricula, String marca, String model, int anyFabricacio, Categoria categoria, BigDecimal preuDia, String fotoVehicle) {
        this.id = id;
        this.matricula = matricula;
        this.marca = marca;
        this.model = model;
        this.anyFabricacio = anyFabricacio;
        this.categoria = categoria;
        this.preuDia = preuDia;
        this.fotoVehicle = fotoVehicle;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getAnyFabricacio() {
        return anyFabricacio;
    }

    public void setAnyFabricacio(int anyFabricacio) {
        this.anyFabricacio = anyFabricacio;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPreuDia() {
        return preuDia;
    }

    public void setPreuDia(BigDecimal preuDia) {
        this.preuDia = preuDia;
    }

    public EstatVehicle getEstatVehicle() {
        return estatVehicle;
    }

    public void setEstatVehicle(EstatVehicle estatVehicle) {
        this.estatVehicle = estatVehicle;
    }

    public Boolean getActiu() {
        return actiu;
    }

    public void setActiu(Boolean actiu) {
        this.actiu = actiu;
    }

    public String getFotoVehicle() {
        return fotoVehicle;
    }

    public void setFotoVehicle(String fotoVehicle) {
        this.fotoVehicle = fotoVehicle;
    }
    
}
