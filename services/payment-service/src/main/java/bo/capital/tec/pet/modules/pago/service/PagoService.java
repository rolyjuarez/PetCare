package bo.capital.tec.pet.modules.pago.service;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.modules.pago.dto.PagoResponseDTO;
import bo.capital.tec.pet.modules.pago.dto.ProcesarPagoRequestDTO;
import bo.capital.tec.pet.modules.pago.event.ReservaConfirmadaEvent;

public interface PagoService {

    PagoResponseDTO crearDesdeReservaConfirmada(ReservaConfirmadaEvent event);

    PagoResponseDTO getById(Long id);

    PagoResponseDTO getByReservaId(Long reservaId);

    PagedResponse<PagoResponseDTO> listByReservaId(Long reservaId, int page, int size);

    PagoResponseDTO procesar(Long id, ProcesarPagoRequestDTO request);

    PagoResponseDTO reembolsar(Long id);
}
