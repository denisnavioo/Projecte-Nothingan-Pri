package cat.copernic.Projecte1.repository;

import cat.copernic.Projecte1.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author facu
 */
public interface VehicleRepository extends JpaRepository<Vehicle , Long>{
    
}
