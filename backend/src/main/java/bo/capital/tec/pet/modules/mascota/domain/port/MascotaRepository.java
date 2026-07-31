package bo.capital.tec.pet.modules.mascota.domain.port;

import bo.capital.tec.pet.modules.mascota.dto.MascotaSummaryDTO;
import bo.capital.tec.pet.modules.mascota.domain.model.Mascota;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface MascotaRepository {
Long insert(Mascota mascota);
    Mascota selectById(@Param("id") Long id);
    List<MascotaSummaryDTO> selectAll(@Param("nombre") String nombre, @Param("clienteId") Long clienteId, @Param("especieId") Long especieId, @Param("offset") int offset, @Param("limit") int limit);
    long countAll(@Param("nombre") String nombre, @Param("clienteId") Long clienteId, @Param("especieId") Long especieId);
    List<MascotaSummaryDTO> selectByClienteId(@Param("clienteId") Long clienteId, @Param("offset") int offset, @Param("limit") int limit);
    long countByClienteId(@Param("clienteId") Long clienteId);
    void update(Mascota mascota);
    void softDelete(@Param("id") Long id);
}
