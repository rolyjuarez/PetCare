package bo.capital.tec.pet.modules.promocion.service;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.modules.promocion.dto.PromocionRequestDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionResponseDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionSummaryDTO;

import java.util.List;

public interface PromocionService {

    PagedResponse<PromocionSummaryDTO> listar(Long proveedorId, int page, int size);

    List<PromocionSummaryDTO> listarActivas(Long proveedorId);

    PromocionResponseDTO crear(Long proveedorId, PromocionRequestDTO dto);

    PromocionResponseDTO actualizar(Long proveedorId, Long id, PromocionRequestDTO dto);

    void eliminar(Long proveedorId, Long id);
}
