package bo.capital.tec.pet.modules.rol.api;

import bo.capital.tec.pet.modules.rol.domain.model.Rol;

public interface RolApi {
    Rol selectByNombre(String nombre);
    Rol selectById(Long id);
}
