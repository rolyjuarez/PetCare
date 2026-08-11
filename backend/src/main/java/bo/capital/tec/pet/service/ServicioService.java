package bo.capital.tec.pet.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.ServicioRequestDTO;
import bo.capital.tec.pet.dto.ServicioResponseDTO;
import bo.capital.tec.pet.dto.ServicioSummaryDTO;

import java.util.List;

public interface ServicioService {
    ServicioResponseDTO create(ServicioRequestDTO dto);
    ServicioResponseDTO getById(Long id);
    PagedResponse<ServicioSummaryDTO> getAll(String nombre, Long categoriaId, boolean activo, int page, int size);
    List<ServicioSummaryDTO> getActive();
    ServicioResponseDTO update(Long id, ServicioRequestDTO dto);
    void delete(Long id);
}
