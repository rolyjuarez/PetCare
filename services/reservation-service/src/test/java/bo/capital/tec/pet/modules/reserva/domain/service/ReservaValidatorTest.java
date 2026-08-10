package bo.capital.tec.pet.modules.reserva.domain.service;

import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.modules.reserva.domain.model.Disponibilidad;
import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.dto.RegistroVacunacionInfoDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservaValidatorTest {

    private final ReservaValidator validator = new ReservaValidator();

    private static final LocalDate FECHA = LocalDate.of(2026, 8, 12);

    @Test
    void validarSinReservaActivaRechazaHorarioSolapado() {
        Reserva activa = Reserva.builder()
                .mascotaId(1L).servicioId(1L).fechaInicio(FECHA)
                .horaInicio(LocalTime.of(9, 0)).horaFin(LocalTime.of(10, 0))
                .build();

        assertThatThrownBy(() -> validator.validarSinReservaActiva(List.of(activa), 1L, 1L, FECHA, FECHA,
                LocalTime.of(9, 30), LocalTime.of(10, 30)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("reserva activa");
    }

    @Test
    void validarSinReservaActivaPermiteHorarioNoSolapado() {
        Reserva activa = Reserva.builder()
                .mascotaId(1L).servicioId(1L).fechaInicio(FECHA)
                .horaInicio(LocalTime.of(9, 0)).horaFin(LocalTime.of(10, 0))
                .build();

        assertThatCode(() -> validator.validarSinReservaActiva(List.of(activa), 1L, 1L, FECHA, FECHA,
                LocalTime.of(11, 0), LocalTime.of(12, 0)))
                .doesNotThrowAnyException();
    }

    @Test
    void validarSinReservaActivaIgnoraOtraMascotaOServicio() {
        Reserva activa = Reserva.builder()
                .mascotaId(2L).servicioId(2L).fechaInicio(FECHA)
                .horaInicio(LocalTime.of(9, 0)).horaFin(LocalTime.of(10, 0))
                .build();

        assertThatCode(() -> validator.validarSinReservaActiva(List.of(activa), 1L, 1L, FECHA, FECHA,
                LocalTime.of(9, 30), LocalTime.of(10, 30)))
                .doesNotThrowAnyException();
    }

    @Test
    void validarSlotProveedorRechazaHorarioTomado() {
        Reserva booked = Reserva.builder()
                .id(1L).fechaInicio(FECHA)
                .horaInicio(LocalTime.of(9, 0)).horaFin(LocalTime.of(10, 0))
                .build();

        assertThatThrownBy(() -> validator.validarSlotProveedor(List.of(booked), List.of(), FECHA,
                LocalTime.of(9, 30), LocalTime.of(10, 30)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya tiene una reserva");
    }

    @Test
    void validarSlotProveedorRechazaFueraDeDisponibilidad() {
        Disponibilidad window = Disponibilidad.builder()
                .proveedorId(1L).servicioId(1L).diaSemana(3)
                .horaInicio(LocalTime.of(9, 0)).horaFin(LocalTime.of(11, 0))
                .build();

        assertThatThrownBy(() -> validator.validarSlotProveedor(List.of(), List.of(window), FECHA,
                LocalTime.of(13, 0), LocalTime.of(14, 0)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("disponibilidad del proveedor");
    }

    @Test
    void validarSlotProveedorPermiteDentroDeDisponibilidad() {
        Disponibilidad window = Disponibilidad.builder()
                .proveedorId(1L).servicioId(1L).diaSemana(3)
                .horaInicio(LocalTime.of(9, 0)).horaFin(LocalTime.of(11, 0))
                .build();

        assertThatCode(() -> validator.validarSlotProveedor(List.of(), List.of(window), FECHA,
                LocalTime.of(9, 30), LocalTime.of(10, 30)))
                .doesNotThrowAnyException();
    }

    @Test
    void validarModalidadRechazaModalidadNoDisponible() {
        assertThatThrownBy(() -> validator.validarModalidad(false, "DOMICILIO", null, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no está disponible");
    }

    @Test
    void validarModalidadExigeCoordenadasFueraDeEstablecimiento() {
        assertThatThrownBy(() -> validator.validarModalidad(true, "DOMICILIO", null, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("latitud");
    }

    @Test
    void validarModalidadDevuelveModalidadNormalizada() {
        assertThat(validator.validarModalidad(true, "EN_ESTABLECIMIENTO", null, null))
                .isEqualTo("EN_ESTABLECIMIENTO");
        assertThat(validator.validarModalidad(true, "DOMICILIO", new BigDecimal("-70.5"), new BigDecimal("-33.4")))
                .isEqualTo("DOMICILIO");
    }

    @Test
    void validarCertificadoRechazaFaltanteSiServicioRequiere() {
        assertThatThrownBy(() -> validator.validarCertificado(true, null, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("certificado");
    }

    @Test
    void validarCertificadoRechazaRegistroDeOtraMascota() {
        RegistroVacunacionInfoDTO rv = RegistroVacunacionInfoDTO.builder()
                .id(10L).mascotaId(2L).certificadoUrl("https://example.com/vacuna.pdf")
                .build();

        assertThatThrownBy(() -> validator.validarCertificado(true, rv, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no pertenece");
    }

    @Test
    void validarCertificadoRechazaRegistroSinCertificadoAdjunto() {
        RegistroVacunacionInfoDTO rv = RegistroVacunacionInfoDTO.builder()
                .id(10L).mascotaId(1L).certificadoUrl(null)
                .build();

        assertThatThrownBy(() -> validator.validarCertificado(true, rv, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Debe adjuntar el certificado");
    }

    @Test
    void validarCertificadoPermiteRegistroValido() {
        RegistroVacunacionInfoDTO rv = RegistroVacunacionInfoDTO.builder()
                .id(10L).mascotaId(1L).certificadoUrl("https://example.com/vacuna.pdf")
                .build();

        assertThatCode(() -> validator.validarCertificado(true, rv, 1L))
                .doesNotThrowAnyException();
    }
}
