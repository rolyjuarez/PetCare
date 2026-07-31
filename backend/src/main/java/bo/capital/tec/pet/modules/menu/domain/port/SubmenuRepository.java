package bo.capital.tec.pet.modules.menu.domain.port;

import bo.capital.tec.pet.modules.menu.domain.model.Submenu;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SubmenuRepository {
Long insert(Submenu submenu);
    Submenu selectById(@Param("id") Long id);
    List<Submenu> selectByMenuId(@Param("menuId") Long menuId);
    List<Submenu> selectByMenuIdAndUsername(@Param("menuId") Long menuId, @Param("username") String username);
    void update(Submenu submenu);
    void softDelete(@Param("id") Long id);
}
