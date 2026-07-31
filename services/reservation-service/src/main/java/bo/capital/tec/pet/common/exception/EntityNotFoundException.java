package bo.capital.tec.pet.common.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entity, Long id) {
        super(entity + " no encontrado con id: " + id);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
