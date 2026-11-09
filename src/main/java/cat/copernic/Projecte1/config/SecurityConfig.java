/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.Projecte1.config;

import cat.copernic.Projecte1.repository.UsuarisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UsuarisRepository usuarisRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                // 1. Rutes PÚBLIQUES (Fitxers estàtics, login, registre, Home i cerca de cotxes)
                .requestMatchers(
                        "/", 
                        "/home", 
                        "/css/**", 
                        "/js/**", 
                        "/images/**", 
                        "/uploads/**", 
                        "/assets/**",
                        "/web/usuaris/registre", 
                        "/web/usuaris/login",
                        "/coches/**", 
                        "/web/coches/**", 
                        "/api/coches/**"
                ).permitAll()
                
                // 2. Rutes protegides per Rol
                .requestMatchers("/web/admin/**", "/web/usuaris/pendents").hasRole("ADMIN")
                .requestMatchers("/web/usuaris/client/**").hasRole("CLIENT")
                
                // 3. Qualsevol altra sol·licitud requereix estar autenticat
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/web/usuaris/login")
                .loginProcessingUrl("/web/usuaris/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(customSuccessHandler()) // <-- REDIRECCIÓ DINÀMICA PER ROL
                .permitAll()
                )
                .logout(logout -> logout
                .logoutUrl("/web/usuaris/logout")
                .logoutSuccessUrl("/web/usuaris/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
                );

        return http.build();
    }

    // Handler personalitzat per decidir la ruta d'entrada segons el Rol
    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            String email = authentication.getName(); // Obtenim l'email de l'usuari autenticat

            // Guardem l'objecte Usuaris a la sessió HTTP per a que ClientController el trobi
            usuarisRepository.findByEmail(email).ifPresent(usuari -> {
                request.getSession().setAttribute("usuariLoguejat", usuari);
            });

            var authorities = authentication.getAuthorities();
            String redirectUrl = "/";

            for (var authority : authorities) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    redirectUrl = "/web/usuaris/all";
                    break;
                } else if (authority.getAuthority().equals("ROLE_AGENT")) {
                    redirectUrl = "/web/usuaris/all";
                    break;
                } else if (authority.getAuthority().equals("ROLE_CLIENT")) {
                    redirectUrl = "/web/usuaris/client/home"; // Ruta d'entrada del client
                    break;
                }
            }

            response.sendRedirect(redirectUrl);
        };
    }
}