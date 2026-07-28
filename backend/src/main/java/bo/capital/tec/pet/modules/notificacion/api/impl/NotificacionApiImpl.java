package bo.capital.tec.pet.modules.notificacion.api.impl;

import bo.capital.tec.pet.modules.notificacion.api.NotificacionApi;
import bo.capital.tec.pet.modules.notificacion.entity.Notificacion;
import bo.capital.tec.pet.modules.notificacion.mapper.NotificacionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacionApiImpl implements NotificacionApi {

    private final NotificacionMapper notificacionMapper;

    @Override
    public Long insert(Notificacion notificacion) {
        return notificacionMapper.insert(notificacion);
    }
}
