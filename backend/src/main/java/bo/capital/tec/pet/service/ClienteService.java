package bo.capital.tec.pet.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.ClienteRequestDTO;
import bo.capital.tec.pet.dto.ClienteResponseDTO;
import bo.capital.tec.pet.dto.ClienteSummaryDTO;

public interface ClienteService {
    ClienteResponseDTO create(ClienteRequestDTO dto);
    ClienteResponseDTO getById(Long id);
    ClienteResponseDTO getByPersonaId(Long personaId);
    PagedResponse<ClienteSummaryDTO> getAll(String nombre, String ci, boolean activo, int page, int size);
    ClienteResponseDTO update(Long id, ClienteRequestDTO dto);
    void delete(Long id);
}
