package bo.capital.tec.pet.modules.reserva.domain.service;

import bo.capital.tec.pet.modules.reserva.domain.model.Disponibilidad;
import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.model.SlotHorario;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DisponibilidadCalculatorTest {

    private final DisponibilidadCalculator calculator = new DisponibilidadCalculator();

    private static final LocalDate LUNES = LocalDate.of(2026, 8, 10);

    @Test
    void calcularGeneraSlotsRespetandoDuracionYVentana() {
        Disponibilidad window = Disponibilidad.builder()
                .proveedorId(1L).servicioId(1L).diaSemana(1)
                .horaInicio(LocalTime.of(9, 0)).horaFin(LocalTime.of(11, 0))
                .build();

        Map<LocalDate, List<SlotHorario>> result =
                calculator.calcular(List.of(window), List.of(), LUNES, LUNES, 60);

        assertThat(result).containsOnlyKeys(LUNES);
        assertThat(result.get(LUNES))
                .extracting(SlotHorario::inicio)
                .containsExactly(LocalTime.of(9, 0), LocalTime.of(10, 0));
    }

    @Test
    void calcularExcluyeSlotsReservados() {
        Disponibilidad window = Disponibilidad.builder()
                .proveedorId(1L).servicioId(1L).diaSemana(1)
                .horaInicio(LocalTime.of(8, 0)).horaFin(LocalTime.of(12, 0))
                .build();
        Reserva booked = Reserva.builder()
                .id(1L).fechaInicio(LUNES)
                .horaInicio(LocalTime.of(9, 0)).horaFin(LocalTime.of(10, 0))
                .build();

        Map<LocalDate, List<SlotHorario>> result =
                calculator.calcular(List.of(window), List.of(booked), LUNES, LUNES, 60);

        assertThat(result.get(LUNES))
                .extracting(SlotHorario::inicio)
                .containsExactly(LocalTime.of(8, 0), LocalTime.of(10, 0), LocalTime.of(11, 0));
    }

    @Test
    void calcularSoloIncluyeFechasDelDiaSemanaDelRango() {
        Disponibilidad window = Disponibilidad.builder()
                .proveedorId(1L).servicioId(1L).diaSemana(1)
                .horaInicio(LocalTime.of(9, 0)).horaFin(LocalTime.of(10, 0))
                .build();
        LocalDate martes = LUNES.plusDays(1);

        Map<LocalDate, List<SlotHorario>> result =
                calculator.calcular(List.of(window), List.of(), LUNES, martes, 60);

        assertThat(result).containsOnlyKeys(LUNES);
    }

    @Test
    void calcularOmiteSlotsParcialesFueraDeLaVentana() {
        Disponibilidad window = Disponibilidad.builder()
                .proveedorId(1L).servicioId(1L).diaSemana(1)
                .horaInicio(LocalTime.of(10, 0)).horaFin(LocalTime.of(10, 30))
                .build();

        Map<LocalDate, List<SlotHorario>> result =
                calculator.calcular(List.of(window), List.of(), LUNES, LUNES, 60);

        assertThat(result).isEmpty();
    }

    @Test
    void calcularNoDevuelveFechasSinDisponibilidad() {
        Disponibilidad window = Disponibilidad.builder()
                .proveedorId(1L).servicioId(1L).diaSemana(3)
                .horaInicio(LocalTime.of(9, 0)).horaFin(LocalTime.of(10, 0))
                .build();

        Map<LocalDate, List<SlotHorario>> result =
                calculator.calcular(List.of(window), List.of(), LUNES, LUNES, 60);

        assertThat(result).isEmpty();
    }
}
