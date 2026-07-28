package bo.capital.tec.pet.modules.persona.api;

import bo.capital.tec.pet.modules.persona.entity.Persona;

public interface PersonaApi {
    Persona selectById(Long id);
    Persona selectByCi(String ci);
    Persona selectByEmail(String email);
    Long insert(Persona persona);
    void update(Persona persona);
}
