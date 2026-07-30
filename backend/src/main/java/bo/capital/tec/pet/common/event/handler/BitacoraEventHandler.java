package bo.capital.tec.pet.common.event.handler;

import bo.capital.tec.pet.common.event.DomainEvent;
import bo.capital.tec.pet.modules.bitacora.dto.BitacoraRequestDTO;
import bo.capital.tec.pet.modules.bitacora.service.BitacoraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class BitacoraEventHandler {

    private final BitacoraService bitacoraService;

    @EventListener
    public void handleDomainEvent(DomainEvent event) {
        String accion = event.getAggregateType() + "_" + event.getClass().getSimpleName()
                .replace("Event", "").toUpperCase();
        BitacoraRequestDTO dto = BitacoraRequestDTO.builder()
                .accion(accion)
                .entidad(event.getAggregateType())
                .entidadId(event.getAggregateId())
                .datosNuevos("Evento: " + event.getClass().getSimpleName()
                        + " | Id: " + event.getEventId()
                        + " | Timestamp: " + event.getOccurredOn())
                .build();
        bitacoraService.saveAsync(dto);
    }
}
