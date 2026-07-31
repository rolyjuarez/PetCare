package bo.capital.tec.pet.modules.rol.infrastructure.api.impl;

import bo.capital.tec.pet.modules.rol.api.RolApi;
import bo.capital.tec.pet.modules.rol.domain.model.Rol;
import bo.capital.tec.pet.modules.rol.domain.port.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RolApiImpl implements RolApi {

    private final RolRepository rolRepository;

    @Override
    public Rol selectByNombre(String nombre) {
        return rolRepository.selectByNombre(nombre);
    }

    @Override
    public Rol selectById(Long id) {
        return rolRepository.selectById(id);
    }
}
