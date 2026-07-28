package bo.capital.tec.pet.modules.servicio.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.servicio.dto.ServicioRequestDTO;
import bo.capital.tec.pet.modules.servicio.dto.ServicioResponseDTO;
import bo.capital.tec.pet.modules.servicio.dto.ServicioSummaryDTO;
import bo.capital.tec.pet.modules.servicio.entity.Servicio;
import bo.capital.tec.pet.modules.servicio.mapper.ServicioMapper;
import bo.capital.tec.pet.modules.servicio.service.ServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicioServiceImpl implements ServicioService {

    private final ServicioMapper servicioMapper;

    @Override
    @Transactional
    public ServicioResponseDTO create(ServicioRequestDTO dto) {
        Servicio servicio = Servicio.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .duracionMinutos(dto.getDuracionMinutos())
                .precioBase(dto.getPrecioBase())
                .imagenUrl(dto.getImagenUrl())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .categoria(dto.getCategoria())
                .build();
        servicioMapper.insert(servicio);
        return toResponseDTO(servicio);
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioResponseDTO getById(Long id) {
        Servicio servicio = servicioMapper.selectById(id);
        if (servicio == null) {
            throw new EntityNotFoundException("Servicio", id);
        }
        return toResponseDTO(servicio);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ServicioSummaryDTO> getAll(String nombre, Long categoriaId, boolean activo, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Servicio> servicios = servicioMapper.selectAll(nombre, null, offset, size);
        long total = servicioMapper.countAll(nombre, null);
        List<ServicioSummaryDTO> content = servicios.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        return PagedResponse.<ServicioSummaryDTO>builder()
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
    public List<ServicioSummaryDTO> getActive() {
        List<Servicio> servicios = servicioMapper.selectActive();
        return servicios.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServicioResponseDTO update(Long id, ServicioRequestDTO dto) {
        Servicio servicio = servicioMapper.selectById(id);
        if (servicio == null) {
            throw new EntityNotFoundException("Servicio", id);
        }
        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setDuracionMinutos(dto.getDuracionMinutos());
        servicio.setPrecioBase(dto.getPrecioBase());
        servicio.setImagenUrl(dto.getImagenUrl());
        if (dto.getActivo() != null) {
            servicio.setActivo(dto.getActivo());
        }
        servicio.setCategoria(dto.getCategoria());
        servicioMapper.update(servicio);
        return toResponseDTO(servicio);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Servicio servicio = servicioMapper.selectById(id);
        if (servicio == null) {
            throw new EntityNotFoundException("Servicio", id);
        }
        servicioMapper.softDelete(id);
    }

    private ServicioResponseDTO toResponseDTO(Servicio servicio) {
        return ServicioResponseDTO.builder()
                .id(servicio.getId())
                .nombre(servicio.getNombre())
                .descripcion(servicio.getDescripcion())
                .duracionMinutos(servicio.getDuracionMinutos())
                .precioBase(servicio.getPrecioBase())
                .imagenUrl(servicio.getImagenUrl())
                .activo(servicio.getActivo())
                .categoria(servicio.getCategoria())
                .createdAt(servicio.getCreatedAt())
                .build();
    }

    private ServicioSummaryDTO toSummaryDTO(Servicio servicio) {
        return ServicioSummaryDTO.builder()
                .id(servicio.getId())
                .nombre(servicio.getNombre())
                .precioBase(servicio.getPrecioBase())
                .categoria(servicio.getCategoria())
                .build();
    }
}
