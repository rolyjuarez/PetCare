package bo.capital.tec.pet.modules.vacuna.api.impl;

import bo.capital.tec.pet.modules.vacuna.api.RegistroVacunacionApi;
import bo.capital.tec.pet.modules.vacuna.mapper.RegistroVacunacionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistroVacunacionApiImpl implements RegistroVacunacionApi {

    private final RegistroVacunacionMapper registroVacunacionMapper;

    @Override
    public void softDeleteByMascotaId(Long mascotaId) {
        registroVacunacionMapper.softDeleteByMascotaId(mascotaId);
    }
}
