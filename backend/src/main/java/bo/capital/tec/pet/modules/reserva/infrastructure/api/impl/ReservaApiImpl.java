package bo.capital.tec.pet.modules.reserva.infrastructure.api.impl;

import bo.capital.tec.pet.modules.reserva.api.ReservaApi;
import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.port.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservaApiImpl implements ReservaApi {

    private final ReservaRepository reservaRepository;

    @Override
    public Reserva selectById(Long id) {
        return reservaRepository.selectById(id);
    }

    @Override
    public long countByProveedorId(Long proveedorId) {
        return reservaRepository.countByProveedorId(proveedorId);
    }

    @Override
    public void softDeleteByMascotaId(Long mascotaId) {
        reservaRepository.softDeleteByMascotaId(mascotaId);
    }
}
