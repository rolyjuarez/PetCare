package bo.capital.tec.pet.modules.reserva.infrastructure.adapter.out.persistence;

import bo.capital.tec.pet.modules.notificacion.entity.Notificacion;
import bo.capital.tec.pet.modules.notificacion.mapper.NotificacionMapper;
import bo.capital.tec.pet.modules.reserva.domain.port.out.NotificationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Implementación MyBatis del puerto de notificaciones.
 */
@Repository
@RequiredArgsConstructor
public class MyBatisNotificationPort implements NotificationPort {

    private final NotificacionMapper notificacionMapper;

    @Override
    public void notificar(Long usuarioId, String titulo, String mensaje, String tipo) {
        notificacionMapper.insert(Notificacion.builder()
                .usuarioId(usuarioId)
                .titulo(titulo)
                .mensaje(mensaje)
                .tipo(tipo)
                .leida(false)
                .build());
    }
}
