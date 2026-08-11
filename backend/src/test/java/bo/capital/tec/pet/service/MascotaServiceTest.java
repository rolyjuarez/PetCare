package bo.capital.tec.pet.service;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.MascotaRequestDTO;
import bo.capital.tec.pet.dto.MascotaResponseDTO;
import bo.capital.tec.pet.dto.MascotaSummaryDTO;
import bo.capital.tec.pet.domain.Mascota;
import bo.capital.tec.pet.repository.ClienteMapper;
import bo.capital.tec.pet.repository.EspecieMapper;
import bo.capital.tec.pet.repository.MascotaMapper;
import bo.capital.tec.pet.repository.PersonaMapper;
import bo.capital.tec.pet.repository.RazaMapper;
import bo.capital.tec.pet.repository.UsuarioMapper;
import bo.capital.tec.pet.service.impl.MascotaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MascotaServiceTest {

    @Mock
    private MascotaMapper mascotaMapper;

    @Mock
    private EspecieMapper especieMapper;

    @Mock
    private RazaMapper razaMapper;

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private PersonaMapper personaMapper;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private MascotaServiceImpl mascotaService;

    private Mascota mascota;

    @BeforeEach
    void setUp() {
        mascota = Mascota.builder()
                .id(1L)
                .nombre("Max")
                .especieId(1L)
                .razaId(1L)
                .clienteId(1L)
                .fechaNacimiento(LocalDate.of(2020, 5, 15))
                .genero("M")
                .peso(new BigDecimal("25.50"))
                .color("Dorado")
                .deleted(false)
                .version(1)
                .build();
    }

    @Test
    void create_ShouldReturnMascotaResponse() {
        MascotaRequestDTO request = MascotaRequestDTO.builder()
                .nombre("Max")
                .especieId(1L)
                .razaId(1L)
                .clienteId(1L)
                .fechaNacimiento(LocalDate.of(2020, 5, 15))
                .genero("M")
                .peso(new BigDecimal("25.50"))
                .color("Dorado")
                .build();

        when(mascotaMapper.insert(any(Mascota.class))).thenReturn(1L);

        MascotaResponseDTO response = mascotaService.create(request);

        assertNotNull(response);
        assertEquals("Max", response.getNombre());
        verify(mascotaMapper).insert(any(Mascota.class));
    }

    @Test
    void getById_ShouldReturnMascota() {
        when(mascotaMapper.selectById(1L)).thenReturn(mascota);

        MascotaResponseDTO response = mascotaService.getById(1L);

        assertNotNull(response);
        assertEquals("Max", response.getNombre());
        assertEquals(new BigDecimal("25.50"), response.getPeso());
    }

    @Test
    void getById_ShouldThrow_WhenNotFound() {
        when(mascotaMapper.selectById(99L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> mascotaService.getById(99L));
    }

    @Test
    void getAll_ShouldReturnPagedResponse() {
        MascotaSummaryDTO summary = MascotaSummaryDTO.builder()
                .id(1L)
                .nombre("Max")
                .build();
        when(mascotaMapper.selectAll(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(summary));
        when(mascotaMapper.countAll(any(), any(), any())).thenReturn(1L);

        PagedResponse<MascotaSummaryDTO> response = mascotaService.getAll(null, null, null, 0, 20);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Max", response.getContent().get(0).getNombre());
        assertEquals(1L, response.getTotalElements());
    }

    @Test
    void getByClienteId_ShouldReturnList() {
        MascotaSummaryDTO summary = MascotaSummaryDTO.builder()
                .id(1L)
                .nombre("Max")
                .build();
        when(mascotaMapper.selectByClienteId(eq(1L), anyInt(), anyInt()))
                .thenReturn(List.of(summary));

        List<MascotaSummaryDTO> response = mascotaService.getByClienteId(1L);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Max", response.get(0).getNombre());
    }

    @Test
    void update_ShouldReturnUpdatedMascota() {
        MascotaRequestDTO request = MascotaRequestDTO.builder()
                .nombre("Max Actualizado")
                .especieId(1L)
                .razaId(1L)
                .clienteId(1L)
                .fechaNacimiento(LocalDate.of(2020, 5, 15))
                .genero("M")
                .peso(new BigDecimal("30.00"))
                .color("Marron")
                .build();

        Mascota updatedMascota = Mascota.builder()
                .id(1L)
                .nombre("Max Actualizado")
                .especieId(1L)
                .razaId(1L)
                .clienteId(1L)
                .fechaNacimiento(LocalDate.of(2020, 5, 15))
                .genero("M")
                .peso(new BigDecimal("30.00"))
                .color("Marron")
                .deleted(false)
                .version(2)
                .build();

        when(mascotaMapper.selectById(1L)).thenReturn(mascota).thenReturn(updatedMascota);

        MascotaResponseDTO response = mascotaService.update(1L, request);

        assertNotNull(response);
        assertEquals("Max Actualizado", response.getNombre());
        verify(mascotaMapper).update(any(Mascota.class));
    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        MascotaRequestDTO request = MascotaRequestDTO.builder()
                .nombre("Max")
                .especieId(1L)
                .razaId(1L)
                .clienteId(1L)
                .fechaNacimiento(LocalDate.of(2020, 5, 15))
                .genero("M")
                .build();

        when(mascotaMapper.selectById(99L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> mascotaService.update(99L, request));
    }

    @Test
    void delete_ShouldCallSoftDelete() {
        when(mascotaMapper.selectById(1L)).thenReturn(mascota);

        mascotaService.delete(1L);

        verify(mascotaMapper).softDelete(1L);
    }

    @Test
    void delete_ShouldThrow_WhenNotFound() {
        when(mascotaMapper.selectById(99L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> mascotaService.delete(99L));
    }
}
