package bo.capital.tec.pet.modules.cliente.domain.port;

import bo.capital.tec.pet.modules.cliente.dto.ClienteSummaryDTO;
import bo.capital.tec.pet.modules.cliente.domain.model.Cliente;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ClienteRepository {
Long insert(Cliente cliente);
    Cliente selectById(@Param("id") Long id);
    List<ClienteSummaryDTO> selectAll(@Param("nombre") String nombre, @Param("ci") String ci, @Param("offset") int offset, @Param("limit") int limit);
    long countAll(@Param("nombre") String nombre, @Param("ci") String ci);
    void update(Cliente cliente);
    void softDelete(@Param("id") Long id);
    Cliente findByPersonaId(@Param("personaId") Long personaId);
    Cliente findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
