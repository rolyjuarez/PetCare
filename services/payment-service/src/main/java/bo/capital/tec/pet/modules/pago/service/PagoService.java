package bo.capital.tec.pet.modules.pago.service;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.modules.pago.command.CrearPagoCommand;
import bo.capital.tec.pet.modules.pago.dto.PagoResponseDTO;
import bo.capital.tec.pet.modules.pago.dto.ProcesarPagoRequestDTO;

public interface PagoService {

    PagoResponseDTO crearPago(CrearPagoCommand comando);

    PagoResponseDTO getById(Long id);

    PagoResponseDTO getByReservaId(Long reservaId);

    PagedResponse<PagoResponseDTO> listByReservaId(Long reservaId, int page, int size);

    PagoResponseDTO procesar(Long id, ProcesarPagoRequestDTO request);

    PagoResponseDTO reembolsar(Long id);

    void liberarDescuento(Long reservaId);
}
