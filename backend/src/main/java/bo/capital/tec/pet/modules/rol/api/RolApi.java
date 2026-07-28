package bo.capital.tec.pet.modules.rol.api;

import bo.capital.tec.pet.modules.rol.entity.Rol;

public interface RolApi {
    Rol selectByNombre(String nombre);
    Rol selectById(Long id);
}
