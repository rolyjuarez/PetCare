package bo.capital.tec.pet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PromocionRequestDTO {
    @NotBlank @Size(max = 20)
    private String codigo;
    @NotBlank @Size(max = 100)
    private String nombre;
    @Size(max = 500)
    private String descripcion;
    @NotBlank @Pattern(regexp = "^(PERCENTAGE|FIXED)$")
    private String tipoDescuento;
    @NotNull @DecimalMin("0.01")
    private BigDecimal valorDescuento;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaInicio;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaFin;
    private Boolean activa;
    @Min(1)
    private Integer limiteUsos;
}
