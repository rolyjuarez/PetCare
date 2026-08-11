package bo.capital.tec.pet.repository;

import bo.capital.tec.pet.domain.Submenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SubmenuMapper {
    Long insert(Submenu submenu);
    Submenu selectById(@Param("id") Long id);
    List<Submenu> selectByMenuId(@Param("menuId") Long menuId);
    List<Submenu> selectByMenuIdAndUsername(@Param("menuId") Long menuId, @Param("username") String username);
    void update(Submenu submenu);
    void softDelete(@Param("id") Long id);
}
