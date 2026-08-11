package bo.capital.tec.pet.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChangePasswordRequestDTO {
    @NotBlank
    private String oldPassword;
    @NotBlank @Size(min = 6, max = 100)
    private String newPassword;
}
