package bo.capital.tec.pet.modules.persona.infrastructure.api.impl;

import bo.capital.tec.pet.modules.persona.api.PersonaApi;
import bo.capital.tec.pet.modules.persona.domain.model.Persona;
import bo.capital.tec.pet.modules.persona.domain.port.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonaApiImpl implements PersonaApi {

    private final PersonaRepository personaRepository;

    @Override
    public Persona selectById(Long id) {
        return personaRepository.selectById(id);
    }

    @Override
    public Persona selectByCi(String ci) {
        return personaRepository.selectByCi(ci);
    }

    @Override
    public Persona selectByEmail(String email) {
        return personaRepository.selectByEmail(email);
    }

    @Override
    public Long insert(Persona persona) {
        return personaRepository.insert(persona);
    }

    @Override
    public void update(Persona persona) {
        personaRepository.update(persona);
    }
}
