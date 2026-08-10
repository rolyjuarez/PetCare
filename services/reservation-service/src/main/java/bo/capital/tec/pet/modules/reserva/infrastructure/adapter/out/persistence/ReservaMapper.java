package bo.capital.tec.pet.modules.reserva.infrastructure.adapter.out.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservaMapper {

    Long insert(bo.capital.tec.pet.modules.reserva.domain.model.Reserva reserva);

    bo.capital.tec.pet.modules.reserva.domain.model.Reserva selectById(@Param("id") Long id);

    List<bo.capital.tec.pet.modules.reserva.domain.model.Reserva> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    long countAll();

    List<bo.capital.tec.pet.modules.reserva.domain.model.Reserva> selectByClienteId(
            @Param("clienteId") Long clienteId, @Param("offset") int offset, @Param("limit") int limit);

    long countByProveedorId(@Param("proveedorId") Long proveedorId);

    List<bo.capital.tec.pet.modules.reserva.domain.model.Reserva> selectFiltered(
            @Param("clienteId") Long clienteId,
            @Param("mascotaId") Long mascotaId,
            @Param("servicioId") Long servicioId,
            @Param("estado") String estado,
            @Param("fechaDesde") java.time.LocalDate fechaDesde,
            @Param("fechaHasta") java.time.LocalDate fechaHasta,
            @Param("offset") int offset, @Param("limit") int limit);

    long countFiltered(
            @Param("clienteId") Long clienteId,
            @Param("mascotaId") Long mascotaId,
            @Param("servicioId") Long servicioId,
            @Param("estado") String estado,
            @Param("fechaDesde") java.time.LocalDate fechaDesde,
            @Param("fechaHasta") java.time.LocalDate fechaHasta);

    List<bo.capital.tec.pet.modules.reserva.domain.model.Reserva> selectBooked(
            @Param("proveedorId") Long proveedorId,
            @Param("servicioId") Long servicioId,
            @Param("desde") java.time.LocalDate desde,
            @Param("hasta") java.time.LocalDate hasta,
            @Param("excluirId") Long excluirId);

    void update(bo.capital.tec.pet.modules.reserva.domain.model.Reserva reserva);

    void updateEstado(@Param("id") Long id, @Param("estadoReservaId") Long estadoReservaId);

    void updateProveedor(@Param("id") Long id, @Param("proveedorId") Long proveedorId);

    void updateRespuesta(@Param("id") Long id, @Param("proveedorId") Long proveedorId,
                         @Param("estadoReservaId") Long estadoReservaId,
                         @Param("motivoRechazo") String motivoRechazo);

    void softDelete(@Param("id") Long id);

    void softDeleteByMascotaId(@Param("mascotaId") Long mascotaId);
}
