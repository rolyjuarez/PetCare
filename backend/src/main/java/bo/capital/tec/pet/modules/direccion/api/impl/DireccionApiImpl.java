package bo.capital.tec.pet.modules.direccion.api.impl;

import bo.capital.tec.pet.modules.direccion.api.DireccionApi;
import bo.capital.tec.pet.modules.direccion.entity.Direccion;
import bo.capital.tec.pet.modules.direccion.mapper.DireccionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DireccionApiImpl implements DireccionApi {

    private final DireccionMapper direccionMapper;

    @Override
    public Long insert(Direccion direccion) {
        return direccionMapper.insert(direccion);
    }

    @Override
    public Direccion selectById(Long id) {
        return direccionMapper.selectById(id);
    }

    @Override
    public void update(Direccion direccion) {
        direccionMapper.update(direccion);
    }
}
