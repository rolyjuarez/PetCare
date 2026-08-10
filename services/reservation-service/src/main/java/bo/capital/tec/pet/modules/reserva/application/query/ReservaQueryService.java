package bo.capital.tec.pet.modules.reserva.application.query;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.modules.reserva.dto.DisponibilidadSlotsDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaSummaryDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Casos de uso del lado lectura (CQRS read side) del módulo de reservas.
 */
public interface ReservaQueryService {

    List<DisponibilidadSlotsDTO> getSlots(Long proveedorId, Long servicioId,
                                          LocalDate desde, LocalDate hasta, Long excluirReservaId);

    ReservaResponseDTO getById(Long id);

    PagedResponse<ReservaSummaryDTO> getAll(Long clienteId, Long mascotaId, Long servicioId,
                                            String estado, LocalDate fechaDesde, LocalDate fechaHasta,
                                            int page, int size);
}
