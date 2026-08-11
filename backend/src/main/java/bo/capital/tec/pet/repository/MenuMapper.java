package bo.capital.tec.pet.repository;

import bo.capital.tec.pet.domain.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MenuMapper {
    Long insert(Menu menu);
    Menu selectById(@Param("id") Long id);
    List<Menu> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    void update(Menu menu);
    void softDelete(@Param("id") Long id);
    List<Menu> findByRolId(@Param("rolId") Long rolId);
    List<Menu> findByUsername(@Param("username") String username);
}
