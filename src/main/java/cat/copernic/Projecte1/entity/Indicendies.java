/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.Projecte1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Maxna
 */
@Entity
public class Indicendies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate Data_Hora;
    private int Quilometratge;
    private String Descripcio;
    private String Resolucio;
    private Enum Tipus;
    private Double Importe;
    private String Foto_incidencia;

    public Indicendies() {
    }

    public Indicendies(long id, LocalDate Data_Hora, int Quilometratge, String Descripcio, String Resolucio, Enum Tipus, Double Importe,  String Foto_incidencia) {
        this.id = id;
        this.Data_Hora = Data_Hora;
        this.Quilometratge = Quilometratge;
        this.Descripcio = Descripcio;
        this.Resolucio = Resolucio;
        this.Tipus = Tipus;
        this.Importe = Importe;
        this.Foto_incidencia = Foto_incidencia;
    }

    public Indicendies(LocalDate Data_Hora, int Quilometratge, String Descripcio, String Resolucio, Enum Tipus, Double Importe, String Foto_incidencia) {
        this.Data_Hora = Data_Hora;
        this.Quilometratge = Quilometratge;
        this.Descripcio = Descripcio;
        this.Resolucio = Resolucio;
        this.Tipus = Tipus;
        this.Importe = Importe;
        this.Foto_incidencia = Foto_incidencia;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getData_Hora() {
        return Data_Hora;
    }

    public void setData_Hora(LocalDate Data_Hora) {
        this.Data_Hora = Data_Hora;
    }

    public int getQuilometratge() {
        return Quilometratge;
    }

    public void setQuilometratge(int Quilometratge) {
        this.Quilometratge = Quilometratge;
    }

    public String getDescripcio() {
        return Descripcio;
    }

    public void setDescripcio(String Descripcio) {
        this.Descripcio = Descripcio;
    }

    public String getResolucio() {
        return Resolucio;
    }

    public void setResolucio(String Resolucio) {
        this.Resolucio = Resolucio;
    }

    public Enum getTipus() {
        return Tipus;
    }

    public void setTipus(Enum Tipus) {
        this.Tipus = Tipus;
    }

    public Double getImporte() {
        return Importe;
    }

    public void setImporte(Double Importe) {
        this.Importe = Importe;
    }

    public String getFoto_incidencia() {
        return Foto_incidencia;
    }

    public void setFoto_incidencia(String Foto_incidencia) {
        this.Foto_incidencia = Foto_incidencia;
    }
    
    
    

}
