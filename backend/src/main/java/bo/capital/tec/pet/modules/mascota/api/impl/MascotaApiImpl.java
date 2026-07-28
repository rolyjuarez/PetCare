package bo.capital.tec.pet.modules.mascota.api.impl;

import bo.capital.tec.pet.modules.mascota.api.MascotaApi;
import bo.capital.tec.pet.modules.mascota.entity.Mascota;
import bo.capital.tec.pet.modules.mascota.mapper.MascotaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MascotaApiImpl implements MascotaApi {

    private final MascotaMapper mascotaMapper;

    @Override
    public Mascota selectById(Long id) {
        return mascotaMapper.selectById(id);
    }
}
