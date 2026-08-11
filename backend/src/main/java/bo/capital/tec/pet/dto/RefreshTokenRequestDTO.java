package bo.capital.tec.pet.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RefreshTokenRequestDTO {
    @NotBlank
    private String refreshToken;
}
