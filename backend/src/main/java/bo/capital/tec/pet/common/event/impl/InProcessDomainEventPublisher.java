package bo.capital.tec.pet.common.event.impl;

import bo.capital.tec.pet.common.event.DomainEvent;
import bo.capital.tec.pet.common.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "app.events.mode", havingValue = "inprocess", matchIfMissing = true)
@RequiredArgsConstructor
public class InProcessDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
