package bo.capital.tec.pet.modules.reserva.domain.port.out;

/**
 * Puerto de salida para notificaciones internas de la aplicación.
 */
public interface NotificationPort {

    void notificar(Long usuarioId, String titulo, String mensaje, String tipo);
}
