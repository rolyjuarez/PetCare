package bo.capital.tec.pet.modules.reserva.application.service;

import java.time.LocalDate;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.reserva.dto.ReservaRequestDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaSummaryDTO;

public interface ReservaService {
    ReservaResponseDTO create(ReservaRequestDTO dto);
    ReservaResponseDTO getById(Long id);
    PagedResponse<ReservaSummaryDTO> getAll(Long clienteId, Long mascotaId, Long servicioId,
            String estado, LocalDate fechaDesde, LocalDate fechaHasta, int page, int size);
    ReservaResponseDTO update(Long id, ReservaRequestDTO dto);
    void delete(Long id);
    ReservaResponseDTO confirmar(Long id);
    ReservaResponseDTO rechazar(Long id, String motivo);
    ReservaResponseDTO iniciar(Long id);
    ReservaResponseDTO completar(Long id);
    ReservaResponseDTO cancelar(Long id, String motivo);
    ReservaResponseDTO asignarProveedor(Long id, Long proveedorId);
}
