package bo.capital.tec.pet.modules.reserva.infrastructure.adapter.in.kafka;

import bo.capital.tec.pet.common.kafka.IdempotencyService;
import bo.capital.tec.pet.modules.reserva.application.command.CancelarReservaCommand;
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
class CancelarReservaCommandConsumerTest {

    @Mock
    private ReservaCommandUseCase reservaCommandUseCase;
    @Mock
    private IdempotencyService idempotencyService;
    @Mock
    private Acknowledgment ack;

    @InjectMocks
    private CancelarReservaCommandConsumer consumer;

    @Test
    void onCancelarReservaDelegaCuandoEsNuevo() {
        CancelarReservaCommand comando = new CancelarReservaCommand(7L, "RES-X", "Pago rechazado");
        when(idempotencyService.tryMarkProcessed(eq(comando.getCommandId()), eq("CancelarReservaCommand"),
                eq(7L), eq("reservation-service"), eq(comando))).thenReturn(true);

        consumer.onCancelarReserva(comando, ack);

        verify(reservaCommandUseCase).compensarPagoFallido(comando);
        verify(ack).acknowledge();
    }

    @Test
    void onCancelarReservaIgnoraDuplicado() {
        CancelarReservaCommand comando = new CancelarReservaCommand(7L, "RES-X", "Pago rechazado");
        when(idempotencyService.tryMarkProcessed(any(), any(), any(), any(), any())).thenReturn(false);

        consumer.onCancelarReserva(comando, ack);

        verify(reservaCommandUseCase, never()).compensarPagoFallido(any(CancelarReservaCommand.class));
        verify(ack).acknowledge();
    }
}
