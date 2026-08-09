package bo.capital.tec.pet.modules.promocion;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.common.exception.EntityNotFoundException;
import bo.capital.tec.pet.modules.promocion.dto.PromocionRequestDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionResponseDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionSummaryDTO;
import bo.capital.tec.pet.modules.promocion.service.PromocionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1)
class PromocionFlowIntegrationTest {

    @Autowired
    private PromocionService promocionService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limpiarBase() {
        jdbcTemplate.update("DELETE FROM promocion WHERE codigo LIKE 'NUEVO-%'");
    }

    private PromocionRequestDTO buildDTO(String codigo, Long servicioId) {
        return PromocionRequestDTO.builder()
                .codigo(codigo)
                .servicioId(servicioId)
                .nombre("Descuento " + codigo)
                .descripcion("Descuento de prueba")
                .tipoDescuento("PERCENTAGE")
                .valorDescuento(new BigDecimal("10.00"))
                .fechaInicio(LocalDateTime.now().minusDays(1))
                .fechaFin(LocalDateTime.now().plusDays(30))
                .activa(true)
                .limiteUsos(50)
                .build();
    }

    @Test
    void listarDescuentosDeUnProveedor() {
        PagedResponse<PromocionSummaryDTO> pagina = promocionService.listar(1L, 0, 20);

        assertThat(pagina.getTotalElements()).isEqualTo(2);
        assertThat(pagina.getContent())
                .extracting(PromocionSummaryDTO::getCodigo)
                .contains("TEST-10", "TEST-FIX");
        assertThat(pagina.getContent())
                .filteredOn(p -> "TEST-10".equals(p.getCodigo()))
                .singleElement()
                .extracting(PromocionSummaryDTO::getServicioNombre)
                .isEqualTo("Consulta general");
    }

    @Test
    void listarSoloDescuentosActivosVigentes() {
        List<PromocionSummaryDTO> activas = promocionService.listarActivas(1L);

        assertThat(activas).hasSize(2);
        assertThat(activas).allMatch(PromocionSummaryDTO::getActiva);
        assertThat(activas)
                .allMatch(p -> !p.getFechaInicio().isAfter(LocalDateTime.now())
                        && p.getFechaFin().isAfter(LocalDateTime.now()));

        List<PromocionSummaryDTO> proveedor2 = promocionService.listarActivas(2L);
        assertThat(proveedor2).isEmpty();
    }

    @Test
    void crearDescuentoParaServicioDelProveedor() {
        PromocionResponseDTO creada = promocionService.crear(1L, buildDTO("NUEVO-10", 2L));

        assertThat(creada.getId()).isNotNull();
        assertThat(creada.getCodigo()).isEqualTo("NUEVO-10");
        assertThat(creada.getProveedorId()).isEqualTo(1L);
        assertThat(creada.getServicioId()).isEqualTo(2L);
        assertThat(creada.getUsosActuales()).isZero();

        PagedResponse<PromocionSummaryDTO> pagina = promocionService.listar(1L, 0, 20);
        assertThat(pagina.getTotalElements()).isEqualTo(3);
    }

    @Test
    void noPermiteServicioDeOtroProveedor() {
        assertThatThrownBy(() -> promocionService.crear(1L, buildDTO("NUEVO-99", 999L)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no pertenece al proveedor");
    }

    @Test
    void noPermiteCodigoDuplicado() {
        assertThatThrownBy(() -> promocionService.crear(1L, buildDTO("TEST-10", 1L)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("codigo");
    }

    @Test
    void noPermitePorcentajeMayorACien() {
        PromocionRequestDTO dto = buildDTO("NUEVO-150", 1L);
        dto.setTipoDescuento("PERCENTAGE");
        dto.setValorDescuento(new BigDecimal("150.00"));

        assertThatThrownBy(() -> promocionService.crear(1L, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("100%");
    }

    @Test
    void actualizarDescuentoPropio() {
        PromocionRequestDTO dto = buildDTO("TEST-10", 1L);
        dto.setNombre("Consulta 15%");
        dto.setValorDescuento(new BigDecimal("15.00"));

        PromocionResponseDTO actualizada = promocionService.actualizar(1L, 1L, dto);

        assertThat(actualizada.getNombre()).isEqualTo("Consulta 15%");
        assertThat(actualizada.getValorDescuento()).isEqualByComparingTo("15.00");
    }

    @Test
    void noPermiteActualizarDescuentoDeOtroProveedor() {
        assertThatThrownBy(() -> promocionService.actualizar(2L, 1L, buildDTO("TEST-10", 1L)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no pertenece a este proveedor");
    }

    @Test
    void eliminarDescuentoEsSoftDelete() {
        promocionService.eliminar(1L, 2L);

        assertThatThrownBy(() -> promocionService.actualizar(1L, 2L, buildDTO("TEST-FIX", 2L)))
                .isInstanceOf(EntityNotFoundException.class);
        PagedResponse<PromocionSummaryDTO> pagina = promocionService.listar(1L, 0, 20);
        assertThat(pagina.getTotalElements()).isEqualTo(1);
    }
}
