package bo.capital.tec.pet.modules.proveedor.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.proveedor.dto.DisponibilidadRequestDTO;
import bo.capital.tec.pet.modules.proveedor.dto.DisponibilidadResponseDTO;
import bo.capital.tec.pet.modules.proveedor.entity.Disponibilidad;
import bo.capital.tec.pet.modules.proveedor.mapper.DisponibilidadMapper;
import bo.capital.tec.pet.modules.proveedor.service.DisponibilidadService;
import bo.capital.tec.pet.modules.servicio.entity.Servicio;
import bo.capital.tec.pet.modules.servicio.mapper.ServicioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisponibilidadServiceImpl implements DisponibilidadService {

    private static final String[] DIAS_SEMANA = {"Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"};

    private final DisponibilidadMapper disponibilidadMapper;
    private final ServicioMapper servicioMapper;

    @Override
    @Transactional
    public DisponibilidadResponseDTO create(DisponibilidadRequestDTO dto) {
        Disponibilidad disponibilidad = Disponibilidad.builder()
                .proveedorId(dto.getProveedorId())
                .servicioId(dto.getServicioId())
                .diaSemana(dto.getDiaSemana())
                .horaInicio(dto.getHoraInicio())
                .horaFin(dto.getHoraFin())
                .activo(true)
                .build();
        disponibilidadMapper.insert(disponibilidad);
        return toResponseDTO(disponibilidad);
    }

    @Override
    @Transactional(readOnly = true)
    public DisponibilidadResponseDTO getById(Long id) {
        Disponibilidad disponibilidad = disponibilidadMapper.selectById(id);
        if (disponibilidad == null) {
            throw new EntityNotFoundException("Disponibilidad", id);
        }
        return toResponseDTO(disponibilidad);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DisponibilidadResponseDTO> getAll(Long proveedorId, Long servicioId, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Disponibilidad> disponibilidades = disponibilidadMapper.selectAll(proveedorId, servicioId, offset, size);
        long total = disponibilidadMapper.countAll(proveedorId, servicioId);
        List<DisponibilidadResponseDTO> content = disponibilidades.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return PagedResponse.<DisponibilidadResponseDTO>builder()
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
    public List<DisponibilidadResponseDTO> getByProveedorAndServicio(Long proveedorId, Long servicioId) {
        List<Disponibilidad> disponibilidades = disponibilidadMapper.selectByProveedorYServicio(proveedorId, servicioId);
        return disponibilidades.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadResponseDTO> getByServicio(Long servicioId) {
        List<Disponibilidad> disponibilidades = disponibilidadMapper.selectByServicioId(servicioId);
        return disponibilidades.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DisponibilidadResponseDTO update(Long id, DisponibilidadRequestDTO dto) {
        Disponibilidad disponibilidad = disponibilidadMapper.selectById(id);
        if (disponibilidad == null) {
            throw new EntityNotFoundException("Disponibilidad", id);
        }
        disponibilidad.setProveedorId(dto.getProveedorId());
        disponibilidad.setServicioId(dto.getServicioId());
        disponibilidad.setDiaSemana(dto.getDiaSemana());
        disponibilidad.setHoraInicio(dto.getHoraInicio());
        disponibilidad.setHoraFin(dto.getHoraFin());
        disponibilidadMapper.update(disponibilidad);
        return toResponseDTO(disponibilidad);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Disponibilidad disponibilidad = disponibilidadMapper.selectById(id);
        if (disponibilidad == null) {
            throw new EntityNotFoundException("Disponibilidad", id);
        }
        disponibilidadMapper.softDelete(id);
    }

    private DisponibilidadResponseDTO toResponseDTO(Disponibilidad disponibilidad) {
        String diaSemanaNombre = "";
        if (disponibilidad.getDiaSemana() != null && disponibilidad.getDiaSemana() >= 0 && disponibilidad.getDiaSemana() <= 6) {
            diaSemanaNombre = DIAS_SEMANA[disponibilidad.getDiaSemana()];
        }
        String servicioNombre = "";
        Servicio servicio = servicioMapper.selectById(disponibilidad.getServicioId());
        if (servicio != null) {
            servicioNombre = servicio.getNombre();
        }
        return DisponibilidadResponseDTO.builder()
                .id(disponibilidad.getId())
                .proveedorId(disponibilidad.getProveedorId())
                .servicioId(disponibilidad.getServicioId())
                .servicioNombre(servicioNombre)
                .diaSemana(disponibilidad.getDiaSemana())
                .diaSemanaNombre(diaSemanaNombre)
                .horaInicio(disponibilidad.getHoraInicio())
                .horaFin(disponibilidad.getHoraFin())
                .activo(disponibilidad.getActivo())
                .build();
    }
}
