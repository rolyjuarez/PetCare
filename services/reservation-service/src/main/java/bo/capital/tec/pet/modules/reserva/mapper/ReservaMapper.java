package bo.capital.tec.pet.modules.reserva.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservaMapper {

    Long insert(bo.capital.tec.pet.modules.reserva.entity.Reserva reserva);

    bo.capital.tec.pet.modules.reserva.entity.Reserva selectById(@Param("id") Long id);

    bo.capital.tec.pet.modules.reserva.entity.Reserva selectByCodigo(@Param("codigo") String codigo);

    List<bo.capital.tec.pet.modules.reserva.entity.Reserva> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    long countAll();

    List<bo.capital.tec.pet.modules.reserva.entity.Reserva> selectByClienteId(
            @Param("clienteId") Long clienteId, @Param("offset") int offset, @Param("limit") int limit);

    long countByClienteId(@Param("clienteId") Long clienteId);

    List<bo.capital.tec.pet.modules.reserva.entity.Reserva> selectByProveedorId(
            @Param("proveedorId") Long proveedorId, @Param("offset") int offset, @Param("limit") int limit);

    long countByProveedorId(@Param("proveedorId") Long proveedorId);

    List<bo.capital.tec.pet.modules.reserva.entity.Reserva> selectByEstado(
            @Param("estadoReservaId") Long estadoReservaId, @Param("offset") int offset, @Param("limit") int limit);

    long countByEstado(@Param("estadoReservaId") Long estadoReservaId);

    List<bo.capital.tec.pet.modules.reserva.entity.Reserva> selectBooked(
            @Param("proveedorId") Long proveedorId,
            @Param("servicioId") Long servicioId,
            @Param("desde") java.time.LocalDate desde,
            @Param("hasta") java.time.LocalDate hasta,
            @Param("excluirId") Long excluirId);

    void update(bo.capital.tec.pet.modules.reserva.entity.Reserva reserva);

    void updateEstado(@Param("id") Long id, @Param("estadoReservaId") Long estadoReservaId);

    void updateProveedor(@Param("id") Long id, @Param("proveedorId") Long proveedorId);

    void updateRespuesta(@Param("id") Long id, @Param("proveedorId") Long proveedorId,
                         @Param("estadoReservaId") Long estadoReservaId,
                         @Param("motivoRechazo") String motivoRechazo);

    void softDelete(@Param("id") Long id);

    void softDeleteByMascotaId(@Param("mascotaId") Long mascotaId);
}
