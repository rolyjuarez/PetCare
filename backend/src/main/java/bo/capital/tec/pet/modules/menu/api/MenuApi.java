package bo.capital.tec.pet.modules.menu.api;

import bo.capital.tec.pet.modules.menu.domain.model.Menu;

import java.util.List;

public interface MenuApi {
    List<Menu> findByRolId(Long rolId);
}
