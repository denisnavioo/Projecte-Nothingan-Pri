/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.Projecte1.repository;

import cat.copernic.Projecte1.entity.Usuaris;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Maxna
 */
public interface UsuarisRepository extends JpaRepository<Usuaris, Long> {

    //Optional ESTRICTO List PARCIAL
    // Método necesario para comprobar correos duplicados (RF09)
    // hace una llamada a la bbdd donde le pasa el atributo entre parentesis y busca las coincidencias
    //con los usuarios y devuelve una lista de usuarios con sus atributos y todo lo que contengan  
    Optional<Usuaris> findByEmail(String email);

    // Busqueda Dni parcial (sale dni exacto o equivalentes)
    // hace una llamada a la bbdd donde le pasa el atributo entre parentesis y busca las coincidencias
    //con los usuarios y devuelve un usuario con sus atributos y todo lo que contengan  
    List<Usuaris> findByDniContainingIgnoreCase(String dni);

    List<Usuaris> findByEmailContainingIgnoreCase(String email);

    List<Usuaris> findByEstatRegistre(String estatRegistre);

    List<Usuaris> findByRol(String rol);

    Optional<Usuaris> findByTelefon(String telefon);

    Optional<Usuaris> findByDni(String dni);

    @Query("SELECT u FROM Usuaris u WHERE "
            + "(:email IS NULL OR :email = '' OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND "
            + "(:nacionalitat IS NULL OR :nacionalitat = '' OR LOWER(u.nacionalitat) LIKE LOWER(CONCAT('%', :nacionalitat, '%'))) AND "
            + "(:poblacio IS NULL OR :poblacio = '' OR LOWER(u.poblacio) LIKE LOWER(CONCAT('%', :poblacio, '%'))) AND "
            + "(:dni IS NULL OR :dni = '' OR LOWER(u.dni) LIKE LOWER(CONCAT('%', :dni, '%'))) AND "
            + "(:nom IS NULL OR :nom = '' OR LOWER(u.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND "
            + "(:telefon IS NULL OR :telefon = '' OR u.telefon LIKE CONCAT('%', :telefon, '%'))")
    List<Usuaris> findClientByFiltreMultiple(
            @Param("email") String email,
            @Param("nacionalitat") String nacionalitat,
            @Param("poblacio") String poblacio,
            @Param("dni") String dni,
            @Param("nom") String nom,
            @Param("telefon") String telefon
    );

}
