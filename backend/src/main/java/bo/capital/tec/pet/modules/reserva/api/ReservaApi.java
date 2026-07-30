package bo.capital.tec.pet.modules.reserva.api;

import bo.capital.tec.pet.modules.reserva.entity.Reserva;

public interface ReservaApi {
    long countByProveedorId(Long proveedorId);
    Reserva selectById(Long id);
    void softDeleteByMascotaId(Long mascotaId);
}
