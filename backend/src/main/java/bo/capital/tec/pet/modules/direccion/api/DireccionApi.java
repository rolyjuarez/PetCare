package bo.capital.tec.pet.modules.direccion.api;

import bo.capital.tec.pet.modules.direccion.entity.Direccion;

public interface DireccionApi {
    Long insert(Direccion direccion);
    Direccion selectById(Long id);
    void update(Direccion direccion);
}
