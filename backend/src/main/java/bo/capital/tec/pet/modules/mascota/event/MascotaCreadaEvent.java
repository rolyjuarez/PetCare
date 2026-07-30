package bo.capital.tec.pet.modules.mascota.event;

import bo.capital.tec.pet.common.event.DomainEvent;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MascotaCreadaEvent extends DomainEvent {

    private Long mascotaId;
    private String nombre;
    private Long clienteId;

    public MascotaCreadaEvent(Long mascotaId, String nombre, Long clienteId) {
        super("MASCOTA", mascotaId);
        this.mascotaId = mascotaId;
        this.nombre = nombre;
        this.clienteId = clienteId;
    }
}
