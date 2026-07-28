package bo.capital.tec.pet.modules.persona.api.impl;

import bo.capital.tec.pet.modules.persona.api.PersonaApi;
import bo.capital.tec.pet.modules.persona.entity.Persona;
import bo.capital.tec.pet.modules.persona.mapper.PersonaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonaApiImpl implements PersonaApi {

    private final PersonaMapper personaMapper;

    @Override
    public Persona selectById(Long id) {
        return personaMapper.selectById(id);
    }

    @Override
    public Persona selectByCi(String ci) {
        return personaMapper.selectByCi(ci);
    }

    @Override
    public Persona selectByEmail(String email) {
        return personaMapper.selectByEmail(email);
    }

    @Override
    public Long insert(Persona persona) {
        return personaMapper.insert(persona);
    }

    @Override
    public void update(Persona persona) {
        personaMapper.update(persona);
    }
}
