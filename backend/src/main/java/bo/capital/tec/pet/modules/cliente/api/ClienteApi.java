package bo.capital.tec.pet.modules.cliente.api;

import bo.capital.tec.pet.modules.cliente.entity.Cliente;

public interface ClienteApi {
    Cliente selectById(Long id);
    Cliente findByPersonaId(Long personaId);
    Long insert(Cliente cliente);
}
