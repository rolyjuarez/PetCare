package bo.capital.tec.pet.modules.reserva.mapper;

import bo.capital.tec.pet.modules.reserva.entity.Reserva;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ReservaMapper {
    Long insert(Reserva reserva);
    Reserva selectById(@Param("id") Long id);
    Reserva selectByCodigo(@Param("codigo") String codigo);
    List<Reserva> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    List<Reserva> selectByClienteId(@Param("clienteId") Long clienteId, @Param("offset") int offset, @Param("limit") int limit);
    long countByClienteId(@Param("clienteId") Long clienteId);
    List<Reserva> selectByProveedorId(@Param("proveedorId") Long proveedorId, @Param("offset") int offset, @Param("limit") int limit);
    long countByProveedorId(@Param("proveedorId") Long proveedorId);
    List<Reserva> selectByEstado(@Param("estadoReservaId") Long estadoReservaId, @Param("offset") int offset, @Param("limit") int limit);
    long countByEstado(@Param("estadoReservaId") Long estadoReservaId);
    void update(Reserva reserva);
    void updateEstado(@Param("id") Long id, @Param("estadoReservaId") Long estadoReservaId);
    void updateProveedor(@Param("id") Long id, @Param("proveedorId") Long proveedorId);
    void softDelete(@Param("id") Long id);
    void softDeleteByMascotaId(@Param("mascotaId") Long mascotaId);
}
