package cat.copernic.Projecte1.logic;

import cat.copernic.Projecte1.entity.Enum.Categoria;
import cat.copernic.Projecte1.entity.Enum.EstatVehicle;
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

    /**
     * Método 1: Filtra la lista por Marca
     */
    public List<Vehicle> filtrarPerMarca(List<Vehicle> llista, String marca) {
        if (marca == null || marca.trim().isEmpty()) {
            return llista;
        }
        String marcaClean = marca.trim().toLowerCase();
        return llista.stream()
                .filter(v -> v.getMarca() != null && v.getMarca().toLowerCase().contains(marcaClean))
                .toList();
    }

    /**
     * Método 2: Filtra la lista por Categoria (comparación segura por nombre)
     */
    public List<Vehicle> filtrarPerCategoria(List<Vehicle> llista, Categoria categoria) {
        if (categoria == null) {
            return llista;
        }
        return llista.stream()
                .filter(v -> v.getCategoria() != null && v.getCategoria().name().equalsIgnoreCase(categoria.name()))
                .toList();
    }

    /**
     * Método 3: Filtra la lista por Estat del Vehicle (comparación segura por
     * nombre)
     */
    public List<Vehicle> filtrarPerEstat(List<Vehicle> llista, EstatVehicle estat) {
        if (estat == null) {
            return llista;
        }
        return llista.stream()
                .filter(v -> v.getEstatVehicle() != null && v.getEstatVehicle().name().equalsIgnoreCase(estat.name()))
                .toList();
    }

    /**
     * Método principal de filtrado
     */
    public List<Vehicle> findByFiltres(String matricula, String marca, Categoria categoria, EstatVehicle estat) {
        // 1. Obtenemos la lista base (por matrícula o todos)
        List<Vehicle> llista = findByMatricula(matricula);

        // 2. Aplicamos secuencialmente los 3 filtros
        llista = filtrarPerMarca(llista, marca);
        llista = filtrarPerCategoria(llista, categoria);
        llista = filtrarPerEstat(llista, estat);

        return llista;
    }
}
