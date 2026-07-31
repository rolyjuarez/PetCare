package bo.capital.tec.pet.modules.servicio.api;

import bo.capital.tec.pet.modules.servicio.domain.model.Servicio;

public interface ServicioApi {
    Servicio selectById(Long id);
}
