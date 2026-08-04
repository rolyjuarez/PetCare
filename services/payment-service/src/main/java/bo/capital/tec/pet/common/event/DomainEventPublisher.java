package bo.capital.tec.pet.common.event;

@FunctionalInterface
public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
