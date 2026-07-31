package bo.capital.tec.pet.modules.menu.infrastructure.api.impl;

import bo.capital.tec.pet.modules.menu.api.MenuApi;
import bo.capital.tec.pet.modules.menu.domain.model.Menu;
import bo.capital.tec.pet.modules.menu.domain.port.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuApiImpl implements MenuApi {

    private final MenuRepository menuRepository;

    @Override
    public List<Menu> findByRolId(Long rolId) {
        return menuRepository.findByRolId(rolId);
    }
}
