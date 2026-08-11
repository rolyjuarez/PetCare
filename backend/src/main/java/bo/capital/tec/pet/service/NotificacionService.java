package bo.capital.tec.pet.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.NotificacionRequestDTO;
import bo.capital.tec.pet.dto.NotificacionResponseDTO;
import bo.capital.tec.pet.dto.NotificacionSummaryDTO;

public interface NotificacionService {
    NotificacionResponseDTO create(NotificacionRequestDTO dto);
    NotificacionResponseDTO getById(Long id);
    PagedResponse<NotificacionResponseDTO> getAll(int page, int size);
    PagedResponse<NotificacionSummaryDTO> getByUsuarioId(Long usuarioId, boolean soloNoLeidas, int page, int size);
    long getUnreadCount(Long usuarioId);
    void markAsRead(Long id);
    void markAllAsRead(Long usuarioId);
    void delete(Long id);
}
