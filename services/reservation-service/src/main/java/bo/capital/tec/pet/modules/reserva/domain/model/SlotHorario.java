package bo.capital.tec.pet.modules.reserva.domain.model;

import java.time.LocalTime;

/**
 * Intervalo horario libre que el proveedor puede atender para un servicio.
 * Value object inmutable de dominio.
 */
public record SlotHorario(LocalTime inicio, LocalTime fin) {
}
