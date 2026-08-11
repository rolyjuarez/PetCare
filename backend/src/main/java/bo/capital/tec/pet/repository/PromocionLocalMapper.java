package bo.capital.tec.pet.repository;

import bo.capital.tec.pet.domain.PromocionLocal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PromocionLocalMapper {
    Long insert(PromocionLocal pl);
    PromocionLocal selectById(@Param("id") Long id);
    List<PromocionLocal> selectByReservaId(@Param("reservaId") Long reservaId);
    List<PromocionLocal> selectAll(@Param("offset") int offset, @Param("limit") int limit);
}
