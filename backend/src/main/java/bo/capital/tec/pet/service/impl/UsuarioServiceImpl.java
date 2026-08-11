package bo.capital.tec.pet.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.domain.Persona;
import bo.capital.tec.pet.repository.PersonaMapper;
import bo.capital.tec.pet.dto.UsuarioRequestDTO;
import bo.capital.tec.pet.dto.UsuarioResponseDTO;
import bo.capital.tec.pet.dto.UsuarioSummaryDTO;
import bo.capital.tec.pet.domain.Usuario;
import bo.capital.tec.pet.repository.UsuarioMapper;
import bo.capital.tec.pet.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioMapper usuarioMapper;
    private final PersonaMapper personaMapper;

    @Override
    @Transactional
    public UsuarioResponseDTO create(UsuarioRequestDTO dto) {
        Persona persona = personaMapper.selectById(dto.getPersonaId());
        if (persona == null) {
            throw new EntityNotFoundException("Persona", dto.getPersonaId());
        }
        Usuario usuario = Usuario.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .personaId(dto.getPersonaId())
                .activo(true)
                .intentosFallidos(0)
                .bloqueado(false)
                .build();
        usuarioMapper.insert(usuario);
        return toResponseDTO(usuario, persona);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO getById(Long id) {
        Usuario usuario = usuarioMapper.selectById(id);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario", id);
        }
        Persona persona = personaMapper.selectById(usuario.getPersonaId());
        return toResponseDTO(usuario, persona);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO getByUsername(String username) {
        Usuario usuario = usuarioMapper.findByUsername(username);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario", "username", username);
        }
        Persona persona = personaMapper.selectById(usuario.getPersonaId());
        return toResponseDTO(usuario, persona);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UsuarioSummaryDTO> getAll(String username, Long personaId, Long rolId, boolean activo, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Usuario> usuarios = usuarioMapper.selectAll(username, null, offset, size);
        long total = usuarioMapper.countAll(username, null);
        List<UsuarioSummaryDTO> content = usuarios.stream()
                .map(u -> {
                    Persona persona = personaMapper.selectById(u.getPersonaId());
                    String personaNombre = persona != null ? persona.getNombre() + " " +
                            (persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "") : "";
                    return UsuarioSummaryDTO.builder()
                            .id(u.getId())
                            .username(u.getUsername())
                            .personaNombre(personaNombre.trim())
                            .activo(u.getActivo())
                            .build();
                })
                .collect(Collectors.toList());
        return PagedResponse.<UsuarioSummaryDTO>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .first(page == 0)
                .last((long) (page + 1) * size >= total)
                .build();
    }

    @Override
    @Transactional
    public UsuarioResponseDTO update(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioMapper.selectById(id);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario", id);
        }
        usuario.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(dto.getPassword());
        }
        usuarioMapper.update(usuario);
        Persona persona = personaMapper.selectById(usuario.getPersonaId());
        return toResponseDTO(usuario, persona);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Usuario usuario = usuarioMapper.selectById(id);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario", id);
        }
        usuarioMapper.softDelete(id);
    }

    @Override
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        Usuario usuario = usuarioMapper.selectById(id);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario", id);
        }
        if (!usuario.getPassword().equals(oldPassword)) {
            throw new bo.capital.tec.pet.common.exceptions.BusinessException("La contraseña actual no es correcta");
        }
        usuario.setPassword(newPassword);
        usuarioMapper.update(usuario);
    }

    @Override
    @Transactional
    public void toggleActive(Long id) {
        Usuario usuario = usuarioMapper.selectById(id);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario", id);
        }
        usuario.setActivo(!usuario.getActivo());
        usuarioMapper.update(usuario);
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario, Persona persona) {
        List<String> roles = usuarioMapper.findRolesByUsuarioId(usuario.getId());
        String personaNombre = "";
        if (persona != null) {
            personaNombre = persona.getNombre() + " " +
                    (persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "");
        }
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .personaId(usuario.getPersonaId())
                .personaNombre(personaNombre.trim())
                .activo(usuario.getActivo())
                .bloqueado(usuario.getBloqueado())
                .ultimoAcceso(usuario.getUltimoAcceso())
                .roles(roles)
                .createdAt(usuario.getCreatedAt())
                .build();
    }
}
