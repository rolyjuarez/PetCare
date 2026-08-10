package bo.capital.tec.pet.modules.reserva.domain.service;

import bo.capital.tec.pet.modules.reserva.domain.model.Disponibilidad;
import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.model.SlotHorario;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cálculo de disponibilidad de horarios, regla pura del dominio.
 *
 * <p>Genera los intervalos libres de un proveedor para un servicio dentro de un
 * rango de fechas, respetando la duración del servicio y excluyendo los slots ya
 * reservados.</p>
 */
@Component
public class DisponibilidadCalculator {

    /**
     * @return fechas con su lista de slots libres (solo fechas con al menos un slot).
     */
    public Map<LocalDate, List<SlotHorario>> calcular(List<Disponibilidad> windows, List<Reserva> booked,
                                                      LocalDate desde, LocalDate hasta, int duracionMinutos) {
        Map<LocalDate, List<SlotHorario>> resultado = new LinkedHashMap<>();
        for (LocalDate fecha = desde; !fecha.isAfter(hasta); fecha = fecha.plusDays(1)) {
            int diaSemana = fecha.getDayOfWeek().getValue() % 7;
            List<SlotHorario> slots = new ArrayList<>();
            for (Disponibilidad window : windows) {
                if (!Integer.valueOf(diaSemana).equals(window.getDiaSemana())) {
                    continue;
                }
                LocalTime t = window.getHoraInicio();
                while (!t.isAfter(window.getHoraFin())) {
                    LocalTime fin = t.plusMinutes(duracionMinutos);
                    if (fin.isAfter(window.getHoraFin())) {
                        break;
                    }
                    if (!solapaConReservas(booked, fecha, t, fin)) {
                        slots.add(new SlotHorario(t, fin));
                    }
                    t = fin;
                }
            }
            if (!slots.isEmpty()) {
                resultado.put(fecha, slots);
            }
        }
        return resultado;
    }

    private boolean solapaConReservas(List<Reserva> booked, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        for (Reserva r : booked) {
            if (!r.getFechaInicio().equals(fecha)) {
                continue;
            }
            LocalTime rInicio = r.getHoraInicio();
            LocalTime rFin = r.getHoraFin() != null ? r.getHoraFin() : rInicio.plusHours(1);
            if (inicio.isBefore(rFin) && fin.isAfter(rInicio)) {
                return true;
            }
        }
        return false;
    }
}
