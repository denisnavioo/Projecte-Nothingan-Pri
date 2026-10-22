package cat.copernic.Projecte1.repository;


import cat.copernic.Projecte1.entity.Vehicle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author facu
 */
public interface VehicleRepository extends JpaRepository<Vehicle , Long>{
    
    // Método para coincidencia parcial (ej. buscar "123" encuentra "1234ABC")
    List<Vehicle> findByMatriculaContainingIgnoreCase(String matricula);

    // O si solo quieres coincidencia exacta, usa:
    // Optional<Vehicle> findByMatricula(String matricula);
    
}
