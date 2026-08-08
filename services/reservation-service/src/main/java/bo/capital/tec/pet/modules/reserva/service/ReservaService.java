package bo.capital.tec.pet.modules.reserva.service;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.modules.reserva.command.CancelarReservaCommand;
import bo.capital.tec.pet.modules.reserva.command.ConfirmarReservaCommand;
import bo.capital.tec.pet.modules.reserva.command.RechazarReservaCommand;
import bo.capital.tec.pet.modules.reserva.dto.DisponibilidadSlotsDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaRequestDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaSummaryDTO;

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

    ReservaResponseDTO aplicarAceptacion(ConfirmarReservaCommand comando);

    ReservaResponseDTO aplicarRechazo(RechazarReservaCommand comando);

    ReservaResponseDTO compensarPorPagoFallido(CancelarReservaCommand comando);
}
