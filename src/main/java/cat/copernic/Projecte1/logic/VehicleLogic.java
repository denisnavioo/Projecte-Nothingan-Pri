package cat.copernic.Projecte1.logic;

import cat.copernic.Projecte1.entity.Vehicle;
import cat.copernic.Projecte1.repository.VehicleRepository;
import java.util.List;
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

    public List<Vehicle> findAllProducts(){

        List<Vehicle> ret;

        ret = vehicleRepo.findAll();

        return ret;

    }

    public Long saveVehicle(Vehicle vehicle){

        Vehicle ret = vehicleRepo.save(vehicle);

        return ret.getId();

    }

    public void deleteVehicleById(Long id){

        vehicleRepo.deleteById(id);

    }

    public Vehicle getProductById(Long id){

        return vehicleRepo.findById(id).orElse(null);

    }

    public boolean existsById(Long id)
    {
        Vehicle p = vehicleRepo.findById(id).orElse(null);

        return (p != null);
    }
    
}

