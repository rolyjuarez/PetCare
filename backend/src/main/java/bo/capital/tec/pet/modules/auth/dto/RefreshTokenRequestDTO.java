package bo.capital.tec.pet.modules.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RefreshTokenRequestDTO {
    @NotBlank
    private String refreshToken;
}
