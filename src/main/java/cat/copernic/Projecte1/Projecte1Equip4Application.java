package cat.copernic.Projecte1;

import cat.copernic.Projecte1.entity.Enum.Categoria;
import cat.copernic.Projecte1.entity.Enum.EstatVehicle;
import cat.copernic.Projecte1.entity.Usuaris;
import cat.copernic.Projecte1.entity.Vehicle;
import cat.copernic.Projecte1.repository.UsuarisRepository;
import cat.copernic.Projecte1.repository.VehicleRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Projecte1Equip4Application {

    public static void main(String[] args) {
        SpringApplication.run(Projecte1Equip4Application.class, args);
    }

    @Bean
    public CommandLineRunner initData(UsuarisRepository usuarisRepo, VehicleRepository vehicleRepo) {
        return args -> {

            crearUsuariSiNoExisteix(usuarisRepo, "Alejandro Lorente", "alorente@alumnat.copernic.cat", "1212", "384639013", "VALIDAT", "74827465H", false, "AGENT");
            crearUsuariSiNoExisteix(usuarisRepo, "Facundo jaeggi", "fjaeggi@alumnat.copernic.cat", "1212", " 456789032", "VALIDAT", "74864465N", false, "ADMIN");
            crearUsuariSiNoExisteix(usuarisRepo, "Denis Navio", "dnavio@alumnat.copernic.cat", "1212", "234567890", "VALIDAT", "74827665T", false, "AGENT");
            crearUsuariSiNoExisteix(usuarisRepo, "Carolina Macià", "cemacia@alumnat.copernic.cat", "1212", "600112233", "VALIDAT", "99887766A", true, "CLIENT");

            crearVehicleSiNoExisteix(vehicleRepo, "3343 DGY", "Seat", "Altea", 2005, Categoria.Monovolum, new BigDecimal("24.56"), EstatVehicle.Disponible, "https://images.unsplash.com/photo-150.0530855697-b586d89ba3ee");
            crearVehicleSiNoExisteix(vehicleRepo, "1123 FRG", "Seat", "Leon", 2014, Categoria.Compacte, new BigDecimal("31.43"), EstatVehicle.En_manteniment, "https://images.unsplash.com/photo-150.0530855697-b586d89ba3ee");
            crearVehicleSiNoExisteix(vehicleRepo, "4312 NGR", "Nissan", "Micra", 2025, Categoria.Utilitari, new BigDecimal("34.76"), EstatVehicle.Disponible, "https://images.unsplash.com/photo-150.0530855697-b586d89ba3ee");
            crearVehicleSiNoExisteix(vehicleRepo, "8162 GWX", "Mazda", "2", 2010, Categoria.Compacte, new BigDecimal("35.12"), EstatVehicle.Disponible, "https://images.unsplash.com/photo-150.0530855697-b586d89ba3ee");
            crearVehicleSiNoExisteix(vehicleRepo, "4365 FDE", "Mercedes", "Vito", 2006, Categoria.Furgoneta, new BigDecimal("40.65"), EstatVehicle.En_manteniment, "https://images.unsplash.com/photo-150.0530855697-b586d89ba3ee");
            crearVehicleSiNoExisteix(vehicleRepo, "5821 LKM", "Volkswagen", "Touran", 2010, Categoria.Monovolum, new BigDecimal("35.00"), EstatVehicle.Disponible, "https://images.unsplash.com/photo-1549317661-bd32c8ce0db2"
            );
            System.out.println("\n----------------------------------------------------------");
            System.out.println(" Aplicació iniciada correctament!");
            System.out.println(" Accés al login: http://localhost:8080/web/usuaris/login");
            System.out.println("----------------------------------------------------------\n");
        };
    }

    private void crearUsuariSiNoExisteix(UsuarisRepository repo, String nom, String email, String password, String telefon, String estat, String dni, boolean premium, String rol) {
        if (repo.findByEmail(email).isEmpty() && repo.findByDni(dni).isEmpty()) {
            Usuaris u = new Usuaris();
            u.setNom(nom);
            u.setEmail(email);
            u.setPassword(password);
            u.setTelefon(telefon);
            u.setEstatRegistre(estat);
            u.setDni(dni);
            u.setPremium(premium);
            u.setRol(rol);
            repo.save(u);
            System.out.println("Usuari creat: " + email + " [Rol: " + rol + "]");
        } else {
            System.out.println("L'usuari ja existeix a la BD: " + email);
        }
    }

    private void crearVehicleSiNoExisteix(VehicleRepository repo, String matricula,
            String marca, String model, int anyFabricacio, Categoria categoria, BigDecimal preuDia, EstatVehicle estatVehicle, String fotoVehicle) {
        if (repo.findByMatriculaIgnoreCase(matricula).isEmpty()) {
            Vehicle v = new Vehicle();
            v.setMatricula(matricula);
            v.setMarca(marca);
            v.setModel(model);
            v.setAnyFabricacio(anyFabricacio);
            v.setCategoria(categoria);
            v.setPreuDia(preuDia);
            v.setEstatVehicle(estatVehicle);
            v.setFotoVehicle(fotoVehicle);
            repo.save(v);
            System.out.println("Vehicle creat: " + matricula + " [Marca: " + marca + "]");
        } else {
            System.out.println("El vehicle ja existeix a la BD: " + matricula);
        }
    }
}
