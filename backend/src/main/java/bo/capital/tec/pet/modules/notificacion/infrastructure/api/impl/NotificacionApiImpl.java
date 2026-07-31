package bo.capital.tec.pet.modules.notificacion.infrastructure.api.impl;

import bo.capital.tec.pet.modules.notificacion.api.NotificacionApi;
import bo.capital.tec.pet.modules.notificacion.domain.model.Notificacion;
import bo.capital.tec.pet.modules.notificacion.domain.port.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacionApiImpl implements NotificacionApi {

    private final NotificacionRepository notificacionRepository;

    @Override
    public Long insert(Notificacion notificacion) {
        return notificacionRepository.insert(notificacion);
    }
}
