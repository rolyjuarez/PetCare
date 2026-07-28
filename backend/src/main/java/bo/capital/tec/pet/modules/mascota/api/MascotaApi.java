package bo.capital.tec.pet.modules.mascota.api;

import bo.capital.tec.pet.modules.mascota.entity.Mascota;

public interface MascotaApi {
    Mascota selectById(Long id);
}
