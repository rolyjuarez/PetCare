package bo.capital.tec.pet.common.security;

import lombok.Data;

@Data
public class UsuarioAuth {
    private Long id;
    private String username;
    private String password;
    private Long personaId;
    private String personaNombre;
    private String personaPrimerApellido;
    private String email;
    private Boolean activo;
    private Boolean bloqueado;
}
