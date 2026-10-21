package cat.copernic.Projecte1.repository;

import cat.copernic.Projecte1.entity.Reserva;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Alejandro Lorente
 */
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    // Comprova si el codi de reserva ja existeix.
    boolean existsByCodiReserva(String codiReserva);

    // Busca les reserves d'un vehicle.
    List<Reserva> findByVehicle_Id(long vehicleId);
}
