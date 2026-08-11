package bo.capital.tec.pet.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PagoRequestDTO {
    @NotNull
    private Long reservaId;
    @NotNull @DecimalMin("0.01")
    private BigDecimal monto;
    @NotBlank @Pattern(regexp = "^(EFECTIVO|TARJETA_CREDITO|TARJETA_DEBITO|TRANSFERENCIA|QR)$")
    private String metodoPago;
    private String referenciaTransaccion;
    private String comprobanteUrl;
}
