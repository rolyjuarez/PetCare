package bo.capital.tec.pet.cliente.mapper;

import bo.capital.tec.pet.cliente.entity.Cliente;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ClienteMapper {
    Long insert(Cliente cliente);
    Cliente selectById(@Param("id") Long id);
    List<Cliente> selectAll(@Param("nombre") String nombre, @Param("ci") String ci, @Param("offset") int offset, @Param("limit") int limit);
    long countAll(@Param("nombre") String nombre, @Param("ci") String ci);
    void update(Cliente cliente);
    void softDelete(@Param("id") Long id);
    Cliente findByPersonaId(@Param("personaId") Long personaId);
    Cliente findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
