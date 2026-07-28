package bo.capital.tec.pet.modules.direccion.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DireccionRequestDTO {
    @NotBlank @Size(max = 200)
    private String calle;
    @Size(max = 20)
    private String numero;
    @Size(max = 10)
    private String piso;
    @Size(max = 10)
    private String apartamento;
    private BigDecimal latitud;
    private BigDecimal longitud;
    @Size(max = 300)
    private String referencia;
    @NotNull
    private Long ciudadId;
    private Long estadoId;
}
