package bo.capital.tec.pet.saga.infrastructure.adapter.out.persistence;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Registro persistido de la tabla {@code saga_estado}. Detalle de
 * infraestructura; el dominio trabaja con {@link bo.capital.tec.pet.saga.domain.model.SagaEstado}.
 */
@Data
public class SagaEstadoRecord {

    private Long id;
    private Long reservaId;
    private String codigoReserva;
    private String estado;
    private String pasoActual;
    private String motivo;
    private String ultimoEventoId;
    private LocalDateTime creadaEn;
    private LocalDateTime actualizadaEn;
}
