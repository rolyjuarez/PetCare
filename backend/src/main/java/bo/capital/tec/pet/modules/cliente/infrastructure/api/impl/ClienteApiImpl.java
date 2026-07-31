package bo.capital.tec.pet.modules.cliente.infrastructure.api.impl;

import bo.capital.tec.pet.modules.cliente.api.ClienteApi;
import bo.capital.tec.pet.modules.cliente.domain.model.Cliente;
import bo.capital.tec.pet.modules.cliente.domain.port.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteApiImpl implements ClienteApi {

    private final ClienteRepository clienteRepository;

    @Override
    public Cliente selectById(Long id) {
        return clienteRepository.selectById(id);
    }

    @Override
    public Cliente findByPersonaId(Long personaId) {
        return clienteRepository.findByPersonaId(personaId);
    }

    @Override
    public Long insert(Cliente cliente) {
        return clienteRepository.insert(cliente);
    }
}
