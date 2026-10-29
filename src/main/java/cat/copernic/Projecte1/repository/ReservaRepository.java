package cat.copernic.Projecte1.repository;

import cat.copernic.Projecte1.entity.Enum.EstatReserva;
import cat.copernic.Projecte1.entity.Reserva;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Gestiona las consultas de reservas en la base de datos.
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    // Comprueba si el código de reserva ya existe.
    boolean existsByCodiReserva(String codiReserva);

    // Busca las reservas de un vehículo.
    List<Reserva> findByVehicle_Id(long vehicleId);
    
    // Busca las reservas de un vehículo que coinciden con unas fechas.
    @Query("SELECT r FROM Reserva r WHERE r.vehicle.id = :vehicleId "
            + "AND r.estat <> :anulada "
            + "AND r.dataInici < :dataFi "
            + "AND r.dataFi > :dataInici")
    List<Reserva> findReservesSolapades(
            @Param("vehicleId") long vehicleId,
            @Param("dataInici") LocalDateTime dataInici,
            @Param("dataFi") LocalDateTime dataFi,
            @Param("anulada") EstatReserva anulada);
}
