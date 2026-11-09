/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.Projecte1.logic;

import cat.copernic.Projecte1.entity.Usuaris;
import cat.copernic.Projecte1.entity.Enum.rol;
import cat.copernic.Projecte1.repository.UsuarisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarisRepository usuarisRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuaris usuari = usuarisRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuari no trobat: " + email));

        String nomRol = (usuari.getRol() != null) ? usuari.getRol().name() : "CLIENT";

        return new User(
                usuari.getEmail(),
                usuari.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + nomRol))
        );
    }
}