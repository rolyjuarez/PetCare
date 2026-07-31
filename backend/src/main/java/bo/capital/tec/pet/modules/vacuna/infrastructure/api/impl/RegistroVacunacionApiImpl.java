package bo.capital.tec.pet.modules.vacuna.infrastructure.api.impl;

import bo.capital.tec.pet.modules.vacuna.api.RegistroVacunacionApi;
import bo.capital.tec.pet.modules.vacuna.domain.port.RegistroVacunacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistroVacunacionApiImpl implements RegistroVacunacionApi {

    private final RegistroVacunacionRepository registroVacunacionRepository;

    @Override
    public void softDeleteByMascotaId(Long mascotaId) {
        registroVacunacionRepository.softDeleteByMascotaId(mascotaId);
    }
}
