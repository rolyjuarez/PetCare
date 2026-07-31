package bo.capital.tec.pet.modules.promocion.domain.port;

import bo.capital.tec.pet.modules.promocion.domain.model.PromocionLocal;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface PromocionLocalRepository {
Long insert(PromocionLocal pl);
    PromocionLocal selectById(@Param("id") Long id);
    List<PromocionLocal> selectByReservaId(@Param("reservaId") Long reservaId);
    List<PromocionLocal> selectAll(@Param("offset") int offset, @Param("limit") int limit);
}
