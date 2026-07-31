package bo.capital.tec.pet.modules.proveedor.infrastructure.api.impl;

import bo.capital.tec.pet.modules.proveedor.api.ProveedorApi;
import bo.capital.tec.pet.modules.proveedor.domain.model.Proveedor;
import bo.capital.tec.pet.modules.proveedor.domain.port.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProveedorApiImpl implements ProveedorApi {

    private final ProveedorRepository proveedorRepository;

    @Override
    public Proveedor selectById(Long id) {
        return proveedorRepository.selectById(id);
    }
}
