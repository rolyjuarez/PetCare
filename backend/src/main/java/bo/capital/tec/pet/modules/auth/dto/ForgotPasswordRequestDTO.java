package bo.capital.tec.pet.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ForgotPasswordRequestDTO {
    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser valido")
    private String email;
}
