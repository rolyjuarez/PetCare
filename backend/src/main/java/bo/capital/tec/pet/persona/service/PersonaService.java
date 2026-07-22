package bo.capital.tec.pet.persona.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.persona.dto.PersonaRequestDTO;
import bo.capital.tec.pet.persona.dto.PersonaResponseDTO;
import bo.capital.tec.pet.persona.dto.PersonaSummaryDTO;

public interface PersonaService {
    PersonaResponseDTO create(PersonaRequestDTO dto);
    PersonaResponseDTO getById(Long id);
    PagedResponse<PersonaSummaryDTO> getAll(String nombre, String ci, String email, int page, int size);
    PersonaResponseDTO update(Long id, PersonaRequestDTO dto);
    void delete(Long id);
}
