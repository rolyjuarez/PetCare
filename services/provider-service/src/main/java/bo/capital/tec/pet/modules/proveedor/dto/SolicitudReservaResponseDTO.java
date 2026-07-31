package bo.capital.tec.pet.modules.proveedor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudReservaResponseDTO {
    private Long id;
    private Long reservaId;
    private String codigo;
    private Long clienteId;
    private String clienteNombre;
    private Long proveedorId;
    private String proveedorEmpresa;
    private Long servicioId;
    private String servicioNombre;
    private Long mascotaId;
    private String mascotaNombre;
    private LocalDate fechaInicio;
    private LocalTime horaInicio;
    private BigDecimal precioTotal;
    private String estado;
    private String motivoRechazo;
    private LocalDateTime creadaEn;
    private LocalDateTime respondidaEn;
}
