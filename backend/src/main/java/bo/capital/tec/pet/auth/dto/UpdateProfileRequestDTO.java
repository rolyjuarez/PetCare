package bo.capital.tec.pet.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateProfileRequestDTO {
    @Size(max = 100)
    private String nombre;
    @Size(max = 100)
    private String primerApellido;
    @Size(max = 100)
    private String segundoApellido;
    @Size(max = 20)
    private String telefono;
    @Size(max = 100)
    private String email;
}
