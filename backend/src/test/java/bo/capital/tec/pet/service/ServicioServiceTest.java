package bo.capital.tec.pet.service;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.ServicioRequestDTO;
import bo.capital.tec.pet.dto.ServicioResponseDTO;
import bo.capital.tec.pet.dto.ServicioSummaryDTO;
import bo.capital.tec.pet.domain.Servicio;
import bo.capital.tec.pet.repository.ServicioMapper;
import bo.capital.tec.pet.service.impl.ServicioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioServiceTest {

    @Mock
    private ServicioMapper servicioMapper;

    @InjectMocks
    private ServicioServiceImpl servicioService;

    private Servicio servicio;

    @BeforeEach
    void setUp() {
        servicio = Servicio.builder()
                .id(1L)
                .nombre("Peluqueria")
                .descripcion("Corte y banio completo")
                .duracionMinutos(60)
                .precioBase(new BigDecimal("150.00"))
                .imagenUrl("peluqueria.jpg")
                .activo(true)
                .categoria("Estetica")
                .deleted(false)
                .version(1)
                .build();
    }

    @Test
    void create_ShouldReturnServicioResponse() {
        ServicioRequestDTO request = ServicioRequestDTO.builder()
                .nombre("Peluqueria")
                .descripcion("Corte y banio completo")
                .duracionMinutos(60)
                .precioBase(new BigDecimal("150.00"))
                .imagenUrl("peluqueria.jpg")
                .activo(true)
                .categoria("Estetica")
                .build();

        when(servicioMapper.insert(any(Servicio.class))).thenReturn(1L);

        ServicioResponseDTO response = servicioService.create(request);

        assertNotNull(response);
        assertEquals("Peluqueria", response.getNombre());
        verify(servicioMapper).insert(any(Servicio.class));
    }

    @Test
    void getById_ShouldReturnServicio() {
        when(servicioMapper.selectById(1L)).thenReturn(servicio);

        ServicioResponseDTO response = servicioService.getById(1L);

        assertNotNull(response);
        assertEquals("Peluqueria", response.getNombre());
        assertEquals(new BigDecimal("150.00"), response.getPrecioBase());
    }

    @Test
    void getById_ShouldThrow_WhenNotFound() {
        when(servicioMapper.selectById(99L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> servicioService.getById(99L));
    }

    @Test
    void getAll_ShouldReturnPagedResponse() {
        when(servicioMapper.selectAll(any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(servicio));
        when(servicioMapper.countAll(any(), any())).thenReturn(1L);

        PagedResponse<ServicioSummaryDTO> response = servicioService.getAll(null, null, true, 0, 20);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Peluqueria", response.getContent().get(0).getNombre());
        assertEquals(1L, response.getTotalElements());
    }

    @Test
    void getActive_ShouldReturnActiveServicios() {
        when(servicioMapper.selectActive()).thenReturn(List.of(servicio));

        List<ServicioSummaryDTO> response = servicioService.getActive();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Peluqueria", response.get(0).getNombre());
    }

    @Test
    void update_ShouldReturnUpdatedServicio() {
        ServicioRequestDTO request = ServicioRequestDTO.builder()
                .nombre("Peluqueria Premium")
                .descripcion("Corte, banio y secado")
                .duracionMinutos(90)
                .precioBase(new BigDecimal("200.00"))
                .imagenUrl("peluqueria-premium.jpg")
                .activo(true)
                .categoria("Estetica")
                .build();

        Servicio updatedServicio = Servicio.builder()
                .id(1L)
                .nombre("Peluqueria Premium")
                .descripcion("Corte, banio y secado")
                .duracionMinutos(90)
                .precioBase(new BigDecimal("200.00"))
                .imagenUrl("peluqueria-premium.jpg")
                .activo(true)
                .categoria("Estetica")
                .deleted(false)
                .version(2)
                .build();

        when(servicioMapper.selectById(1L)).thenReturn(servicio).thenReturn(updatedServicio);

        ServicioResponseDTO response = servicioService.update(1L, request);

        assertNotNull(response);
        assertEquals("Peluqueria Premium", response.getNombre());
        verify(servicioMapper).update(any(Servicio.class));
    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        ServicioRequestDTO request = ServicioRequestDTO.builder()
                .nombre("Peluqueria")
                .duracionMinutos(60)
                .precioBase(new BigDecimal("150.00"))
                .categoria("Estetica")
                .build();

        when(servicioMapper.selectById(99L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> servicioService.update(99L, request));
    }

    @Test
    void delete_ShouldCallSoftDelete() {
        when(servicioMapper.selectById(1L)).thenReturn(servicio);

        servicioService.delete(1L);

        verify(servicioMapper).softDelete(1L);
    }

    @Test
    void delete_ShouldThrow_WhenNotFound() {
        when(servicioMapper.selectById(99L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> servicioService.delete(99L));
    }
}
