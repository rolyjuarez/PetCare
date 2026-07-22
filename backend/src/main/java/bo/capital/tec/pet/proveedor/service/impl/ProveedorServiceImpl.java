package bo.capital.tec.pet.proveedor.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.persona.entity.Persona;
import bo.capital.tec.pet.persona.mapper.PersonaMapper;
import bo.capital.tec.pet.proveedor.dto.ProveedorRequestDTO;
import bo.capital.tec.pet.proveedor.dto.ProveedorResponseDTO;
import bo.capital.tec.pet.proveedor.dto.ProveedorSummaryDTO;
import bo.capital.tec.pet.proveedor.entity.Proveedor;
import bo.capital.tec.pet.proveedor.entity.ProveedorEspecialidad;
import bo.capital.tec.pet.proveedor.mapper.DisponibilidadMapper;
import bo.capital.tec.pet.proveedor.mapper.ProveedorEspecialidadMapper;
import bo.capital.tec.pet.proveedor.mapper.ProveedorMapper;
import bo.capital.tec.pet.proveedor.service.ProveedorService;
import bo.capital.tec.pet.servicio.mapper.ServicioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorMapper proveedorMapper;
    private final ProveedorEspecialidadMapper proveedorEspecialidadMapper;
    private final DisponibilidadMapper disponibilidadMapper;
    private final PersonaMapper personaMapper;
    private final ServicioMapper servicioMapper;

    @Override
    @Transactional
    public ProveedorResponseDTO create(ProveedorRequestDTO dto) {
        Proveedor proveedor = Proveedor.builder()
                .personaId(dto.getPersonaId())
                .usuarioId(dto.getUsuarioId())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .radioCoberturaKm(dto.getRadioCoberturaKm())
                .descripcion(dto.getDescripcion())
                .verificado(false)
                .calificacion(BigDecimal.ZERO)
                .build();
        proveedorMapper.insert(proveedor);
        if (dto.getServicioIds() != null) {
            for (Long servicioId : dto.getServicioIds()) {
                ProveedorEspecialidad pe = ProveedorEspecialidad.builder()
                        .proveedorId(proveedor.getId())
                        .servicioId(servicioId)
                        .build();
                proveedorEspecialidadMapper.insert(pe);
            }
        }
        return toResponseDTO(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponseDTO getById(Long id) {
        Proveedor proveedor = proveedorMapper.selectById(id);
        if (proveedor == null) {
            throw new EntityNotFoundException("Proveedor", id);
        }
        return toResponseDTO(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProveedorSummaryDTO> getAll(String nombre, Long especialidadId, boolean activo, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Proveedor> proveedores = proveedorMapper.selectAll(nombre, null, offset, size);
        long total = proveedorMapper.countAll(nombre, null);
        List<ProveedorSummaryDTO> content = proveedores.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        return PagedResponse.<ProveedorSummaryDTO>builder()
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
    public ProveedorResponseDTO update(Long id, ProveedorRequestDTO dto) {
        Proveedor proveedor = proveedorMapper.selectById(id);
        if (proveedor == null) {
            throw new EntityNotFoundException("Proveedor", id);
        }
        proveedor.setPersonaId(dto.getPersonaId());
        proveedor.setUsuarioId(dto.getUsuarioId());
        proveedor.setLatitud(dto.getLatitud());
        proveedor.setLongitud(dto.getLongitud());
        proveedor.setRadioCoberturaKm(dto.getRadioCoberturaKm());
        proveedor.setDescripcion(dto.getDescripcion());
        proveedorMapper.update(proveedor);
        if (dto.getServicioIds() != null) {
            proveedorEspecialidadMapper.deleteByProveedorId(id);
            for (Long servicioId : dto.getServicioIds()) {
                ProveedorEspecialidad pe = ProveedorEspecialidad.builder()
                        .proveedorId(proveedor.getId())
                        .servicioId(servicioId)
                        .build();
                proveedorEspecialidadMapper.insert(pe);
            }
        }
        return toResponseDTO(proveedor);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Proveedor proveedor = proveedorMapper.selectById(id);
        if (proveedor == null) {
            throw new EntityNotFoundException("Proveedor", id);
        }
        proveedorEspecialidadMapper.deleteByProveedorId(id);
        proveedorMapper.softDelete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorSummaryDTO> searchNearby(BigDecimal lat, BigDecimal lng, BigDecimal radioKm) {
        List<Proveedor> proveedores = proveedorMapper.searchNearby(lat, lng, radioKm);
        return proveedores.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorSummaryDTO> getByServicioId(Long servicioId) {
        List<Proveedor> proveedores = proveedorMapper.findByServicioId(servicioId);
        return proveedores.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    private ProveedorResponseDTO toResponseDTO(Proveedor proveedor) {
        Persona persona = personaMapper.selectById(proveedor.getPersonaId());
        String personaNombre = "";
        String personaTelefono = "";
        String personaEmail = "";
        if (persona != null) {
            personaNombre = (persona.getNombre() != null ? persona.getNombre() : "") + " " +
                    (persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "");
            personaTelefono = persona.getTelefono() != null ? persona.getTelefono() : "";
            personaEmail = persona.getEmail() != null ? persona.getEmail() : "";
        }
        List<ProveedorEspecialidad> especialidades = proveedorEspecialidadMapper.selectByProveedorId(proveedor.getId());
        List<String> especialidadNombres = new ArrayList<>();
        for (ProveedorEspecialidad pe : especialidades) {
            var servicio = servicioMapper.selectById(pe.getServicioId());
            if (servicio != null) {
                especialidadNombres.add(servicio.getNombre());
            }
        }
        return ProveedorResponseDTO.builder()
                .id(proveedor.getId())
                .personaId(proveedor.getPersonaId())
                .personaNombre(personaNombre.trim())
                .personaTelefono(personaTelefono)
                .personaEmail(personaEmail)
                .latitud(proveedor.getLatitud())
                .longitud(proveedor.getLongitud())
                .radioCoberturaKm(proveedor.getRadioCoberturaKm())
                .descripcion(proveedor.getDescripcion())
                .verificado(proveedor.getVerificado())
                .calificacion(proveedor.getCalificacion())
                .especialidades(especialidadNombres)
                .createdAt(proveedor.getCreatedAt())
                .build();
    }

    private ProveedorSummaryDTO toSummaryDTO(Proveedor proveedor) {
        Persona persona = personaMapper.selectById(proveedor.getPersonaId());
        String nombre = "";
        if (persona != null) {
            nombre = (persona.getNombre() != null ? persona.getNombre() : "") + " " +
                    (persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "");
        }
        return ProveedorSummaryDTO.builder()
                .id(proveedor.getId())
                .nombre(nombre.trim())
                .descripcion(proveedor.getDescripcion())
                .calificacion(proveedor.getCalificacion())
                .verificado(proveedor.getVerificado())
                .build();
    }
}
