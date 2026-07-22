package bo.capital.tec.pet.common.exceptions;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
    private final String entity;
    
    public EntityNotFoundException(String entity, Object id) {
        super(String.format("%s no encontrado con id: %s", entity, id));
        this.entity = entity;
    }
    
    public EntityNotFoundException(String entity, String field, Object value) {
        super(String.format("%s no encontrado con %s: %s", entity, field, value));
        this.entity = entity;
    }
}
