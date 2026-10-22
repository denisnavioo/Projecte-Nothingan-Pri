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
            // Només inserta si la base de dades està buida
            if (usuarisRepo.count() == 0) {
                // Usuari 1
                Usuaris u1 = new Usuaris();
                u1.setNom("Denis Navio");
                u1.setEmail("dnavio@alumnat.copernic.cat");
                u1.setPassword("123456");
                u1.setTelefon(600000000);
                
                // Si vas afegir els camps DNI i estatRegistre a l'entitat:
                // u1.setDni("12345678A");
                // u1.setEstatRegistre("Validat");

                // Usuari 2
                Usuaris u2 = new Usuaris();
                u2.setNom("Facundo Jahegi");
                u2.setEmail("fjahegi@alumnat.copernic.cat");
                u2.setPassword("qwerty");
                u2.setTelefon(611111111);

                // Si vas afegir els camps DNI i estatRegistre a l'entitat:
                // u2.setDni("87654321B");
                // u2.setEstatRegistre("Pendent");

                usuarisRepo.save(u1);
                usuarisRepo.save(u2);

                System.out.println("http://localhost:8080/web/usuaris/login");
            }
        };
    }
}
