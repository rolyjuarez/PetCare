package bo.capital.tec.pet.modules.proveedor.api;

import bo.capital.tec.pet.modules.proveedor.domain.model.Proveedor;

public interface ProveedorApi {
    Proveedor selectById(Long id);
}
