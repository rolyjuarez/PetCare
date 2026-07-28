package bo.capital.tec.pet.modules.proveedor.api;

import bo.capital.tec.pet.modules.proveedor.entity.Proveedor;

public interface ProveedorApi {
    Proveedor selectById(Long id);
}
