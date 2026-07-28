package bo.capital.tec.pet.modules.usuario.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.usuario.dto.UsuarioRequestDTO;
import bo.capital.tec.pet.modules.usuario.dto.UsuarioResponseDTO;
import bo.capital.tec.pet.modules.usuario.dto.UsuarioSummaryDTO;

public interface UsuarioService {
    UsuarioResponseDTO create(UsuarioRequestDTO dto);
    UsuarioResponseDTO getById(Long id);
    UsuarioResponseDTO getByUsername(String username);
    PagedResponse<UsuarioSummaryDTO> getAll(String username, Long personaId, Long rolId, boolean activo, int page, int size);
    UsuarioResponseDTO update(Long id, UsuarioRequestDTO dto);
    void delete(Long id);
    void changePassword(Long id, String oldPassword, String newPassword);
    void toggleActive(Long id);
}
