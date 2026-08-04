package bo.capital.tec.pet.modules.pago.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaInfoDTO {
    private Long reservaId;
    private String codigo;
    private Long clienteId;
    private Long proveedorId;
    private Long servicioId;
    private String modalidadEntrega;
    private BigDecimal precioTotal;
    private LocalDate fechaInicio;
    private LocalTime horaInicio;
}
