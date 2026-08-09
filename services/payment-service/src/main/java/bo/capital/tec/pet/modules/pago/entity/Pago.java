package bo.capital.tec.pet.modules.pago.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
    private Long id;
    private Long reservaId;
    private BigDecimal monto;
    private String metodoPago;
    private String estadoPago;
    private String referenciaTransaccion;
    private LocalDateTime fechaPago;
    private String comprobanteUrl;
    private String modalidadPago;
    private String intencionId;
    private BigDecimal montoOriginal;
    private BigDecimal descuentoTotal;
    private Long descuentoId;
    private String descuentoCodigo;
    private String descuentoNombre;
    private String descuentoTipo;
    private Long descuentoServicioId;
    private String estadoSync;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
