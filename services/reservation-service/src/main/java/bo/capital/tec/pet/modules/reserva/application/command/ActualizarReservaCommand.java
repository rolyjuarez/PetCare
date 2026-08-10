package bo.capital.tec.pet.modules.reserva.application.command;

import bo.capital.tec.pet.modules.reserva.dto.ReservaRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Comando de escritura para actualizar los datos de una reserva existente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarReservaCommand {

    private Long clienteId;
    private Long proveedorId;
    private Long servicioId;
    private Long mascotaId;
    private LocalDate fechaReserva;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Long registroVacunacionId;
    private String modalidadEntrega;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String direccionReferencia;
    private String notas;
    private BigDecimal precioTotal;

    public CrearReservaCommand toCrearReservaCommand() {
        return CrearReservaCommand.builder()
                .clienteId(clienteId)
                .proveedorId(proveedorId)
                .servicioId(servicioId)
                .mascotaId(mascotaId)
                .fechaReserva(fechaReserva)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .horaInicio(horaInicio)
                .horaFin(horaFin)
                .registroVacunacionId(registroVacunacionId)
                .modalidadEntrega(modalidadEntrega)
                .latitud(latitud)
                .longitud(longitud)
                .direccionReferencia(direccionReferencia)
                .notas(notas)
                .precioTotal(precioTotal)
                .build();
    }

    public static ActualizarReservaCommand desde(ReservaRequestDTO dto) {
        return ActualizarReservaCommand.builder()
                .clienteId(dto.getClienteId())
                .proveedorId(dto.getProveedorId())
                .servicioId(dto.getServicioId())
                .mascotaId(dto.getMascotaId())
                .fechaReserva(dto.getFechaReserva())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .horaInicio(dto.getHoraInicio())
                .horaFin(dto.getHoraFin())
                .registroVacunacionId(dto.getRegistroVacunacionId())
                .modalidadEntrega(dto.getModalidadEntrega())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .direccionReferencia(dto.getDireccionReferencia())
                .notas(dto.getNotas())
                .precioTotal(dto.getPrecioTotal())
                .build();
    }
}
