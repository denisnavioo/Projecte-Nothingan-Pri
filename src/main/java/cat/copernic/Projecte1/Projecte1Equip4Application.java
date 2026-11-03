package cat.copernic.Projecte1;

import cat.copernic.Projecte1.entity.Usuaris;
import cat.copernic.Projecte1.repository.UsuarisRepository;
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
    public CommandLineRunner initData(UsuarisRepository usuarisRepo) {
        return args -> {

            crearUsuariSiNoExisteix(usuarisRepo, "Alejandro Lorente", "alorente@alumnat.copernic.cat", "1212", "384639013", "VALIDAT", "74827465H", false, "AGENT");
            crearUsuariSiNoExisteix(usuarisRepo, "Facundo jaeggi", "fjaeggi@alumnat.copernic.cat", "1212"," 456789032", "VALIDAT", "74864465N", false, "ADMIN");
            crearUsuariSiNoExisteix(usuarisRepo, "Denis Navio", "dnavio@alumnat.copernic.cat", "1212", "234567890", "VALIDAT", "74827665T", false, "AGENT");
            crearUsuariSiNoExisteix(usuarisRepo, "Carolina Macià", "cemacia@alumnat.copernic.cat", "1212", "600112233", "VALIDAT", "99887766A", true, "CLIENT");

            System.out.println("\n----------------------------------------------------------");
            System.out.println(" Aplicació iniciada correctament!");
            System.out.println(" Accés al login: http://localhost:8080/web/usuaris/login");
            System.out.println("----------------------------------------------------------\n");
        };
    }

    private void crearUsuariSiNoExisteix(UsuarisRepository repo, String nom, String email, String password, String telefon, String estat, String dni, boolean premium) {
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
}
