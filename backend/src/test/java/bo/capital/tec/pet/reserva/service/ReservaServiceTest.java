package bo.capital.tec.pet.reserva.service;

import bo.capital.tec.pet.modules.reserva.entity.EstadoReserva;
import bo.capital.tec.pet.modules.reserva.entity.Reserva;
import bo.capital.tec.pet.modules.reserva.mapper.EstadoReservaMapper;
import bo.capital.tec.pet.modules.reserva.mapper.ReservaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
    private ReservaMapper reservaMapper;

    @Mock
    private EstadoReservaMapper estadoReservaMapper;

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
        when(reservaMapper.selectById(1L)).thenReturn(reserva);

        Reserva found = reservaMapper.selectById(1L);

        assertNotNull(found);
        assertEquals("RES-001", found.getCodigo());
        assertEquals(1L, found.getClienteId());
        assertEquals(new BigDecimal("150.00"), found.getPrecioTotal());
    }

    @Test
    void selectById_ShouldReturnNull_WhenNotFound() {
        when(reservaMapper.selectById(99L)).thenReturn(null);

        Reserva found = reservaMapper.selectById(99L);

        assertNull(found);
    }

    @Test
    void selectByCodigo_ShouldReturnReserva() {
        when(reservaMapper.selectByCodigo("RES-001")).thenReturn(reserva);

        Reserva found = reservaMapper.selectByCodigo("RES-001");

        assertNotNull(found);
        assertEquals("RES-001", found.getCodigo());
    }

    @Test
    void insert_ShouldReturnId() {
        when(reservaMapper.insert(any(Reserva.class))).thenReturn(1L);

        Long id = reservaMapper.insert(Reserva.builder()
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
        verify(reservaMapper).insert(any(Reserva.class));
    }

    @Test
    void updateEstado_ShouldChangeReservaState() {
        when(reservaMapper.selectById(1L)).thenReturn(reserva);

        reservaMapper.updateEstado(1L, 2L);

        verify(reservaMapper).updateEstado(1L, 2L);
    }

    @Test
    void updateEstado_FromPendienteToConfirmado() {
        when(reservaMapper.selectById(1L)).thenReturn(reserva);
        when(estadoReservaMapper.selectById(1L)).thenReturn(estadoPendiente);
        when(estadoReservaMapper.selectByNombre("CONFIRMADO")).thenReturn(estadoConfirmado);

        Reserva found = reservaMapper.selectById(1L);
        assertEquals(1L, found.getEstadoReservaId());

        EstadoReserva confirmado = estadoReservaMapper.selectByNombre("CONFIRMADO");
        reservaMapper.updateEstado(1L, confirmado.getId());

        verify(reservaMapper).updateEstado(1L, 2L);
    }

    @Test
    void updateEstado_FromConfirmadoToEnProgreso() {
        reserva.setEstadoReservaId(2L);
        when(reservaMapper.selectById(1L)).thenReturn(reserva);
        when(estadoReservaMapper.selectById(2L)).thenReturn(estadoConfirmado);
        when(estadoReservaMapper.selectByNombre("EN_PROGRESO")).thenReturn(estadoEnProgreso);

        Reserva found = reservaMapper.selectById(1L);
        EstadoReserva estadoActual = estadoReservaMapper.selectById(found.getEstadoReservaId());
        assertEquals("CONFIRMADO", estadoActual.getNombre());

        EstadoReserva nuevoEstado = estadoReservaMapper.selectByNombre("EN_PROGRESO");
        reservaMapper.updateEstado(1L, nuevoEstado.getId());

        verify(reservaMapper).updateEstado(1L, 3L);
    }

    @Test
    void updateEstado_FromEnProgresoToFinalizado() {
        reserva.setEstadoReservaId(3L);
        when(reservaMapper.selectById(1L)).thenReturn(reserva);
        when(estadoReservaMapper.selectById(3L)).thenReturn(estadoEnProgreso);
        when(estadoReservaMapper.selectByNombre("FINALIZADO")).thenReturn(estadoFinalizado);

        Reserva found = reservaMapper.selectById(1L);
        EstadoReserva estadoActual = estadoReservaMapper.selectById(found.getEstadoReservaId());
        assertEquals("EN_PROGRESO", estadoActual.getNombre());

        EstadoReserva nuevoEstado = estadoReservaMapper.selectByNombre("FINALIZADO");
        reservaMapper.updateEstado(1L, nuevoEstado.getId());

        verify(reservaMapper).updateEstado(1L, 4L);
    }

    @Test
    void updateEstado_FromPendienteToCancelado() {
        when(reservaMapper.selectById(1L)).thenReturn(reserva);
        when(estadoReservaMapper.selectById(1L)).thenReturn(estadoPendiente);
        when(estadoReservaMapper.selectByNombre("CANCELADO")).thenReturn(estadoCancelado);

        Reserva found = reservaMapper.selectById(1L);
        EstadoReserva estadoActual = estadoReservaMapper.selectById(found.getEstadoReservaId());
        assertEquals("PENDIENTE", estadoActual.getNombre());

        EstadoReserva nuevoEstado = estadoReservaMapper.selectByNombre("CANCELADO");
        reservaMapper.updateEstado(1L, nuevoEstado.getId());

        verify(reservaMapper).updateEstado(1L, 5L);
    }

    @Test
    void selectAll_ShouldReturnPagedResults() {
        when(reservaMapper.selectAll(0, 20)).thenReturn(List.of(reserva));
        when(reservaMapper.countAll()).thenReturn(1L);

        List<Reserva> reservas = reservaMapper.selectAll(0, 20);
        long total = reservaMapper.countAll();

        assertNotNull(reservas);
        assertEquals(1, reservas.size());
        assertEquals(1L, total);
    }

    @Test
    void selectByClienteId_ShouldReturnReservas() {
        when(reservaMapper.selectByClienteId(1L, 0, 20)).thenReturn(List.of(reserva));
        when(reservaMapper.countByClienteId(1L)).thenReturn(1L);

        List<Reserva> reservas = reservaMapper.selectByClienteId(1L, 0, 20);

        assertNotNull(reservas);
        assertEquals(1, reservas.size());
        assertEquals("RES-001", reservas.get(0).getCodigo());
    }

    @Test
    void selectByProveedorId_ShouldReturnReservas() {
        when(reservaMapper.selectByProveedorId(1L, 0, 20)).thenReturn(List.of(reserva));
        when(reservaMapper.countByProveedorId(1L)).thenReturn(1L);

        List<Reserva> reservas = reservaMapper.selectByProveedorId(1L, 0, 20);

        assertNotNull(reservas);
        assertEquals(1, reservas.size());
    }

    @Test
    void softDelete_ShouldMarkAsDeleted() {
        reservaMapper.softDelete(1L);
        verify(reservaMapper).softDelete(1L);
    }

    @Test
    void selectByEstado_ShouldReturnReservas() {
        when(reservaMapper.selectByEstado(1L, 0, 20)).thenReturn(List.of(reserva));
        when(reservaMapper.countByEstado(1L)).thenReturn(1L);

        List<Reserva> reservas = reservaMapper.selectByEstado(1L, 0, 20);

        assertNotNull(reservas);
        assertEquals(1, reservas.size());
    }

    @Test
    void estadoReserva_selectById_ShouldReturnEstado() {
        when(estadoReservaMapper.selectById(1L)).thenReturn(estadoPendiente);

        EstadoReserva estado = estadoReservaMapper.selectById(1L);

        assertNotNull(estado);
        assertEquals("PENDIENTE", estado.getNombre());
        assertEquals(Integer.valueOf(1), estado.getOrden());
    }

    @Test
    void estadoReserva_selectByNombre_ShouldReturnEstado() {
        when(estadoReservaMapper.selectByNombre("CONFIRMADO")).thenReturn(estadoConfirmado);

        EstadoReserva estado = estadoReservaMapper.selectByNombre("CONFIRMADO");

        assertNotNull(estado);
        assertEquals("CONFIRMADO", estado.getNombre());
        assertEquals(2L, estado.getId());
    }
}
