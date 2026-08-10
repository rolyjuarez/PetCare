package bo.capital.tec.pet.modules.reserva.application.command;

import bo.capital.tec.pet.common.client.ProviderCatalogClient;
import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.modules.reserva.domain.model.Disponibilidad;
import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.port.out.CatalogInfoRepository;
import bo.capital.tec.pet.modules.reserva.domain.port.out.ReservaCommandRepository;
import bo.capital.tec.pet.modules.reserva.domain.service.ReservaValidator;
import bo.capital.tec.pet.modules.reserva.dto.RegistroVacunacionInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

/**
 * Orquesta las validaciones de negocio previas a guardar una reserva (crear o
 * actualizar), recopilando la información requerida por el dominio.
 */
@Component
@RequiredArgsConstructor
public class ReservaDatosValidator {

    private static final String MODALIDAD_ESTABLECIMIENTO = "EN_ESTABLECIMIENTO";

    private final ReservaCommandRepository commandRepository;
    private final ProviderCatalogClient providerCatalogClient;
    private final CatalogInfoRepository catalogInfoRepository;
    private final ReservaValidator validator;

    /**
     * Ejecuta todas las reglas de negocio para los datos de una reserva.
     *
     * @return modalidad de entrega normalizada (con valor por defecto)
     */
    public String validarParaGuardar(CrearReservaCommand comando) {
        if (comando.getClienteId() != null) {
            List<Reserva> activas = commandRepository.findActivasPorCliente(comando.getClienteId());
            validator.validarSinReservaActiva(activas, comando.getMascotaId(), comando.getServicioId(),
                    comando.getFechaInicio(), comando.getFechaFin(),
                    comando.getHoraInicio(), comando.getHoraFin());
        }
        validarSlotProveedor(comando);
        validarCertificado(comando);
        return validarModalidad(comando);
    }

    private void validarSlotProveedor(CrearReservaCommand comando) {
        if (comando.getProveedorId() == null || comando.getFechaInicio() == null || comando.getHoraInicio() == null) {
            return;
        }
        LocalTime fin = comando.getHoraFin() != null ? comando.getHoraFin() : comando.getHoraInicio().plusHours(1);
        List<Reserva> booked = commandRepository.findBooked(comando.getProveedorId(), comando.getServicioId(),
                comando.getFechaInicio(), comando.getFechaInicio(), null);
        List<Disponibilidad> windows = providerCatalogClient.getDisponibilidades(comando.getProveedorId(), comando.getServicioId());
        validator.validarSlotProveedor(booked, windows, comando.getFechaInicio(), comando.getHoraInicio(), fin);
    }

    private void validarCertificado(CrearReservaCommand comando) {
        if (comando.getProveedorId() == null) {
            return;
        }
        boolean requiere = Boolean.TRUE.equals(
                providerCatalogClient.getRequiereCertificado(comando.getProveedorId(), comando.getServicioId()));
        if (!requiere && comando.getRegistroVacunacionId() == null) {
            return;
        }
        RegistroVacunacionInfoDTO rv = null;
        if (comando.getRegistroVacunacionId() != null) {
            rv = catalogInfoRepository.findRegistroVacunacion(comando.getRegistroVacunacionId());
            if (rv == null) {
                throw new BusinessException("El registro de vacunación seleccionado no existe");
            }
        }
        validator.validarCertificado(requiere, rv, comando.getMascotaId());
    }

    private String validarModalidad(CrearReservaCommand comando) {
        String modalidad = comando.getModalidadEntrega() != null && !comando.getModalidadEntrega().isBlank()
                ? comando.getModalidadEntrega() : MODALIDAD_ESTABLECIMIENTO;
        boolean modalidadValida = comando.getServicioId() == null
                || providerCatalogClient.isModalidadValida(comando.getServicioId(), modalidad);
        return validator.validarModalidad(modalidadValida, modalidad, comando.getLatitud(), comando.getLongitud());
    }
}
