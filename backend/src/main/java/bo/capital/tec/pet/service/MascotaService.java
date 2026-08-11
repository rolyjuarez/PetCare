package bo.capital.tec.pet.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.MascotaRequestDTO;
import bo.capital.tec.pet.dto.MascotaResponseDTO;
import bo.capital.tec.pet.dto.MascotaSummaryDTO;

import java.util.List;

public interface MascotaService {
    MascotaResponseDTO create(MascotaRequestDTO dto);
    MascotaResponseDTO getById(Long id);
    PagedResponse<MascotaSummaryDTO> getAll(String nombre, Long clienteId, Long especieId, int page, int size);
    List<MascotaSummaryDTO> getByClienteId(Long clienteId);
    List<MascotaSummaryDTO> getByUsuarioId(Long usuarioId);
    MascotaResponseDTO update(Long id, MascotaRequestDTO dto);
    void delete(Long id);
}
