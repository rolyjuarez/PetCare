package bo.capital.tec.pet.modules.proveedorservicio.service.impl;

import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.common.exception.EntityNotFoundException;
import bo.capital.tec.pet.modules.proveedor.entity.Proveedor;
import bo.capital.tec.pet.modules.proveedor.mapper.ProveedorMapper;
import bo.capital.tec.pet.modules.proveedorservicio.dto.ModalidadDTO;
import bo.capital.tec.pet.modules.proveedorservicio.dto.ProveedorServicioRequestDTO;
import bo.capital.tec.pet.modules.proveedorservicio.dto.ProveedorServicioResponseDTO;
import bo.capital.tec.pet.modules.proveedorservicio.entity.ProveedorServicio;
import bo.capital.tec.pet.modules.proveedorservicio.entity.ProveedorServicioModalidad;
import bo.capital.tec.pet.modules.proveedorservicio.mapper.ProveedorServicioMapper;
import bo.capital.tec.pet.modules.proveedorservicio.mapper.ProveedorServicioModalidadMapper;
import bo.capital.tec.pet.modules.proveedorservicio.service.ProveedorServicioService;
import bo.capital.tec.pet.modules.soporte.entity.Persona;
import bo.capital.tec.pet.modules.soporte.mapper.PersonaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProveedorServicioServiceImpl implements ProveedorServicioService {

    private static final Set<String> CATEGORIAS = Set.of("PELUQUERIA", "PASEO", "ALOJAMIENTO", "VETERINARIA");
    private static final Set<String> MODALIDADES = Set.of("EN_ESTABLECIMIENTO", "RECOGIDA_ENTREGA", "DOMICILIO");

    private final ProveedorServicioMapper proveedorServicioMapper;
    private final ProveedorServicioModalidadMapper modalidadMapper;
    private final ProveedorMapper proveedorMapper;
    private final PersonaMapper personaMapper;

    @Override
    @Transactional
    public ProveedorServicioResponseDTO create(Long proveedorId, ProveedorServicioRequestDTO dto) {
        validar(dto);
        ProveedorServicio servicio = ProveedorServicio.builder()
                .proveedorId(proveedorId)
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .categoria(dto.getCategoria())
                .duracionMinutos(dto.getDuracionMinutos())
                .precioBase(dto.getPrecioBase())
                .requiereCertificado(Boolean.TRUE.equals(dto.getRequiereCertificado()))
                .activo(dto.getActivo() == null ? true : dto.getActivo())
                .build();
        proveedorServicioMapper.insert(servicio);
        insertarModalidades(servicio.getId(), dto.getModalidades());
        log.info("Servicio {} creado por proveedor {}", servicio.getNombre(), proveedorId);
        return toResponseDTO(servicio);
    }

    @Override
    @Transactional
    public ProveedorServicioResponseDTO update(Long proveedorId, Long id, ProveedorServicioRequestDTO dto) {
        ProveedorServicio servicio = requireOwned(proveedorId, id);
        validar(dto);
        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setCategoria(dto.getCategoria());
        servicio.setDuracionMinutos(dto.getDuracionMinutos());
        servicio.setPrecioBase(dto.getPrecioBase());
        servicio.setRequiereCertificado(Boolean.TRUE.equals(dto.getRequiereCertificado()));
        servicio.setActivo(dto.getActivo() == null ? true : dto.getActivo());
        proveedorServicioMapper.update(servicio);
        modalidadMapper.deleteByProveedorServicioId(id);
        insertarModalidades(id, dto.getModalidades());
        return toResponseDTO(servicio);
    }

    @Override
    @Transactional
    public ProveedorServicioResponseDTO setActivo(Long proveedorId, Long id, Boolean activo) {
        ProveedorServicio servicio = requireOwned(proveedorId, id);
        proveedorServicioMapper.updateActivo(id, Boolean.TRUE.equals(activo));
        servicio.setActivo(Boolean.TRUE.equals(activo));
        return toResponseDTO(servicio);
    }

    @Override
    @Transactional
    public void delete(Long proveedorId, Long id) {
        requireOwned(proveedorId, id);
        modalidadMapper.deleteByProveedorServicioId(id);
        proveedorServicioMapper.softDelete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorServicioResponseDTO getById(Long proveedorId, Long id) {
        return toResponseDTO(requireOwned(proveedorId, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorServicioResponseDTO> listByProveedor(Long proveedorId) {
        return proveedorServicioMapper.selectByProveedorId(proveedorId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorServicioResponseDTO> listActivosByProveedor(Long proveedorId) {
        return proveedorServicioMapper.selectActivosByProveedorId(proveedorId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorServicioResponseDTO> listActivosByCategoria(String categoria) {
        return proveedorServicioMapper.selectActivosByCategoria(categoria).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private void validar(ProveedorServicioRequestDTO dto) {
        if (!CATEGORIAS.contains(dto.getCategoria())) {
            throw new BusinessException("Categoria invalida. Use: PELUQUERIA, PASEO, ALOJAMIENTO o VETERINARIA");
        }
        if (dto.getModalidades() == null || dto.getModalidades().isEmpty()) {
            throw new BusinessException("Debe configurar al menos una modalidad de entrega");
        }
        Set<String> unicas = new HashSet<>();
        for (ModalidadDTO m : dto.getModalidades()) {
            if (m.getModalidad() == null || !MODALIDADES.contains(m.getModalidad())) {
                throw new BusinessException("Modalidad invalida. Use: EN_ESTABLECIMIENTO, RECOGIDA_ENTREGA o DOMICILIO");
            }
            if (!unicas.add(m.getModalidad())) {
                throw new BusinessException("La modalidad " + m.getModalidad() + " esta duplicada");
            }
            if (m.getCostoAdicional() != null && m.getCostoAdicional().signum() < 0) {
                throw new BusinessException("El costo adicional no puede ser negativo");
            }
        }
    }

    private void insertarModalidades(Long servicioId, List<ModalidadDTO> modalidades) {
        for (ModalidadDTO m : modalidades) {
            ProveedorServicioModalidad mod = ProveedorServicioModalidad.builder()
                    .proveedorServicioId(servicioId)
                    .modalidad(m.getModalidad())
                    .costoAdicional(m.getCostoAdicional() != null ? m.getCostoAdicional() : BigDecimal.ZERO)
                    .activo(true)
                    .build();
            modalidadMapper.insert(mod);
        }
    }

    private ProveedorServicio requireOwned(Long proveedorId, Long id) {
        ProveedorServicio servicio = proveedorServicioMapper.selectById(id);
        if (servicio == null) {
            throw new EntityNotFoundException("ProveedorServicio", id);
        }
        if (!servicio.getProveedorId().equals(proveedorId)) {
            throw new BusinessException("El servicio no pertenece al proveedor autenticado");
        }
        return servicio;
    }

    private ProveedorServicioResponseDTO toResponseDTO(ProveedorServicio ps) {
        List<ProveedorServicioResponseDTO.ModalidadDTO> modalidades = new ArrayList<>();
        for (ProveedorServicioModalidad m : modalidadMapper.selectByProveedorServicioId(ps.getId())) {
            modalidades.add(ProveedorServicioResponseDTO.ModalidadDTO.builder()
                    .id(m.getId())
                    .modalidad(m.getModalidad())
                    .costoAdicional(m.getCostoAdicional())
                    .activo(m.getActivo())
                    .build());
        }
        return ProveedorServicioResponseDTO.builder()
                .id(ps.getId())
                .proveedorId(ps.getProveedorId())
                .proveedorNombre(proveedorNombre(ps.getProveedorId()))
                .nombre(ps.getNombre())
                .descripcion(ps.getDescripcion())
                .categoria(ps.getCategoria())
                .duracionMinutos(ps.getDuracionMinutos())
                .precioBase(ps.getPrecioBase())
                .requiereCertificado(ps.getRequiereCertificado())
                .activo(ps.getActivo())
                .modalidades(modalidades)
                .createdAt(ps.getCreatedAt())
                .build();
    }

    private String proveedorNombre(Long proveedorId) {
        if (proveedorId == null) {
            return "";
        }
        Proveedor proveedor = proveedorMapper.selectById(proveedorId);
        if (proveedor == null) {
            return "";
        }
        Persona persona = personaMapper.selectById(proveedor.getPersonaId());
        if (persona == null) {
            return "";
        }
        return (persona.getNombre() + " "
                + (persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "")).trim();
    }
}
