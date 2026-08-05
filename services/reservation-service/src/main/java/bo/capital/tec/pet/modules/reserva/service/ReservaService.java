package bo.capital.tec.pet.modules.reserva.service;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.modules.reserva.dto.DisponibilidadSlotsDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaRequestDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaSummaryDTO;
import bo.capital.tec.pet.modules.reserva.event.PagoFallidoEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaAceptadaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaRechazadaEvent;

import java.time.LocalDate;
import java.util.List;

public interface ReservaService {

    ReservaResponseDTO create(ReservaRequestDTO dto);

    List<DisponibilidadSlotsDTO> getSlots(Long proveedorId, Long servicioId,
                                          LocalDate desde, LocalDate hasta, Long excluirReservaId);

    ReservaResponseDTO getById(Long id);

    PagedResponse<ReservaSummaryDTO> getAll(Long clienteId, Long mascotaId, Long servicioId,
                                            String estado, LocalDate fechaDesde, LocalDate fechaHasta,
                                            int page, int size);

    PagedResponse<ReservaSummaryDTO> getByClienteId(Long clienteId, int page, int size);

    PagedResponse<ReservaSummaryDTO> getByProveedorId(Long proveedorId, int page, int size);

    ReservaResponseDTO update(Long id, ReservaRequestDTO dto);

    void delete(Long id);

    ReservaResponseDTO cancelar(Long id, String motivo);

    ReservaResponseDTO aplicarAceptacion(ReservaAceptadaEvent event);

    ReservaResponseDTO aplicarRechazo(ReservaRechazadaEvent event);

    ReservaResponseDTO compensarPorPagoFallido(PagoFallidoEvent event);
}
