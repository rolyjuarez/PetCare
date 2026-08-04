package bo.capital.tec.pet.modules.promocion.event;

import bo.capital.tec.pet.common.event.DomainEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromocionCreadaEvent extends DomainEvent {

    private Long promocionId;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String tipoDescuento;
    private BigDecimal valorDescuento;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long proveedorId;

    public PromocionCreadaEvent(Long promocionId, String codigo, String nombre, String descripcion,
                                String tipoDescuento, BigDecimal valorDescuento,
                                LocalDateTime fechaInicio, LocalDateTime fechaFin, Long proveedorId) {
        super("PROMOCION", promocionId);
        this.promocionId = promocionId;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoDescuento = tipoDescuento;
        this.valorDescuento = valorDescuento;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.proveedorId = proveedorId;
    }
}
