package bo.capital.tec.pet.modules.servicio.infrastructure.api.impl;

import bo.capital.tec.pet.modules.servicio.api.ServicioApi;
import bo.capital.tec.pet.modules.servicio.domain.model.Servicio;
import bo.capital.tec.pet.modules.servicio.domain.port.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServicioApiImpl implements ServicioApi {

    private final ServicioRepository servicioRepository;

    @Override
    public Servicio selectById(Long id) {
        return servicioRepository.selectById(id);
    }
}
