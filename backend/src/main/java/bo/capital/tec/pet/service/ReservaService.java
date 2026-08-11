package bo.capital.tec.pet.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.ReservaRequestDTO;
import bo.capital.tec.pet.dto.ReservaResponseDTO;
import bo.capital.tec.pet.dto.ReservaSummaryDTO;

import java.time.LocalDate;

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
}
