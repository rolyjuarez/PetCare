package bo.capital.tec.pet.saga.domain.model;

/**
 * Estados de la saga orquestada para una reserva.
 */
public enum EstadoSaga {
    INICIADA,
    PROVEEDOR_NOTIFICADO,
    RESERVA_ACEPTADA,
    RESERVA_CONFIRMADA,
    PAGO_SOLICITADO,
    PAGO_FALLIDO,
    COMPLETADA,
    PAGO_REEMBOLSADO,
    RECHAZADA,
    CANCELADA
}
