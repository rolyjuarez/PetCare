package bo.capital.tec.pet.modules.rol.api.impl;

import bo.capital.tec.pet.modules.rol.api.RolApi;
import bo.capital.tec.pet.modules.rol.entity.Rol;
import bo.capital.tec.pet.modules.rol.mapper.RolMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RolApiImpl implements RolApi {

    private final RolMapper rolMapper;

    @Override
    public Rol selectByNombre(String nombre) {
        return rolMapper.selectByNombre(nombre);
    }

    @Override
    public Rol selectById(Long id) {
        return rolMapper.selectById(id);
    }
}
