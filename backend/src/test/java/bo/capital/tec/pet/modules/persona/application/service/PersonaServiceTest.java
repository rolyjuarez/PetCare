package bo.capital.tec.pet.modules.persona.application.service;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.persona.dto.*;
import bo.capital.tec.pet.modules.persona.domain.model.Persona;
import bo.capital.tec.pet.modules.persona.domain.port.PersonaRepository;
import bo.capital.tec.pet.modules.persona.application.service.impl.PersonaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    @InjectMocks
    private PersonaServiceImpl personaService;

    private Persona persona;

    @BeforeEach
    void setUp() {
        persona = Persona.builder()
                .id(1L)
                .nombre("Juan")
                .primerApellido("Perez")
                .segundoApellido("Lopez")
                .ci("1234567")
                .telefono("77777777")
                .email("juan@test.com")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .genero("M")
                .deleted(false)
                .version(1)
                .build();
    }

    @Test
    void create_ShouldReturnPersonaResponse() {
        PersonaRequestDTO request = PersonaRequestDTO.builder()
                .nombre("Juan").primerApellido("Perez").segundoApellido("Lopez")
                .ci("1234567").telefono("77777777").email("juan@test.com")
                .fechaNacimiento(LocalDate.of(1990, 1, 1)).genero("M").build();
        when(personaRepository.insert(any(Persona.class))).thenReturn(1L);
        PersonaResponseDTO response = personaService.create(request);
        assertNotNull(response);
        assertEquals("Juan", response.getNombre());
        verify(personaRepository).insert(any(Persona.class));
    }

    @Test
    void getById_ShouldReturnPersona() {
        when(personaRepository.selectById(1L)).thenReturn(persona);
        PersonaResponseDTO response = personaService.getById(1L);
        assertNotNull(response);
        assertEquals("Juan", response.getNombre());
    }

    @Test
    void getById_ShouldThrow_WhenNotFound() {
        when(personaRepository.selectById(99L)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> personaService.getById(99L));
    }

    @Test
    void getAll_ShouldReturnPagedResponse() {
        when(personaRepository.selectAll(any(), any(), any(), eq(0), eq(20))).thenReturn(List.of(persona));
        when(personaRepository.countAll(any(), any(), any())).thenReturn(1L);
        PagedResponse<PersonaSummaryDTO> response = personaService.getAll(null, null, null, 0, 20);
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(1L, response.getTotalElements());
    }

    @Test
    void update_ShouldReturnUpdatedPersona() {
        PersonaRequestDTO request = PersonaRequestDTO.builder()
                .nombre("Juan Carlos").primerApellido("Perez").segundoApellido("Lopez")
                .ci("1234567").telefono("77777777").email("juan@test.com")
                .fechaNacimiento(LocalDate.of(1990, 1, 1)).genero("M").build();
        when(personaRepository.selectById(1L)).thenReturn(persona);
        when(personaRepository.selectById(1L)).thenReturn(Persona.builder().id(1L).nombre("Juan Carlos").primerApellido("Perez").build());
        personaService.update(1L, request);
        verify(personaRepository).update(any(Persona.class));
    }

    @Test
    void delete_ShouldCallSoftDelete() {
        when(personaRepository.selectById(1L)).thenReturn(persona);
        personaService.delete(1L);
        verify(personaRepository).softDelete(1L);
    }

    @Test
    void delete_ShouldThrow_WhenNotFound() {
        when(personaRepository.selectById(99L)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> personaService.delete(99L));
    }
}
