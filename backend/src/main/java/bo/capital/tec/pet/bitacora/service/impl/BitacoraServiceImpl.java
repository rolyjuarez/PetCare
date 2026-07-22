package bo.capital.tec.pet.bitacora.service.impl;

import bo.capital.tec.pet.bitacora.dto.BitacoraRequestDTO;
import bo.capital.tec.pet.bitacora.dto.BitacoraResponseDTO;
import bo.capital.tec.pet.bitacora.entity.Bitacora;
import bo.capital.tec.pet.bitacora.mapper.BitacoraMapper;
import bo.capital.tec.pet.bitacora.service.BitacoraService;
import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.persona.entity.Persona;
import bo.capital.tec.pet.persona.mapper.PersonaMapper;
import bo.capital.tec.pet.usuario.entity.Usuario;
import bo.capital.tec.pet.usuario.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BitacoraServiceImpl implements BitacoraService {

    private final BitacoraMapper bitacoraMapper;
    private final UsuarioMapper usuarioMapper;
    private final PersonaMapper personaMapper;

    @Override
    @Async
    @Transactional
    public void saveAsync(BitacoraRequestDTO dto) {
        Bitacora bitacora = Bitacora.builder()
                .usuarioId(dto.getUsuarioId())
                .accion(dto.getAccion())
                .entidad(dto.getEntidad())
                .entidadId(dto.getEntidadId())
                .datosAnteriores(dto.getDatosAnteriores())
                .datosNuevos(dto.getDatosNuevos())
                .ipAddress(dto.getIpAddress())
                .userAgent(dto.getUserAgent())
                .build();
        bitacoraMapper.insert(bitacora);
    }

    @Override
    @Transactional(readOnly = true)
    public BitacoraResponseDTO getById(Long id) {
        Bitacora bitacora = bitacoraMapper.selectById(id);
        if (bitacora == null) {
            throw new EntityNotFoundException("Bitacora", id);
        }
        return toResponseDTO(bitacora);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BitacoraResponseDTO> getAll(int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Bitacora> bitacoras = bitacoraMapper.selectAll(offset, size);
        long total = bitacoraMapper.countAll();
        List<BitacoraResponseDTO> content = bitacoras.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return PagedResponse.<BitacoraResponseDTO>builder()
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
    @Transactional(readOnly = true)
    public PagedResponse<BitacoraResponseDTO> getByFilters(String usuario, String accion, String entidad,
            LocalDateTime fechaDesde, LocalDateTime fechaHasta, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        String fechaInicio = fechaDesde != null ? fechaDesde.toString() : null;
        String fechaFin = fechaHasta != null ? fechaHasta.toString() : null;
        List<Bitacora> bitacoras = bitacoraMapper.selectByFilters(null, entidad, accion, fechaInicio, fechaFin, offset, size);
        long total = bitacoraMapper.countByFilters(null, entidad, accion, fechaInicio, fechaFin);
        List<BitacoraResponseDTO> content = bitacoras.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return PagedResponse.<BitacoraResponseDTO>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .first(page == 0)
                .last((long) (page + 1) * size >= total)
                .build();
    }

    private BitacoraResponseDTO toResponseDTO(Bitacora bitacora) {
        String usuarioNombre = "";
        if (bitacora.getUsuarioId() != null) {
            Usuario usuario = usuarioMapper.selectById(bitacora.getUsuarioId());
            if (usuario != null && usuario.getPersonaId() != null) {
                Persona persona = personaMapper.selectById(usuario.getPersonaId());
                if (persona != null) {
                    usuarioNombre = (persona.getNombre() != null ? persona.getNombre() : "") + " " +
                            (persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "");
                }
            }
        }
        return BitacoraResponseDTO.builder()
                .id(bitacora.getId())
                .usuarioId(bitacora.getUsuarioId())
                .usuarioNombre(usuarioNombre.trim())
                .accion(bitacora.getAccion())
                .entidad(bitacora.getEntidad())
                .entidadId(bitacora.getEntidadId())
                .datosAnteriores(bitacora.getDatosAnteriores())
                .datosNuevos(bitacora.getDatosNuevos())
                .ipAddress(bitacora.getIpAddress())
                .userAgent(bitacora.getUserAgent())
                .createdAt(bitacora.getCreatedAt())
                .build();
    }
}
