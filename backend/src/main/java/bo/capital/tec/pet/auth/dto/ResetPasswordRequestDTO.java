package bo.capital.tec.pet.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ResetPasswordRequestDTO {
    @NotBlank(message = "Token es requerido")
    private String token;

    @NotBlank(message = "Nueva contrasena es requerida")
    @Size(min = 6, max = 100, message = "Contrasena debe tener entre 6 y 100 caracteres")
    private String newPassword;
}
