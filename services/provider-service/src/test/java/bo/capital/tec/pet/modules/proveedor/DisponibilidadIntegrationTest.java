package bo.capital.tec.pet.modules.proveedor;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.common.exception.EntityNotFoundException;
import bo.capital.tec.pet.modules.proveedor.dto.DisponibilidadRequestDTO;
import bo.capital.tec.pet.modules.proveedor.dto.DisponibilidadResponseDTO;
import bo.capital.tec.pet.modules.proveedor.service.DisponibilidadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1)
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class DisponibilidadIntegrationTest {

    @Autowired
    private DisponibilidadService disponibilidadService;

    @Test
    void listarDisponibilidadesPorProveedorYServicio() {
        List<DisponibilidadResponseDTO> lista = disponibilidadService.getByProveedorAndServicio(1L, 1L);

        assertThat(lista).hasSize(2);
        assertThat(lista).allMatch(d -> d.getProveedorId() == 1L && d.getServicioId() == 1L);
        assertThat(lista.get(0).getServicioNombre()).isEqualTo("Consulta general");
        assertThat(lista.get(0).getDiaSemanaNombre()).isEqualTo("Lunes");
    }

    @Test
    void getAllPaginado() {
        PagedResponse<DisponibilidadResponseDTO> pagina =
                disponibilidadService.getAll(1L, 1L, 0, 10);

        assertThat(pagina.getTotalElements()).isEqualTo(2);
        assertThat(pagina.getContent()).hasSize(2);
    }

    @Test
    void crearDisponibilidad() {
        DisponibilidadRequestDTO dto = DisponibilidadRequestDTO.builder()
                .proveedorId(2L)
                .servicioId(2L)
                .diaSemana(4)
                .horaInicio(LocalTime.of(10, 0))
                .horaFin(LocalTime.of(14, 0))
                .build();

        DisponibilidadResponseDTO creada = disponibilidadService.create(dto);

        assertThat(creada.getId()).isNotNull();
        assertThat(creada.getDiaSemana()).isEqualTo(4);
        assertThat(creada.getActivo()).isTrue();
        assertThat(disponibilidadService.getByProveedorAndServicio(2L, 2L)).hasSize(1);
    }

    @Test
    void actualizarDisponibilidad() {
        DisponibilidadRequestDTO dto = DisponibilidadRequestDTO.builder()
                .proveedorId(1L)
                .servicioId(1L)
                .diaSemana(5)
                .horaInicio(LocalTime.of(16, 0))
                .horaFin(LocalTime.of(20, 0))
                .build();

        DisponibilidadResponseDTO actualizada = disponibilidadService.update(1L, dto);

        assertThat(actualizada.getDiaSemana()).isEqualTo(5);
        assertThat(actualizada.getHoraFin()).isEqualTo(LocalTime.of(20, 0));
    }

    @Test
    void eliminarDisponibilidadEsSoftDelete() {
        disponibilidadService.delete(1L);

        assertThat(disponibilidadService.getByProveedorAndServicio(1L, 1L)).hasSize(1);
        assertThatThrownBy(() -> disponibilidadService.getById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
