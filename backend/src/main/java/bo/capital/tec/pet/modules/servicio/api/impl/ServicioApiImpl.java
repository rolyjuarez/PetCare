package bo.capital.tec.pet.modules.servicio.api.impl;

import bo.capital.tec.pet.modules.servicio.api.ServicioApi;
import bo.capital.tec.pet.modules.servicio.entity.Servicio;
import bo.capital.tec.pet.modules.servicio.mapper.ServicioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServicioApiImpl implements ServicioApi {

    private final ServicioMapper servicioMapper;

    @Override
    public Servicio selectById(Long id) {
        return servicioMapper.selectById(id);
    }
}
