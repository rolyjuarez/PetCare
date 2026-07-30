package bo.capital.tec.pet.modules.reserva.api.impl;

import bo.capital.tec.pet.modules.reserva.api.ReservaApi;
import bo.capital.tec.pet.modules.reserva.entity.Reserva;
import bo.capital.tec.pet.modules.reserva.mapper.ReservaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservaApiImpl implements ReservaApi {

    private final ReservaMapper reservaMapper;

    @Override
    public long countByProveedorId(Long proveedorId) {
        return reservaMapper.countByProveedorId(proveedorId);
    }

    @Override
    public Reserva selectById(Long id) {
        return reservaMapper.selectById(id);
    }

    @Override
    public void softDeleteByMascotaId(Long mascotaId) {
        reservaMapper.softDeleteByMascotaId(mascotaId);
    }
}
