package bo.capital.tec.pet.modules.proveedor.mapper;

import bo.capital.tec.pet.modules.proveedor.entity.SolicitudReserva;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SolicitudReservaMapper {

    Long insert(SolicitudReserva solicitud);

    SolicitudReserva selectById(@Param("id") Long id);

    List<SolicitudReserva> selectByReservaId(@Param("reservaId") Long reservaId);

    boolean existsByReservaYProveedor(@Param("reservaId") Long reservaId, @Param("proveedorId") Long proveedorId);

    List<SolicitudReserva> selectByProveedor(@Param("proveedorId") Long proveedorId,
                                             @Param("estado") String estado,
                                             @Param("offset") int offset,
                                             @Param("limit") int limit);

    long countByProveedor(@Param("proveedorId") Long proveedorId, @Param("estado") String estado);

    void updateEstado(@Param("id") Long id,
                      @Param("estado") String estado,
                      @Param("motivoRechazo") String motivoRechazo);
}
