package bo.capital.tec.pet.modules.cliente.api.impl;

import bo.capital.tec.pet.modules.cliente.api.ClienteApi;
import bo.capital.tec.pet.modules.cliente.entity.Cliente;
import bo.capital.tec.pet.modules.cliente.mapper.ClienteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteApiImpl implements ClienteApi {

    private final ClienteMapper clienteMapper;

    @Override
    public Cliente selectById(Long id) {
        return clienteMapper.selectById(id);
    }

    @Override
    public Cliente findByPersonaId(Long personaId) {
        return clienteMapper.findByPersonaId(personaId);
    }

    @Override
    public Long insert(Cliente cliente) {
        return clienteMapper.insert(cliente);
    }
}
