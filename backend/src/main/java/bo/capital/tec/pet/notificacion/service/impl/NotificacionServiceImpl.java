package bo.capital.tec.pet.notificacion.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.notificacion.dto.NotificacionRequestDTO;
import bo.capital.tec.pet.notificacion.dto.NotificacionResponseDTO;
import bo.capital.tec.pet.notificacion.dto.NotificacionSummaryDTO;
import bo.capital.tec.pet.notificacion.entity.Notificacion;
import bo.capital.tec.pet.notificacion.mapper.NotificacionMapper;
import bo.capital.tec.pet.notificacion.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionMapper notificacionMapper;

    @Override
    @Transactional
    public NotificacionResponseDTO create(NotificacionRequestDTO dto) {
        Notificacion notificacion = Notificacion.builder()
                .usuarioId(dto.getUsuarioId())
                .titulo(dto.getTitulo())
                .mensaje(dto.getMensaje())
                .tipo(dto.getTipo())
                .leida(false)
                .build();
        notificacionMapper.insert(notificacion);
        return toResponseDTO(notificacion);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificacionResponseDTO getById(Long id) {
        Notificacion notificacion = notificacionMapper.selectById(id);
        if (notificacion == null) {
            throw new EntityNotFoundException("Notificacion", id);
        }
        return toResponseDTO(notificacion);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<NotificacionResponseDTO> getAll(int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Notificacion> notificaciones = notificacionMapper.selectAll(offset, size);
        long total = notificacionMapper.countAll();
        List<NotificacionResponseDTO> content = notificaciones.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return PagedResponse.<NotificacionResponseDTO>builder()
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
    public PagedResponse<NotificacionSummaryDTO> getByUsuarioId(Long usuarioId, boolean soloNoLeidas, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Notificacion> notificaciones = notificacionMapper.selectByUsuarioId(usuarioId, offset, size);
        long total = notificacionMapper.countByUsuarioIdAndLeida(usuarioId, soloNoLeidas ? false : null);
        List<NotificacionSummaryDTO> content = notificaciones.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        return PagedResponse.<NotificacionSummaryDTO>builder()
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
    public long getUnreadCount(Long usuarioId) {
        return notificacionMapper.countByUsuarioIdAndLeida(usuarioId, false);
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        Notificacion notificacion = notificacionMapper.selectById(id);
        if (notificacion == null) {
            throw new EntityNotFoundException("Notificacion", id);
        }
        notificacionMapper.updateLeida(id);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long usuarioId) {
        List<Notificacion> notificaciones = notificacionMapper.selectByUsuarioId(usuarioId, 0, 10000);
        for (Notificacion notificacion : notificaciones) {
            if (Boolean.FALSE.equals(notificacion.getLeida())) {
                notificacionMapper.updateLeida(notificacion.getId());
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Notificacion notificacion = notificacionMapper.selectById(id);
        if (notificacion == null) {
            throw new EntityNotFoundException("Notificacion", id);
        }
        notificacionMapper.deleteOld();
    }

    private NotificacionResponseDTO toResponseDTO(Notificacion notificacion) {
        return NotificacionResponseDTO.builder()
                .id(notificacion.getId())
                .titulo(notificacion.getTitulo())
                .mensaje(notificacion.getMensaje())
                .tipo(notificacion.getTipo())
                .leida(notificacion.getLeida())
                .fechaLectura(notificacion.getFechaLectura())
                .createdAt(notificacion.getCreatedAt())
                .build();
    }

    private NotificacionSummaryDTO toSummaryDTO(Notificacion notificacion) {
        return NotificacionSummaryDTO.builder()
                .id(notificacion.getId())
                .usuarioId(notificacion.getUsuarioId())
                .titulo(notificacion.getTitulo())
                .tipo(notificacion.getTipo())
                .leida(notificacion.getLeida())
                .createdAt(notificacion.getCreatedAt())
                .build();
    }
}
