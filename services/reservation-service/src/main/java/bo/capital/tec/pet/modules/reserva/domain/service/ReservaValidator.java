package bo.capital.tec.pet.modules.reserva.domain.service;

import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.modules.reserva.domain.model.Disponibilidad;
import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.dto.RegistroVacunacionInfoDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Reglas de negocio del dominio de reservas, puras y sin dependencias externas.
 *
 * <p>Cada método valida una regla y lanza {@link BusinessException} cuando se
 * incumple. Los datos necesarios (reservas ocupadas, disponibilidades, etc.)
 * deben ser provistos por el llamador, de modo que la lógica sea fácilmente
 * testeable de forma unitaria.</p>
 */
@Component
public class ReservaValidator {

    private static final String MODALIDAD_ESTABLECIMIENTO = "EN_ESTABLECIMIENTO";

    /**
     * Valida que no exista una reserva activa para la misma mascota y servicio
     * en la misma fecha con horario solapado.
     */
    public void validarSinReservaActiva(List<Reserva> activas, Long mascotaId, Long servicioId,
                                        LocalDate fechaInicio, LocalDate fechaFin,
                                        LocalTime horaInicio, LocalTime horaFin) {
        boolean solapa = activas.stream()
                .filter(r -> r.getMascotaId().equals(mascotaId) && r.getServicioId().equals(servicioId))
                .filter(r -> r.getFechaInicio().equals(fechaInicio) || r.getFechaInicio().equals(fechaFin))
                .anyMatch(r -> solapaHorarios(r, horaInicio, horaFin));
        if (solapa) {
            throw new BusinessException("Ya existe una reserva activa para esa mascota en el horario solicitado");
        }
    }

    /**
     * Valida que el horario solicitado no esté ocupado por otra reserva del
     * proveedor y que esté dentro de su disponibilidad declarada.
     */
    public void validarSlotProveedor(List<Reserva> booked, List<Disponibilidad> windows,
                                     LocalDate fecha, LocalTime inicio, LocalTime fin) {
        boolean tomado = booked.stream()
                .anyMatch(r -> r.getFechaInicio().equals(fecha) && solapaHorarios(r, inicio, fin));
        if (tomado) {
            throw new BusinessException("El proveedor ya tiene una reserva en ese horario");
        }
        if (!windows.isEmpty() && !cubiertoPorDisponibilidad(windows, fecha, inicio, fin)) {
            throw new BusinessException("El horario seleccionado no está dentro de la disponibilidad del proveedor");
        }
    }

    /**
     * Valida la modalidad de entrega y la devuelve normalizada con el valor por
     * defecto. Exige coordenadas cuando la modalidad no es en establecimiento.
     */
    public String validarModalidad(boolean modalidadValida, String modalidad,
                                   BigDecimal latitud, BigDecimal longitud) {
        if (!modalidadValida) {
            throw new BusinessException("La modalidad " + modalidad + " no está disponible para el servicio seleccionado");
        }
        if (!MODALIDAD_ESTABLECIMIENTO.equals(modalidad) && (latitud == null || longitud == null)) {
            throw new BusinessException("Para la modalidad " + modalidad + " debe indicar la latitud y longitud del lugar");
        }
        return modalidad;
    }

    /**
     * Valida el certificado de vacunación cuando el servicio lo requiere.
     *
     * @param requiere true si el servicio exige certificado
     * @param rv       registro de vacunación aportado (null si no se adjuntó)
     * @param mascotaId mascota dueña de la reserva
     */
    public void validarCertificado(boolean requiere, RegistroVacunacionInfoDTO rv, Long mascotaId) {
        if (requiere && rv == null) {
            throw new BusinessException("El servicio requiere adjuntar un certificado de vacunación");
        }
        if (rv == null) {
            return;
        }
        if (!rv.getMascotaId().equals(mascotaId)) {
            throw new BusinessException("El registro de vacunación no pertenece a la mascota seleccionada");
        }
        if (requiere && (rv.getCertificadoUrl() == null || rv.getCertificadoUrl().isBlank())) {
            throw new BusinessException("Debe adjuntar el certificado de vacunación");
        }
    }

    private boolean cubiertoPorDisponibilidad(List<Disponibilidad> windows, LocalDate fecha,
                                              LocalTime inicio, LocalTime fin) {
        int diaSemana = fecha.getDayOfWeek().getValue() % 7;
        return windows.stream()
                .filter(w -> Integer.valueOf(diaSemana).equals(w.getDiaSemana()))
                .anyMatch(w -> !inicio.isBefore(w.getHoraInicio()) && !fin.isAfter(w.getHoraFin()));
    }

    public boolean solapaHorarios(Reserva existente, LocalTime inicio, LocalTime fin) {
        LocalTime finReal = fin != null ? fin : inicio.plusHours(1);
        LocalTime existenteInicio = existente.getHoraInicio();
        LocalTime existenteFin = existente.getHoraFin() != null ? existente.getHoraFin() : existenteInicio.plusHours(1);
        return inicio.isBefore(existenteFin) && finReal.isAfter(existenteInicio);
    }
}
