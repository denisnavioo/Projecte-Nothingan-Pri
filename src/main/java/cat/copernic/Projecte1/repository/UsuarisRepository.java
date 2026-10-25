/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.Projecte1.repository;

import cat.copernic.Projecte1.entity.Usuaris;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Maxna
 */
public interface UsuarisRepository extends JpaRepository<Usuaris, Long>{
    
    // Método necesario para comprobar correos duplicados (RF09)
    Optional<Usuaris> findByEmail(String email);
    
    // Busqueda Dni parcial (sale dni exacto o equivalentes)
    List<Usuaris> findByDniContainingIgnoreCase(String dni);
    
}
