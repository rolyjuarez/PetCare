package bo.capital.tec.pet.modules.auth.event;

import bo.capital.tec.pet.common.event.DomainEvent;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsuarioRegistradoEvent extends DomainEvent {

    private Long usuarioId;
    private String username;
    private Long personaId;
    private String nombre;
    private String email;
    private Long clienteId;

    public UsuarioRegistradoEvent(Long usuarioId, String username, Long personaId,
                                  String nombre, String email, Long clienteId) {
        super("USUARIO", usuarioId);
        this.usuarioId = usuarioId;
        this.username = username;
        this.personaId = personaId;
        this.nombre = nombre;
        this.email = email;
        this.clienteId = clienteId;
    }
}
