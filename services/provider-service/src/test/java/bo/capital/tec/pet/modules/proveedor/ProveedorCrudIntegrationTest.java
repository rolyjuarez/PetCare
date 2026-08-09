package bo.capital.tec.pet.modules.proveedor;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.common.client.ReservationInternalClient;
import bo.capital.tec.pet.common.exception.EntityNotFoundException;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorFullCreateDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorFullUpdateDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorResponseDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorSummaryDTO;
import bo.capital.tec.pet.modules.proveedor.service.ProveedorCrudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {
        "test.saga.comando.notificar-proveedor",
        "test.reserva.aceptada",
        "test.reserva.rechazada"})
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ProveedorCrudIntegrationTest {

    @Autowired
    private ProveedorCrudService proveedorCrudService;

    @MockitoBean
    private ReservationInternalClient reservationInternalClient;

    private ProveedorFullCreateDTO buildCreateDTO(String username, String ci) {
        return ProveedorFullCreateDTO.builder()
                .username(username)
                .password("pass1234")
                .nombre("Laura")
                .primerApellido("Flores")
                .ci(ci)
                .telefono("77700011")
                .email(username + "@petcare.bo")
                .ciudadId(1L)
                .calle("Av. 6 de Agosto")
                .numero("250")
                .referencia("Piso 2")
                .empresa("PetBien SRL")
                .latitud(new BigDecimal("-16.49"))
                .longitud(new BigDecimal("-68.12"))
                .radioCoberturaKm(new BigDecimal("8.0"))
                .descripcion("Atencion domiciliaria")
                .servicioIds(java.util.List.of(1L))
                .disponibilidades(java.util.List.of(
                        ProveedorFullCreateDTO.DisponibilidadItem.builder()
                                .servicioId(1L)
                                .diaSemana(2)
                                .horaInicio(LocalTime.of(9, 0))
                                .horaFin(LocalTime.of(13, 0))
                                .build()))
                .build();
    }

    @Test
    void createFullCreaProveedorConDireccionYEspecialidades() {
        ProveedorResponseDTO creado = proveedorCrudService.createFull(buildCreateDTO("laura", "777000"));

        assertThat(creado.getId()).isNotNull();
        assertThat(creado.getPersonaNombre()).isEqualTo("Laura Flores");
        assertThat(creado.getCalle()).isEqualTo("Av. 6 de Agosto");
        assertThat(creado.getCiudadId()).isEqualTo(1L);
        assertThat(creado.getEmpresa()).isEqualTo("PetBien SRL");
        assertThat(creado.getServicioIds()).containsExactly(1L);
        assertThat(creado.getDisponibilidades()).hasSize(1);

        ProveedorResponseDTO consultado = proveedorCrudService.getById(creado.getId());
        assertThat(consultado.getCi()).isEqualTo("777000");
        assertThat(consultado.getEspecialidades()).contains("Consulta general");
        assertThat(consultado.getServicios()).isEmpty();
    }

    @Test
    void createFullRechazaUsernameDuplicado() {
        ProveedorFullCreateDTO dto = buildCreateDTO("maria", "999888");

        assertThatThrownBy(() -> proveedorCrudService.createFull(dto))
                .isInstanceOf(bo.capital.tec.pet.common.exception.BusinessException.class)
                .hasMessageContaining("usuario");
    }

    @Test
    void getByIdTraeDatosCompletosDelProveedorSemilla() {
        ProveedorResponseDTO proveedor = proveedorCrudService.getById(1L);

        assertThat(proveedor.getEmpresa()).isEqualTo("VetPet SRL");
        assertThat(proveedor.getPersonaNombre()).isEqualTo("Maria Lopez");
        assertThat(proveedor.getCalle()).isEqualTo("Av. Arce");
        assertThat(proveedor.getEspecialidades()).contains("Consulta general", "Peluqueria");
        assertThat(proveedor.getServicios()).hasSize(2);
        assertThat(proveedor.getDisponibilidades()).hasSize(2);
    }

    @Test
    void getByUsuarioIdDevuelveProveedorDelUsuario() {
        ProveedorResponseDTO proveedor = proveedorCrudService.getByUsuarioId(3L);

        assertThat(proveedor.getId()).isEqualTo(1L);
    }

    @Test
    void getAllListaProveedoresActivos() {
        PagedResponse<ProveedorSummaryDTO> pagina = proveedorCrudService.getAll(null, 0, 20);

        assertThat(pagina.getTotalElements()).isEqualTo(2);
        assertThat(pagina.getContent())
                .extracting(ProveedorSummaryDTO::getEmpresa)
                .contains("VetPet SRL", "PetCare Clínica");
    }

    @Test
    void setRequiereCertificadoMarcaEspecialidad() {
        ProveedorResponseDTO actualizado = proveedorCrudService.setRequiereCertificado(1L, 1L, true);

        assertThat(actualizado.getServicioIdsRequeridos()).contains(1L);
    }

    @Test
    void updateFullActualizaPersonaYProveedor() {
        ProveedorFullUpdateDTO dto = ProveedorFullUpdateDTO.builder()
                .nombre("Maria")
                .primerApellido("Rojas")
                .ci("111222")
                .email("maria2@petcare.bo")
                .telefono("77733344")
                .ciudadId(1L)
                .calle("Calle 21")
                .numero("15")
                .empresa("VetPet Actualizado")
                .radioCoberturaKm(new BigDecimal("12.0"))
                .descripcion("Nueva descripcion")
                .servicioIds(java.util.List.of(1L, 2L))
                .build();

        ProveedorResponseDTO actualizado = proveedorCrudService.updateFull(1L, dto);

        assertThat(actualizado.getPrimerApellido()).isEqualTo("Rojas");
        assertThat(actualizado.getEmpresa()).isEqualTo("VetPet Actualizado");
        assertThat(actualizado.getCalle()).isEqualTo("Calle 21");
        assertThat(actualizado.getServicioIds()).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void deleteEsSoftDelete() {
        proveedorCrudService.delete(1L);

        assertThatThrownBy(() -> proveedorCrudService.getById(1L))
                .isInstanceOf(EntityNotFoundException.class);
        assertThat(proveedorCrudService.getAll(null, 0, 20).getTotalElements()).isEqualTo(1);
    }

    @Test
    void hasReservasConsultaAlServicioDeReservas() {
        when(reservationInternalClient.countReservasByProveedor(1L)).thenReturn(3L);
        assertThat(proveedorCrudService.hasReservas(1L)).isTrue();

        when(reservationInternalClient.countReservasByProveedor(2L)).thenReturn(0L);
        assertThat(proveedorCrudService.hasReservas(2L)).isFalse();
    }
}
