package bo.capital.tec.pet.modules.menu.api.impl;

import bo.capital.tec.pet.modules.menu.api.MenuApi;
import bo.capital.tec.pet.modules.menu.entity.Menu;
import bo.capital.tec.pet.modules.menu.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuApiImpl implements MenuApi {

    private final MenuMapper menuMapper;

    @Override
    public List<Menu> findByRolId(Long rolId) {
        return menuMapper.findByRolId(rolId);
    }
}
