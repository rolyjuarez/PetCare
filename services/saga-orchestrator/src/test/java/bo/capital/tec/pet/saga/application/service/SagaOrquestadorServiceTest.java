package bo.capital.tec.pet.saga.application.service;

import bo.capital.tec.pet.saga.domain.model.CancelarReservaCommand;
import bo.capital.tec.pet.saga.domain.model.ConfirmarReservaCommand;
import bo.capital.tec.pet.saga.domain.model.CrearPagoCommand;
import bo.capital.tec.pet.saga.domain.model.EstadoSaga;
import bo.capital.tec.pet.saga.domain.model.LiberarDescuentoCommand;
import bo.capital.tec.pet.saga.domain.model.NotificarProveedorCommand;
import bo.capital.tec.pet.saga.domain.model.PagoFallidoEvent;
import bo.capital.tec.pet.saga.domain.model.PagoProcesadoEvent;
import bo.capital.tec.pet.saga.domain.model.PagoReembolsadoEvent;
import bo.capital.tec.pet.saga.domain.model.RechazarReservaCommand;
import bo.capital.tec.pet.saga.domain.model.ReservaAceptadaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaCanceladaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaConfirmadaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaCreadaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaRechazadaEvent;
import bo.capital.tec.pet.saga.domain.model.SagaCommand;
import bo.capital.tec.pet.saga.domain.model.SagaEstado;
import bo.capital.tec.pet.saga.domain.port.out.EventoProcesadoRepository;
import bo.capital.tec.pet.saga.domain.port.out.SagaCommandPublisher;
import bo.capital.tec.pet.saga.domain.port.out.SagaEstadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SagaOrquestadorServiceTest {

    @Mock
    private SagaEstadoRepository sagaRepository;
    @Mock
    private SagaCommandPublisher commandPublisher;
    @Mock
    private EventoProcesadoRepository eventoProcesadoRepository;

    private SagaOrquestadorService servicio;

    @BeforeEach
    void setUp() {
        IdempotenciaSagaService idempotencia = new IdempotenciaSagaService(eventoProcesadoRepository);
        servicio = new SagaOrquestadorService(sagaRepository, commandPublisher, idempotencia);
    }

    @Test
    void onReservaCreadaIniciaSagaPublicaNotificacionYTransiciona() {
        ReservaCreadaEvent evento = reservaCreada("ev-1");
        when(eventoProcesadoRepository.existsByEventId("ev-1")).thenReturn(false);
        when(sagaRepository.findByReservaId(1L)).thenReturn(Optional.empty());

        servicio.onReservaCreada(evento);

        ArgumentCaptor<SagaEstado> estadoCaptor = ArgumentCaptor.forClass(SagaEstado.class);
        verify(sagaRepository).insert(estadoCaptor.capture());
        assertThat(estadoCaptor.getValue().getEstado()).isEqualTo(EstadoSaga.INICIADA);

        ArgumentCaptor<SagaCommand> comandoCaptor = ArgumentCaptor.forClass(SagaCommand.class);
        verify(commandPublisher).publicar(comandoCaptor.capture());
        assertThat(comandoCaptor.getValue()).isInstanceOf(NotificarProveedorCommand.class);
        assertThat(((NotificarProveedorCommand) comandoCaptor.getValue()).getReservaId()).isEqualTo(1L);

        verify(sagaRepository).updateEstado(eq(1L), eq(EstadoSaga.PROVEEDOR_NOTIFICADO),
                eq("Comando notificar-proveedor enviado a provider-service"), isNull(), eq("ev-1"));
    }

    @Test
    void onReservaAceptadaPublicaConfirmacion() {
        ReservaAceptadaEvent evento = reservaAceptada("ev-2");
        when(eventoProcesadoRepository.existsByEventId("ev-2")).thenReturn(false);

        servicio.onReservaAceptada(evento);

        ArgumentCaptor<SagaCommand> comandoCaptor = ArgumentCaptor.forClass(SagaCommand.class);
        verify(commandPublisher).publicar(comandoCaptor.capture());
        assertThat(comandoCaptor.getValue()).isInstanceOf(ConfirmarReservaCommand.class);
        assertThat(((ConfirmarReservaCommand) comandoCaptor.getValue()).getProveedorId()).isEqualTo(7L);

        verify(sagaRepository).updateEstado(eq(1L), eq(EstadoSaga.RESERVA_ACEPTADA), any(), isNull(), eq("ev-2"));
    }

    @Test
    void onReservaRechazadaPublicaRechazoConMotivo() {
        ReservaRechazadaEvent evento = reservaRechazada("ev-3");
        when(eventoProcesadoRepository.existsByEventId("ev-3")).thenReturn(false);

        servicio.onReservaRechazada(evento);

        ArgumentCaptor<SagaCommand> comandoCaptor = ArgumentCaptor.forClass(SagaCommand.class);
        verify(commandPublisher).publicar(comandoCaptor.capture());
        assertThat(comandoCaptor.getValue()).isInstanceOf(RechazarReservaCommand.class);
        assertThat(((RechazarReservaCommand) comandoCaptor.getValue()).getMotivo()).isEqualTo("No disponible");

        verify(sagaRepository).updateEstado(eq(1L), eq(EstadoSaga.RECHAZADA), any(),
                eq("No disponible"), eq("ev-3"));
    }

    @Test
    void onReservaConfirmadaSolicitaPago() {
        ReservaConfirmadaEvent evento = reservaConfirmada("ev-4");
        when(eventoProcesadoRepository.existsByEventId("ev-4")).thenReturn(false);

        servicio.onReservaConfirmada(evento);

        ArgumentCaptor<SagaCommand> comandoCaptor = ArgumentCaptor.forClass(SagaCommand.class);
        verify(commandPublisher).publicar(comandoCaptor.capture());
        assertThat(comandoCaptor.getValue()).isInstanceOf(CrearPagoCommand.class);
        assertThat(((CrearPagoCommand) comandoCaptor.getValue()).getPrecioTotal())
                .isEqualByComparingTo("80.00");

        verify(sagaRepository).updateEstado(eq(1L), eq(EstadoSaga.RESERVA_CONFIRMADA), any(), isNull(), eq("ev-4"));
        verify(sagaRepository).updateEstado(eq(1L), eq(EstadoSaga.PAGO_SOLICITADO), any(), isNull(), eq("ev-4"));
    }

    @Test
    void onReservaCanceladaRegistraCompensacion() {
        ReservaCanceladaEvent evento = reservaCancelada("ev-5");
        when(eventoProcesadoRepository.existsByEventId("ev-5")).thenReturn(false);

        servicio.onReservaCancelada(evento);

        verify(sagaRepository).updateEstado(eq(1L), eq(EstadoSaga.CANCELADA), any(),
                eq("Cliente canceló"), eq("ev-5"));
        verify(commandPublisher, never()).publicar(any());
    }

    @Test
    void onPagoProcesadoCompletaSaga() {
        PagoProcesadoEvent evento = pagoProcesado("ev-6");
        when(eventoProcesadoRepository.existsByEventId("ev-6")).thenReturn(false);

        servicio.onPagoProcesado(evento);

        verify(sagaRepository).updateEstado(eq(1L), eq(EstadoSaga.COMPLETADA), any(), isNull(), eq("ev-6"));
        verify(commandPublisher, never()).publicar(any());
    }

    @Test
    void onPagoFallidoEjecutaCompensacionCompleta() {
        PagoFallidoEvent evento = pagoFallido("ev-7", "Tarjeta rechazada");
        when(eventoProcesadoRepository.existsByEventId("ev-7")).thenReturn(false);

        servicio.onPagoFallido(evento);

        verify(sagaRepository).updateEstado(eq(1L), eq(EstadoSaga.PAGO_FALLIDO), any(),
                eq("Tarjeta rechazada"), eq("ev-7"));

        ArgumentCaptor<SagaCommand> comandoCaptor = ArgumentCaptor.forClass(SagaCommand.class);
        verify(commandPublisher, times(2)).publicar(comandoCaptor.capture());
        assertThat(comandoCaptor.getAllValues())
                .hasSize(2)
                .anyMatch(c -> c instanceof CancelarReservaCommand)
                .anyMatch(c -> c instanceof LiberarDescuentoCommand);
        CancelarReservaCommand cancelar = (CancelarReservaCommand) comandoCaptor.getAllValues().stream()
                .filter(c -> c instanceof CancelarReservaCommand).findFirst().orElseThrow();
        assertThat(cancelar.getMotivo()).isEqualTo("Tarjeta rechazada");
    }

    @Test
    void onPagoFallidoSinMotivoUsaMensajePorDefecto() {
        PagoFallidoEvent evento = pagoFallido("ev-8", null);
        when(eventoProcesadoRepository.existsByEventId("ev-8")).thenReturn(false);

        servicio.onPagoFallido(evento);

        verify(sagaRepository).updateEstado(eq(1L), eq(EstadoSaga.PAGO_FALLIDO), any(),
                eq("El pago no pudo completarse"), eq("ev-8"));
    }

    @Test
    void onPagoReembolsadoLiberaDescuento() {
        PagoReembolsadoEvent evento = pagoReembolsado("ev-9");
        when(eventoProcesadoRepository.existsByEventId("ev-9")).thenReturn(false);

        servicio.onPagoReembolsado(evento);

        verify(sagaRepository).updateEstado(eq(1L), eq(EstadoSaga.PAGO_REEMBOLSADO), any(), isNull(), eq("ev-9"));
        ArgumentCaptor<SagaCommand> comandoCaptor = ArgumentCaptor.forClass(SagaCommand.class);
        verify(commandPublisher).publicar(comandoCaptor.capture());
        assertThat(comandoCaptor.getValue()).isInstanceOf(LiberarDescuentoCommand.class);
        assertThat(((LiberarDescuentoCommand) comandoCaptor.getValue()).getReservaId()).isEqualTo(1L);
    }

    @Test
    void eventoDuplicadoEsIgnorado() {
        ReservaCreadaEvent evento = reservaCreada("ev-dup");
        when(eventoProcesadoRepository.existsByEventId("ev-dup")).thenReturn(true);

        servicio.onReservaCreada(evento);

        verify(sagaRepository, never()).insert(any());
        verify(sagaRepository, never()).updateEstado(any(), any(), any(), any(), any());
        verify(commandPublisher, never()).publicar(any());
    }

    @Test
    void sagaYaIniciadaNoReinserta() {
        ReservaCreadaEvent evento = reservaCreada("ev-10");
        when(eventoProcesadoRepository.existsByEventId("ev-10")).thenReturn(false);
        when(sagaRepository.findByReservaId(1L)).thenReturn(Optional.of(SagaEstado.iniciar(
                1L, "RES-1", EstadoSaga.INICIADA, "ya iniciada", "ev-anterior")));

        servicio.onReservaCreada(evento);

        verify(sagaRepository, never()).insert(any());
        verify(commandPublisher).publicar(any(NotificarProveedorCommand.class));
    }

    private ReservaCreadaEvent reservaCreada(String eventId) {
        ReservaCreadaEvent evento = new ReservaCreadaEvent(
                1L, "RES-100", 2L, "Ana", "ana@mail.com",
                7L, "VetCorp", 3L, "Consulta", 4L, "Rex",
                LocalDate.now().plusDays(2), LocalTime.of(10, 0),
                new BigDecimal("80.00"), 5L, "EN_ESTABLECIMIENTO");
        evento.setEventId(eventId);
        return evento;
    }

    private ReservaAceptadaEvent reservaAceptada(String eventId) {
        ReservaAceptadaEvent evento = new ReservaAceptadaEvent(
                1L, "RES-100", 7L, "Dr. Juan", "VetCorp",
                "Consulta", Instant.now(), "Aceptada");
        evento.setEventId(eventId);
        return evento;
    }

    private ReservaRechazadaEvent reservaRechazada(String eventId) {
        ReservaRechazadaEvent evento = new ReservaRechazadaEvent(
                1L, "RES-100", 7L, "Dr. Juan", "VetCorp",
                "Consulta", "No disponible", Instant.now());
        evento.setEventId(eventId);
        return evento;
    }

    private ReservaConfirmadaEvent reservaConfirmada(String eventId) {
        ReservaConfirmadaEvent evento = new ReservaConfirmadaEvent(
                1L, "RES-100", 2L, 7L, 3L, 4L,
                LocalDate.now().plusDays(2), LocalTime.of(10, 0),
                new BigDecimal("80.00"), "EN_ESTABLECIMIENTO", 5L);
        evento.setEventId(eventId);
        return evento;
    }

    private ReservaCanceladaEvent reservaCancelada(String eventId) {
        ReservaCanceladaEvent evento = new ReservaCanceladaEvent(
                1L, "RES-100", 2L, "Ana", "ana@mail.com",
                7L, "VetCorp", 3L, "Consulta", "Cliente canceló");
        evento.setEventId(eventId);
        return evento;
    }

    private PagoProcesadoEvent pagoProcesado(String eventId) {
        PagoProcesadoEvent evento = new PagoProcesadoEvent(
                50L, 1L, new BigDecimal("80.00"), new BigDecimal("100.00"),
                new BigDecimal("20.00"), "TARJETA", "APROBADO", "REF-1");
        evento.setEventId(eventId);
        return evento;
    }

    private PagoFallidoEvent pagoFallido(String eventId, String motivo) {
        PagoFallidoEvent evento = new PagoFallidoEvent(
                50L, 1L, new BigDecimal("80.00"), "INT-1", motivo);
        evento.setEventId(eventId);
        return evento;
    }

    private PagoReembolsadoEvent pagoReembolsado(String eventId) {
        PagoReembolsadoEvent evento = new PagoReembolsadoEvent(
                50L, 1L, new BigDecimal("80.00"), "REF-REEMB");
        evento.setEventId(eventId);
        return evento;
    }
}
