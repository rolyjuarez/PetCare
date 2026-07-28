package bo.capital.tec.pet.modules.auth.dto;

import lombok.*;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProfileResponseDTO {
    private Long usuarioId;
    private String username;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String ci;
    private String telefono;
    private String email;
    private LocalDate fechaNacimiento;
    private String genero;
    private java.util.List<String> roles;
}
