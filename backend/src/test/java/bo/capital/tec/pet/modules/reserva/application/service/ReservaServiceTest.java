package bo.capital.tec.pet.modules.reserva.application.service;

import bo.capital.tec.pet.modules.reserva.domain.model.EstadoReserva;
import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.port.EstadoReservaRepository;
import bo.capital.tec.pet.modules.reserva.domain.port.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private EstadoReservaRepository estadoReservaRepository;

    private Reserva reserva;
    private EstadoReserva estadoPendiente;
    private EstadoReserva estadoConfirmado;
    private EstadoReserva estadoEnProgreso;
    private EstadoReserva estadoFinalizado;
    private EstadoReserva estadoCancelado;

    @BeforeEach
    void setUp() {
        estadoPendiente = EstadoReserva.builder().id(1L).nombre("PENDIENTE").orden(1).build();
        estadoConfirmado = EstadoReserva.builder().id(2L).nombre("CONFIRMADO").orden(2).build();
        estadoEnProgreso = EstadoReserva.builder().id(3L).nombre("EN_PROGRESO").orden(3).build();
        estadoFinalizado = EstadoReserva.builder().id(4L).nombre("FINALIZADO").orden(4).build();
        estadoCancelado = EstadoReserva.builder().id(5L).nombre("CANCELADO").orden(5).build();

        reserva = Reserva.builder()
                .id(1L)
                .codigo("RES-001")
                .clienteId(1L)
                .proveedorId(1L)
                .servicioId(1L)
                .mascotaId(1L)
                .estadoReservaId(1L)
                .fechaReserva(LocalDate.of(2024, 1, 15))
                .fechaInicio(LocalDate.of(2024, 1, 20))
                .horaInicio(LocalTime.of(10, 0))
                .horaFin(LocalTime.of(11, 0))
                .precioTotal(new BigDecimal("150.00"))
                .deleted(false)
                .version(1)
                .build();
    }

    @Test
    void selectById_ShouldReturnReserva() {
        when(reservaRepository.selectById(1L)).thenReturn(reserva);

        Reserva found = reservaRepository.selectById(1L);

        assertNotNull(found);
        assertEquals("RES-001", found.getCodigo());
        assertEquals(1L, found.getClienteId());
        assertEquals(new BigDecimal("150.00"), found.getPrecioTotal());
    }

    @Test
    void selectById_ShouldReturnNull_WhenNotFound() {
        when(reservaRepository.selectById(99L)).thenReturn(null);

        Reserva found = reservaRepository.selectById(99L);

        assertNull(found);
    }

    @Test
    void selectByCodigo_ShouldReturnReserva() {
        when(reservaRepository.selectByCodigo("RES-001")).thenReturn(reserva);

        Reserva found = reservaRepository.selectByCodigo("RES-001");

        assertNotNull(found);
        assertEquals("RES-001", found.getCodigo());
    }

    @Test
    void insert_ShouldReturnId() {
        when(reservaRepository.insert(any(Reserva.class))).thenReturn(1L);

        Long id = reservaRepository.insert(Reserva.builder()
                .clienteId(1L)
                .proveedorId(1L)
                .servicioId(1L)
                .mascotaId(1L)
                .estadoReservaId(1L)
                .fechaReserva(LocalDate.of(2024, 1, 15))
                .fechaInicio(LocalDate.of(2024, 1, 20))
                .horaInicio(LocalTime.of(10, 0))
                .horaFin(LocalTime.of(11, 0))
                .precioTotal(new BigDecimal("150.00"))
                .build());

        assertNotNull(id);
        assertEquals(1L, id);
        verify(reservaRepository).insert(any(Reserva.class));
    }

    @Test
    void updateEstado_ShouldChangeReservaState() {
        reservaRepository.updateEstado(1L, 2L);

        verify(reservaRepository).updateEstado(1L, 2L);
    }

    @Test
    void updateEstado_FromPendienteToConfirmado() {
        when(reservaRepository.selectById(1L)).thenReturn(reserva);
        when(estadoReservaRepository.selectByNombre("CONFIRMADO")).thenReturn(estadoConfirmado);

        Reserva found = reservaRepository.selectById(1L);
        assertEquals(1L, found.getEstadoReservaId());

        EstadoReserva confirmado = estadoReservaRepository.selectByNombre("CONFIRMADO");
        reservaRepository.updateEstado(1L, confirmado.getId());

        verify(reservaRepository).updateEstado(1L, 2L);
    }

    @Test
    void updateEstado_FromConfirmadoToEnProgreso() {
        reserva.setEstadoReservaId(2L);
        when(reservaRepository.selectById(1L)).thenReturn(reserva);
        when(estadoReservaRepository.selectById(2L)).thenReturn(estadoConfirmado);
        when(estadoReservaRepository.selectByNombre("EN_PROGRESO")).thenReturn(estadoEnProgreso);

        Reserva found = reservaRepository.selectById(1L);
        EstadoReserva estadoActual = estadoReservaRepository.selectById(found.getEstadoReservaId());
        assertEquals("CONFIRMADO", estadoActual.getNombre());

        EstadoReserva nuevoEstado = estadoReservaRepository.selectByNombre("EN_PROGRESO");
        reservaRepository.updateEstado(1L, nuevoEstado.getId());

        verify(reservaRepository).updateEstado(1L, 3L);
    }

    @Test
    void updateEstado_FromEnProgresoToFinalizado() {
        reserva.setEstadoReservaId(3L);
        when(reservaRepository.selectById(1L)).thenReturn(reserva);
        when(estadoReservaRepository.selectById(3L)).thenReturn(estadoEnProgreso);
        when(estadoReservaRepository.selectByNombre("FINALIZADO")).thenReturn(estadoFinalizado);

        Reserva found = reservaRepository.selectById(1L);
        EstadoReserva estadoActual = estadoReservaRepository.selectById(found.getEstadoReservaId());
        assertEquals("EN_PROGRESO", estadoActual.getNombre());

        EstadoReserva nuevoEstado = estadoReservaRepository.selectByNombre("FINALIZADO");
        reservaRepository.updateEstado(1L, nuevoEstado.getId());

        verify(reservaRepository).updateEstado(1L, 4L);
    }

    @Test
    void updateEstado_FromPendienteToCancelado() {
        when(reservaRepository.selectById(1L)).thenReturn(reserva);
        when(estadoReservaRepository.selectById(1L)).thenReturn(estadoPendiente);
        when(estadoReservaRepository.selectByNombre("CANCELADO")).thenReturn(estadoCancelado);

        Reserva found = reservaRepository.selectById(1L);
        EstadoReserva estadoActual = estadoReservaRepository.selectById(found.getEstadoReservaId());
        assertEquals("PENDIENTE", estadoActual.getNombre());

        EstadoReserva nuevoEstado = estadoReservaRepository.selectByNombre("CANCELADO");
        reservaRepository.updateEstado(1L, nuevoEstado.getId());

        verify(reservaRepository).updateEstado(1L, 5L);
    }

    @Test
    void selectAll_ShouldReturnPagedResults() {
        when(reservaRepository.selectAll(0, 20)).thenReturn(List.of(reserva));
        when(reservaRepository.countAll()).thenReturn(1L);

        List<Reserva> reservas = reservaRepository.selectAll(0, 20);
        long total = reservaRepository.countAll();

        assertNotNull(reservas);
        assertEquals(1, reservas.size());
        assertEquals(1L, total);
    }

    @Test
    void selectByClienteId_ShouldReturnReservas() {
        when(reservaRepository.selectByClienteId(1L, 0, 20)).thenReturn(List.of(reserva));

        List<Reserva> reservas = reservaRepository.selectByClienteId(1L, 0, 20);

        assertNotNull(reservas);
        assertEquals(1, reservas.size());
        assertEquals("RES-001", reservas.get(0).getCodigo());
    }

    @Test
    void selectByProveedorId_ShouldReturnReservas() {
        when(reservaRepository.selectByProveedorId(1L, 0, 20)).thenReturn(List.of(reserva));

        List<Reserva> reservas = reservaRepository.selectByProveedorId(1L, 0, 20);

        assertNotNull(reservas);
        assertEquals(1, reservas.size());
    }

    @Test
    void softDelete_ShouldMarkAsDeleted() {
        reservaRepository.softDelete(1L);
        verify(reservaRepository).softDelete(1L);
    }

    @Test
    void selectByEstado_ShouldReturnReservas() {
        when(reservaRepository.selectByEstado(1L, 0, 20)).thenReturn(List.of(reserva));

        List<Reserva> reservas = reservaRepository.selectByEstado(1L, 0, 20);

        assertNotNull(reservas);
        assertEquals(1, reservas.size());
    }

    @Test
    void estadoReserva_selectById_ShouldReturnEstado() {
        when(estadoReservaRepository.selectById(1L)).thenReturn(estadoPendiente);

        EstadoReserva estado = estadoReservaRepository.selectById(1L);

        assertNotNull(estado);
        assertEquals("PENDIENTE", estado.getNombre());
        assertEquals(Integer.valueOf(1), estado.getOrden());
    }

    @Test
    void estadoReserva_selectByNombre_ShouldReturnEstado() {
        when(estadoReservaRepository.selectByNombre("CONFIRMADO")).thenReturn(estadoConfirmado);

        EstadoReserva estado = estadoReservaRepository.selectByNombre("CONFIRMADO");

        assertNotNull(estado);
        assertEquals("CONFIRMADO", estado.getNombre());
        assertEquals(2L, estado.getId());
    }
}
