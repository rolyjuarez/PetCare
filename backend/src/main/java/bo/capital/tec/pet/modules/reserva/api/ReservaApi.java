package bo.capital.tec.pet.modules.reserva.api;

import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;

public interface ReservaApi {
    Reserva selectById(Long id);
    long countByProveedorId(Long proveedorId);
    void softDeleteByMascotaId(Long mascotaId);
}
