package bo.capital.tec.pet.modules.reserva.infrastructure.adapter.in.kafka;

import bo.capital.tec.pet.common.kafka.IdempotencyService;
import bo.capital.tec.pet.modules.reserva.application.command.ConfirmarReservaCommand;
import bo.capital.tec.pet.modules.reserva.application.command.RechazarReservaCommand;
import bo.capital.tec.pet.modules.reserva.application.command.ReservaCommandUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmarRechazarCommandConsumerTest {

    @Mock
    private ReservaCommandUseCase reservaCommandUseCase;
    @Mock
    private IdempotencyService idempotencyService;
    @Mock
    private Acknowledgment ack;

    @InjectMocks
    private ConfirmarRechazarCommandConsumer consumer;

    @Test
    void onConfirmarReservaDelegaCuandoEsNuevo() {
        ConfirmarReservaCommand comando =
                new ConfirmarReservaCommand(7L, "RES-X", 3L, "Juan", "VetPet SRL", "Consulta", null);
        when(idempotencyService.tryMarkProcessed(eq(comando.getCommandId()), eq("ConfirmarReservaCommand"),
                eq(7L), eq("reservation-service"), eq(comando))).thenReturn(true);

        consumer.onConfirmarReserva(comando, ack);

        verify(reservaCommandUseCase).confirmar(comando);
        verify(ack).acknowledge();
    }

    @Test
    void onConfirmarReservaIgnoraDuplicado() {
        ConfirmarReservaCommand comando =
                new ConfirmarReservaCommand(7L, "RES-X", 3L, "Juan", "VetPet SRL", "Consulta", null);
        when(idempotencyService.tryMarkProcessed(any(), any(), any(), any(), any())).thenReturn(false);

        consumer.onConfirmarReserva(comando, ack);

        verify(reservaCommandUseCase, never()).confirmar(any(ConfirmarReservaCommand.class));
        verify(ack).acknowledge();
    }

    @Test
    void onRechazarReservaDelegaCuandoEsNuevo() {
        RechazarReservaCommand comando = new RechazarReservaCommand(
                7L, "RES-X", 3L, "Juan", "VetPet SRL", "Consulta", "Horario no disponible");
        when(idempotencyService.tryMarkProcessed(eq(comando.getCommandId()), eq("RechazarReservaCommand"),
                eq(7L), eq("reservation-service"), eq(comando))).thenReturn(true);

        consumer.onRechazarReserva(comando, ack);

        verify(reservaCommandUseCase).rechazar(comando);
        verify(ack).acknowledge();
    }

    @Test
    void onRechazarReservaIgnoraDuplicado() {
        RechazarReservaCommand comando = new RechazarReservaCommand(
                7L, "RES-X", 3L, "Juan", "VetPet SRL", "Consulta", "Horario no disponible");
        when(idempotencyService.tryMarkProcessed(any(), any(), any(), any(), any())).thenReturn(false);

        consumer.onRechazarReserva(comando, ack);

        verify(reservaCommandUseCase, never()).rechazar(any(RechazarReservaCommand.class));
        verify(ack).acknowledge();
    }
}
