package cat.copernic.Projecte1.logic;

import cat.copernic.Projecte1.entity.Enum.Categoria;
import cat.copernic.Projecte1.entity.Vehicle;
import cat.copernic.Projecte1.repository.VehicleRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleLogic {

    @Autowired
    private VehicleRepository vehicleRepo;

    @Autowired
    private ReservaLogic reservaLogic;

    public VehicleLogic(VehicleRepository vehicleRepo) {
        this.vehicleRepo = vehicleRepo;
    }

    public List<Vehicle> findAllVehicles() {
        return vehicleRepo.findAll();
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

    public List<Vehicle> findByMatricula(String matricula) {
        if (matricula == null || matricula.trim().isEmpty()) {
            return vehicleRepo.findAll();
        }
        return vehicleRepo.findByMatriculaContainingIgnoreCase(matricula.trim());
    }

    public boolean existsByMatricula(String matricula, long id) {
        if (matricula == null || matricula.trim().isEmpty()) {
            return false;
        }
        Optional<Vehicle> existing = vehicleRepo.findByMatriculaIgnoreCase(matricula.trim());
        return existing.isPresent() && (id == 0 || existing.get().getId() != id);
    }

    /**
     * Filtratge combinat (sense EstatVehicle)
     */
    public List<Vehicle> findByFiltres(String matricula, String marca, String model,
            Integer anyFabricacio, Categoria categoria,
            LocalDateTime dataInici, LocalDateTime dataFi) {

        List<Vehicle> base = findByMatricula(matricula);

        String marcaClean = (marca != null && !marca.trim().isEmpty()) ? marca.trim().toLowerCase() : null;
        String modelClean = (model != null && !model.trim().isEmpty()) ? model.trim().toLowerCase() : null;
        boolean validarFechas = (dataInici != null && dataFi != null && dataFi.isAfter(dataInici));

        return base.stream()
                .filter(v -> marcaClean == null || (v.getMarca() != null && v.getMarca().toLowerCase().contains(marcaClean)))
                .filter(v -> modelClean == null || (v.getModel() != null && v.getModel().toLowerCase().contains(modelClean)))
                .filter(v -> anyFabricacio == null || anyFabricacio <= 0 || anyFabricacio.equals(v.getAnyFabricacio()))
                .filter(v -> categoria == null || (v.getCategoria() != null && v.getCategoria().name().equalsIgnoreCase(categoria.name())))
                .filter(v -> !validarFechas || reservaLogic.vehicleDisponible(v.getId(), dataInici, dataFi))
                .toList();
    }
}
