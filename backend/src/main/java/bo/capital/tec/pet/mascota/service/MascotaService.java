package bo.capital.tec.pet.mascota.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.mascota.dto.MascotaRequestDTO;
import bo.capital.tec.pet.mascota.dto.MascotaResponseDTO;
import bo.capital.tec.pet.mascota.dto.MascotaSummaryDTO;

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
