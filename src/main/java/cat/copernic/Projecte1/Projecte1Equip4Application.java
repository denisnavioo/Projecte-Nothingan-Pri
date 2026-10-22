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

           

                Usuaris u1 = new Usuaris();
                u1.setNom("Alejandro Lorente");
                u1.setEmail("alorente@alumnat.copernic.cat");
                u1.setPassword("1212");
                u1.setTelefon(765432);
                u1.setEstatRegistre("VALIDAT");
                u1.setDni("22");

                Usuaris u2 = new Usuaris();
                u2.setNom("Facundo jaeggi");
                u2.setEmail("fjaeggi@alumnat.copernic.cat");
                u2.setPassword("1212");
                u2.setTelefon(54152);
                u2.setEstatRegistre("VALIDAT");
                u2.setDni("44");

                Usuaris u3 = new Usuaris();
                u3.setNom("Denis Navio");
                u3.setEmail("dnavio@alumnat.copernic.cat");
                u3.setPassword("1212");
                u3.setTelefon(600000000);
                u3.setEstatRegistre("VALIDAT");
                u3.setDni("11");

                usuarisRepo.save(u1);
                usuarisRepo.save(u2);
                usuarisRepo.save(u3);

            
            System.out.println("\n----------------------------------------------------------");
            System.out.println(" Aplicació iniciada correctament!");
            System.out.println(" Accés al login: http://localhost:8080/web/usuaris/login");
            System.out.println("----------------------------------------------------------\n");
        };
    }
}
