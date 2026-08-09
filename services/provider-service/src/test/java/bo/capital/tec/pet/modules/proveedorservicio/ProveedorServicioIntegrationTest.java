package bo.capital.tec.pet.modules.proveedorservicio;

import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.common.exception.EntityNotFoundException;
import bo.capital.tec.pet.modules.proveedorservicio.dto.ModalidadDTO;
import bo.capital.tec.pet.modules.proveedorservicio.dto.ProveedorServicioRequestDTO;
import bo.capital.tec.pet.modules.proveedorservicio.dto.ProveedorServicioResponseDTO;
import bo.capital.tec.pet.modules.proveedorservicio.service.ProveedorServicioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1)
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ProveedorServicioIntegrationTest {

    @Autowired
    private ProveedorServicioService proveedorServicioService;

    private ProveedorServicioRequestDTO buildDTO() {
        return ProveedorServicioRequestDTO.builder()
                .nombre("Paseo por 30 min")
                .descripcion("Paseo a domicilio")
                .categoria("PASEO")
                .duracionMinutos(30)
                .precioBase(new BigDecimal("40.00"))
                .requiereCertificado(false)
                .activo(true)
                .modalidades(List.of(
                        ModalidadDTO.builder()
                                .modalidad("RECOGIDA_ENTREGA")
                                .costoAdicional(new BigDecimal("10.00"))
                                .build(),
                        ModalidadDTO.builder()
                                .modalidad("DOMICILIO")
                                .costoAdicional(new BigDecimal("15.00"))
                                .build()))
                .build();
    }

    @Test
    void crearServicioConModalidades() {
        ProveedorServicioResponseDTO creado = proveedorServicioService.create(2L, buildDTO());

        assertThat(creado.getId()).isNotNull();
        assertThat(creado.getProveedorId()).isEqualTo(2L);
        assertThat(creado.getProveedorNombre()).isEqualTo("Carlos Torres");
        assertThat(creado.getModalidades()).hasSize(2);
        assertThat(creado.getModalidades())
                .extracting(ProveedorServicioResponseDTO.ModalidadDTO::getModalidad)
                .containsExactlyInAnyOrder("RECOGIDA_ENTREGA", "DOMICILIO");

        List<ProveedorServicioResponseDTO> lista = proveedorServicioService.listByProveedor(2L);
        assertThat(lista).hasSize(1);
    }

    @Test
    void listarServiciosDelProveedor() {
        List<ProveedorServicioResponseDTO> lista = proveedorServicioService.listByProveedor(1L);

        assertThat(lista).hasSize(2);
        assertThat(lista)
                .extracting(ProveedorServicioResponseDTO::getNombre)
                .contains("Consulta general", "Peluqueria");
        assertThat(lista.get(0).getModalidades()).isNotEmpty();
    }

    @Test
    void actualizarServicio() {
        ProveedorServicioRequestDTO dto = buildDTO();
        dto.setNombre("Paseo premium");
        dto.setPrecioBase(new BigDecimal("60.00"));

        ProveedorServicioResponseDTO actualizado = proveedorServicioService.update(1L, 1L, dto);

        assertThat(actualizado.getNombre()).isEqualTo("Paseo premium");
        assertThat(actualizado.getPrecioBase()).isEqualByComparingTo("60.00");
        assertThat(actualizado.getModalidades()).hasSize(2);
    }

    @Test
    void noPermiteServicioDeOtroProveedor() {
        assertThatThrownBy(() -> proveedorServicioService.update(2L, 1L, buildDTO()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no pertenece");
    }

    @Test
    void noPermiteCategoriaInvalida() {
        ProveedorServicioRequestDTO dto = buildDTO();
        dto.setCategoria("SEGUROS");

        assertThatThrownBy(() -> proveedorServicioService.create(2L, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Categoria");
    }

    @Test
    void setActivoInactivaServicio() {
        ProveedorServicioResponseDTO inactivo = proveedorServicioService.setActivo(1L, 1L, false);

        assertThat(inactivo.getActivo()).isFalse();
        assertThat(proveedorServicioService.listActivosByProveedor(1L))
                .extracting(ProveedorServicioResponseDTO::getNombre)
                .doesNotContain("Consulta general");
    }

    @Test
    void eliminarServicioEsSoftDelete() {
        proveedorServicioService.delete(1L, 2L);

        assertThatThrownBy(() -> proveedorServicioService.getById(1L, 2L))
                .isInstanceOf(EntityNotFoundException.class);
        assertThat(proveedorServicioService.listByProveedor(1L)).hasSize(1);
    }
}
