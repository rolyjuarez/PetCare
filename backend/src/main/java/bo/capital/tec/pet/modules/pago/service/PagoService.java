package bo.capital.tec.pet.modules.pago.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.pago.dto.PagoRequestDTO;
import bo.capital.tec.pet.modules.pago.dto.PagoResponseDTO;
import bo.capital.tec.pet.modules.pago.dto.PagoSummaryDTO;

import java.time.LocalDate;

public interface PagoService {
    PagoResponseDTO create(PagoRequestDTO dto);
    PagoResponseDTO getById(Long id);
    PagedResponse<PagoSummaryDTO> getAll(Long reservaId, Long clienteId, String metodoPago,
            String estado, LocalDate fechaDesde, LocalDate fechaHasta, int page, int size);
    PagoResponseDTO update(Long id, PagoRequestDTO dto);
    void delete(Long id);
    PagoResponseDTO confirmarPago(Long id);
    PagoResponseDTO reembolsar(Long id, String motivo);
}
