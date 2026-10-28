package cat.copernic.Projecte1.repository;


import cat.copernic.Projecte1.entity.Enum.EstatVehicle;
import cat.copernic.Projecte1.entity.Vehicle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author facu
 */
public interface VehicleRepository extends JpaRepository<Vehicle , Long>{
    
    List<Vehicle> findByMatriculaContainingIgnoreCase(String matricula);
    
    Optional<Vehicle> findByMatriculaIgnoreCase(String matricula);
  
    List<Vehicle> findByEstatVehicleAndActiuTrue(EstatVehicle estatVehicle);
}
