package cat.copernic.Projecte1.logic;

import cat.copernic.Projecte1.entity.Enum.EstatReserva;
import cat.copernic.Projecte1.entity.Enum.EstatVehicle;
import cat.copernic.Projecte1.entity.Reserva;
import cat.copernic.Projecte1.entity.Usuaris;
import cat.copernic.Projecte1.entity.Vehicle;
import cat.copernic.Projecte1.repository.ReservaRepository;
import cat.copernic.Projecte1.repository.UsuarisRepository;
import cat.copernic.Projecte1.repository.VehicleRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Gestiona la lógica de las reservas.
@Service
public class ReservaLogic {

    // Repositorios necesarios para gestionar las reservas.
    private final ReservaRepository reservaRepo;
    private final VehicleRepository vehicleRepo;
    private final UsuarisRepository usuarisRepo;

    // Inicializa los repositorios.
    public ReservaLogic(
            ReservaRepository reservaRepo,
            VehicleRepository vehicleRepo,
            UsuarisRepository usuarisRepo) {
        this.reservaRepo = reservaRepo;
        this.vehicleRepo = vehicleRepo;
        this.usuarisRepo = usuarisRepo;
    }

    // Devuelve todas las reservas.
    public List<Reserva> findAllReservas() {
        return reservaRepo.findAll();
    }

    // Busca una reserva por su identificador.
    public Reserva getReservaById(Integer id) {
        return reservaRepo.findById(id).orElse(null);
    }

    // Comprueba si existe una reserva con ese identificador.
    public boolean existsById(Integer id) {
        return reservaRepo.existsById(id);
    }

    // RF31: comprueba la disponibilidad del vehículo.
    public boolean vehicleDisponible(
            Long vehicleId, LocalDateTime dataInici, LocalDateTime dataFi) {

        if (vehicleId == null || vehicleId <= 0
                || dataInici == null || dataFi == null
                || !dataFi.isAfter(dataInici)) {
            return false;
        }

        Vehicle vehicle = vehicleRepo.findById(vehicleId).orElse(null);

        if (vehicle == null
                || !Boolean.TRUE.equals(vehicle.getActiu())
                || vehicle.getEstatVehicle() != EstatVehicle.Disponible) {
            return false;
        }

        return reservaRepo.findReservesSolapades(
                vehicleId, dataInici, dataFi, EstatReserva.Anulada).isEmpty();
    }

    // Valida y guarda una nueva reserva.
    @Transactional
    public Integer saveReserva(Reserva reserva) {

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva no pot ser nul·la.");
        }

        // Impide modificar una reserva existente.
        if (reserva.getId() != null) {
            throw new IllegalArgumentException(
                    "No es poden modificar reserves existents.");
        }

        // Comprueba que las fechas sean válidas.
        if (reserva.getDataInici() == null
                || reserva.getDataFi() == null
                || !reserva.getDataFi().isAfter(reserva.getDataInici())) {
            throw new IllegalArgumentException(
                    "La data final ha de ser posterior a la inicial.");
        }

        // Comprueba que se hayan seleccionado vehículo y usuario.
        if (reserva.getVehicle() == null
                || reserva.getUsuari() == null
                || reserva.getVehicle().getId() <= 0
                || reserva.getUsuari().getId() == null) {
            throw new IllegalArgumentException(
                    "Cal seleccionar un vehicle i un usuari vàlids.");
        }

        // Recupera las entidades reales de la base de datos.
        Vehicle vehicle = vehicleRepo.findById(
                reserva.getVehicle().getId()).orElse(null);

        Usuaris usuari = usuarisRepo.findById(
                reserva.getUsuari().getId()).orElse(null);

        if (vehicle == null || usuari == null) {
            throw new IllegalArgumentException(
                    "El vehicle o l'usuari seleccionat no existeix.");
        }

        // Comprueba que el vehículo esté disponible.
        if (!Boolean.TRUE.equals(vehicle.getActiu())
                || vehicle.getEstatVehicle() != EstatVehicle.Disponible) {
            throw new IllegalArgumentException(
                    "El vehicle no està disponible per reservar.");
        }

        // Comprueba que el usuario esté validado.
        if (!"VALIDAT".equalsIgnoreCase(usuari.getEstatRegistre())) {
            throw new IllegalArgumentException(
                    "L'usuari ha d'estar validat per fer una reserva.");
        }

        // Comprueba que el precio diario sea válido.
        if (vehicle.getPreuDia() == null
                || vehicle.getPreuDia().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El vehicle no té un preu diari vàlid.");
        }

        // Comprueba que la fianza no sea negativa.
        if (reserva.getImportFianca() == null
                || reserva.getImportFianca().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "La fiança ha de ser igual o superior a zero.");
        }

        // Calcula los días de alquiler, redondeando hacia arriba.
        Duration durada = Duration.between(
                reserva.getDataInici(), reserva.getDataFi());

        long dies = durada.toDays();

        if (!durada.minusDays(dies).isZero()) {
            dies++;
        }

        // Calcula el precio total del alquiler.
        BigDecimal preuTotal = vehicle.getPreuDia()
                .multiply(BigDecimal.valueOf(dies))
                .setScale(2, RoundingMode.HALF_UP);

        reserva.setPreuLloguer(preuTotal);

        // Aplica un descuento del 10 % a la fianza de usuarios premium.
        BigDecimal fianca = reserva.getImportFianca();

        if (Boolean.TRUE.equals(usuari.getPremium())) {
            fianca = fianca.multiply(new BigDecimal("0.90"));
        }

        reserva.setImportFianca(
                fianca.setScale(2, RoundingMode.HALF_UP));

        // Genera un código único para la reserva.
        if (reserva.getCodiReserva() == null
                || reserva.getCodiReserva().isBlank()) {

            String codi;

            do {
                codi = "RES-" + UUID.randomUUID()
                        .toString().substring(0, 8).toUpperCase();
            } while (reservaRepo.existsByCodiReserva(codi));

            reserva.setCodiReserva(codi);

        } else if (reservaRepo.existsByCodiReserva(
                reserva.getCodiReserva())) {
            throw new IllegalArgumentException(
                    "Aquest codi de reserva ja existeix.");
        }

        // Evita reservas que coincidan en fechas para el mismo vehículo.
        List<Reserva> reserves
                = reservaRepo.findByVehicle_Id(vehicle.getId());

        for (Reserva existent : reserves) {
            if (existent.getEstat() == EstatReserva.Anulada) {
                continue;
            }

            boolean solapades
                    = reserva.getDataInici().isBefore(existent.getDataFi())
                    && reserva.getDataFi().isAfter(existent.getDataInici());

            if (solapades) {
                throw new IllegalArgumentException(
                        "El vehicle ja està reservat en aquestes dates.");
            }
        }

        // Asigna los datos finales y confirma la reserva.
        reserva.setVehicle(vehicle);
        reserva.setUsuari(usuari);
        reserva.setDataReserva(LocalDateTime.now());
        reserva.setEstat(EstatReserva.Confirmada);

        return reservaRepo.save(reserva).getId();
    }

    // Anula una reserva si todavía se puede cancelar.
    @Transactional
    public boolean anularReserva(Integer id) {
        Reserva reserva = getReservaById(id);

        if (reserva == null
                || reserva.getEstat() == EstatReserva.Lliurada
                || reserva.getEstat() == EstatReserva.Anulada) {
            return false;
        }

        reserva.setEstat(EstatReserva.Anulada);
        reservaRepo.save(reserva);

        return true;
    }

    public List<Reserva> obtenirInformeReserves(Integer clientId) {
        return reservaRepo.findReservesPerInforme(clientId);
    }
}
