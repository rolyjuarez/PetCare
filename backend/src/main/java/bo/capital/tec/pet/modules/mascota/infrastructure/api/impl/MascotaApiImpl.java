package bo.capital.tec.pet.modules.mascota.infrastructure.api.impl;

import bo.capital.tec.pet.modules.mascota.api.MascotaApi;
import bo.capital.tec.pet.modules.mascota.domain.model.Mascota;
import bo.capital.tec.pet.modules.mascota.domain.port.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MascotaApiImpl implements MascotaApi {

    private final MascotaRepository mascotaRepository;

    @Override
    public Mascota selectById(Long id) {
        return mascotaRepository.selectById(id);
    }
}
