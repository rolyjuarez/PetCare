package bo.capital.tec.pet.common.client;

import bo.capital.tec.pet.modules.proveedor.dto.ReservaUbicacionDTO;
import bo.capital.tec.pet.modules.proveedor.dto.VacunaInfoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RestReservationInternalClient implements ReservationInternalClient {

    private final RestClient restClient;

    public RestReservationInternalClient(
            @Value("${app.microservices.reservation.base-url:http://localhost:8081}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public VacunaInfoDTO getRegistroVacunacion(Long id) {
        return restClient.get()
                .uri("/api/v1/interna/registros-vacunacion/{id}", id)
                .retrieve()
                .body(VacunaInfoDTO.class);
    }

    @Override
    public ReservaUbicacionDTO getUbicacionReserva(Long reservaId) {
        return restClient.get()
                .uri("/api/v1/interna/reservas/{id}/ubicacion", reservaId)
                .retrieve()
                .body(ReservaUbicacionDTO.class);
    }

    @Override
    public long countReservasByProveedor(Long proveedorId) {
        Long count = restClient.get()
                .uri("/api/v1/interna/reservas/count-by-proveedor/{proveedorId}", proveedorId)
                .retrieve()
                .body(Long.class);
        return count != null ? count : 0L;
    }
}
