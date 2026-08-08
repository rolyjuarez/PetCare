package bo.capital.tec.pet.saga.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Proyección persistida del estado global de una saga de reserva.
 * Entidad de dominio, sin dependencias de infraestructura.
 */
@Getter
@Setter
public class SagaEstado {

    private Long id;
    private Long reservaId;
    private String codigoReserva;
    private EstadoSaga estado;
    private String pasoActual;
    private String motivo;
    private String ultimoEventoId;
    private LocalDateTime creadaEn;
    private LocalDateTime actualizadaEn;

    public static SagaEstado iniciar(Long reservaId, String codigoReserva, EstadoSaga estado,
                                     String pasoActual, String eventoId) {
        SagaEstado saga = new SagaEstado();
        saga.setReservaId(reservaId);
        saga.setCodigoReserva(codigoReserva);
        saga.setEstado(estado);
        saga.setPasoActual(pasoActual);
        saga.setUltimoEventoId(eventoId);
        saga.setCreadaEn(LocalDateTime.now());
        saga.setActualizadaEn(LocalDateTime.now());
        return saga;
    }
}
