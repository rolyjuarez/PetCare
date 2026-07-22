package bo.capital.tec.pet.bitacora.service;

import bo.capital.tec.pet.bitacora.dto.BitacoraRequestDTO;
import bo.capital.tec.pet.bitacora.dto.BitacoraResponseDTO;
import bo.capital.tec.pet.common.response.PagedResponse;

import java.time.LocalDateTime;

public interface BitacoraService {
    void saveAsync(BitacoraRequestDTO dto);
    BitacoraResponseDTO getById(Long id);
    PagedResponse<BitacoraResponseDTO> getAll(int page, int size);
    PagedResponse<BitacoraResponseDTO> getByFilters(String usuario, String accion, String entidad,
            LocalDateTime fechaDesde, LocalDateTime fechaHasta, int page, int size);
}
