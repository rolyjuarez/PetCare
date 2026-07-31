package bo.capital.tec.pet.modules.cliente.application.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.cliente.dto.ClienteRequestDTO;
import bo.capital.tec.pet.modules.cliente.dto.ClienteResponseDTO;
import bo.capital.tec.pet.modules.cliente.dto.ClienteSummaryDTO;

public interface ClienteService {
    ClienteResponseDTO create(ClienteRequestDTO dto);
    ClienteResponseDTO getById(Long id);
    ClienteResponseDTO getByPersonaId(Long personaId);
    PagedResponse<ClienteSummaryDTO> getAll(String nombre, String ci, boolean activo, int page, int size);
    ClienteResponseDTO update(Long id, ClienteRequestDTO dto);
    void delete(Long id);
}
