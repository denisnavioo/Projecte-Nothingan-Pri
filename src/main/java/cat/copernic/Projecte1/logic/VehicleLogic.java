package cat.copernic.Projecte1.logic;

import cat.copernic.Projecte1.entity.Vehicle;
import cat.copernic.Projecte1.repository.VehicleRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author facu
 */
@Service
public class VehicleLogic {

    @Autowired
    VehicleRepository vehicleRepo;

    public VehicleLogic(VehicleRepository vehicleRepo) {
        this.vehicleRepo = vehicleRepo;
    }

    public List<Vehicle> findAllVehicles() {

        List<Vehicle> ret;

        ret = vehicleRepo.findAll();

        return ret;

    }

    public Long saveVehicle(Vehicle vehicle) {

        Vehicle ret = vehicleRepo.save(vehicle);

        return ret.getId();

    }

    public void deleteVehicleById(Long id) {

        vehicleRepo.deleteById(id);

    }

    public Vehicle getVehicleById(Long id) {

        return vehicleRepo.findById(id).orElse(null);

    }

    public boolean existsById(Long id) {
        Vehicle p = vehicleRepo.findById(id).orElse(null);

        return (p != null);
    }

    public List<Vehicle> findByMatricula(String matricula) {
        if (matricula == null || matricula.trim().isEmpty()) {
            return vehicleRepo.findAll();
        }
        return vehicleRepo.findByMatriculaContainingIgnoreCase(matricula);
    }

    public boolean existsByMatricula(String matricula, long id) {
        if (matricula == null || matricula.trim().isEmpty()) {
            return false;
        }
        Optional<Vehicle> existing = vehicleRepo.findByMatriculaIgnoreCase(matricula.trim());
        if (existing.isPresent()) {
            // Si es un alta (id 0 o null) o el ID encontrado no coincide con el actual:
            return id == 0 || existing.get().getId() != id;
        }
        return false;
    }
}
