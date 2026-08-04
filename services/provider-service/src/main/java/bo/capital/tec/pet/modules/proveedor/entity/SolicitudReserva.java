package bo.capital.tec.pet.modules.proveedor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class SolicitudReserva {
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
    private String modalidadEntrega;
    private String estado;
    private String motivoRechazo;
    private Long registroVacunacionId;
    private LocalDateTime creadaEn;
    private LocalDateTime respondidaEn;
}
