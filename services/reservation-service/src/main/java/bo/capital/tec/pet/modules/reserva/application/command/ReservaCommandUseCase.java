package bo.capital.tec.pet.modules.reserva.application.command;

import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;

/**
 * Casos de uso del lado escritura (CQRS write side) del módulo de reservas.
 */
public interface ReservaCommandUseCase {

    ReservaResponseDTO crear(CrearReservaCommand comando);

    ReservaResponseDTO actualizar(Long id, ActualizarReservaCommand comando);

    void eliminar(Long id);

    ReservaResponseDTO cancelar(Long id, String motivo);

    ReservaResponseDTO confirmar(ConfirmarReservaCommand comando);

    ReservaResponseDTO rechazar(RechazarReservaCommand comando);

    ReservaResponseDTO compensarPagoFallido(CancelarReservaCommand comando);
}
