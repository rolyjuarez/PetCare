package bo.capital.tec.pet.modules.proveedor.api.impl;

import bo.capital.tec.pet.modules.proveedor.api.ProveedorApi;
import bo.capital.tec.pet.modules.proveedor.entity.Proveedor;
import bo.capital.tec.pet.modules.proveedor.mapper.ProveedorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProveedorApiImpl implements ProveedorApi {

    private final ProveedorMapper proveedorMapper;

    @Override
    public Proveedor selectById(Long id) {
        return proveedorMapper.selectById(id);
    }
}
