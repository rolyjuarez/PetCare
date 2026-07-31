package bo.capital.tec.pet.modules.auth.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserInfoDTO userInfo;
}
