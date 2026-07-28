package bo.capital.tec.pet.modules.auth.dto;

import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String username;
    private String nombre;
    private List<String> roles;
    private List<String> permissions;
}
