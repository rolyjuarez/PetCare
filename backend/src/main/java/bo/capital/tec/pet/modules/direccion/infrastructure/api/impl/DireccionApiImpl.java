package bo.capital.tec.pet.modules.direccion.infrastructure.api.impl;

import bo.capital.tec.pet.modules.direccion.api.DireccionApi;
import bo.capital.tec.pet.modules.direccion.domain.model.Direccion;
import bo.capital.tec.pet.modules.direccion.domain.port.DireccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DireccionApiImpl implements DireccionApi {

    private final DireccionRepository direccionRepository;

    @Override
    public Long insert(Direccion direccion) {
        return direccionRepository.insert(direccion);
    }

    @Override
    public Direccion selectById(Long id) {
        return direccionRepository.selectById(id);
    }

    @Override
    public void update(Direccion direccion) {
        direccionRepository.update(direccion);
    }
}
