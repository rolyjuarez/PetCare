package bo.capital.tec.pet.common.client;

import bo.capital.tec.pet.modules.pago.dto.PromocionInfoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class RestPagoProviderClient implements PagoProviderClient {

    private final RestClient restClient;

    public RestPagoProviderClient(
            @Value("${app.microservices.provider.base-url:http://localhost:8082}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public PromocionInfoDTO getPromocionActiva(Long proveedorId, Long servicioId) {
        return restClient.get()
                .uri("/api/v1/interna/promociones/activa?proveedorId={proveedorId}&servicioId={servicioId}",
                        proveedorId, servicioId)
                .retrieve()
                .body(PromocionInfoDTO.class);
    }

    @Override
    public void incrementarUsosPromocion(Long promocionId) {
        restClient.post()
                .uri("/api/v1/interna/promociones/{promocionId}/usos/incrementar", promocionId)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void decrementarUsosPromocion(Long promocionId) {
        restClient.post()
                .uri("/api/v1/interna/promociones/{promocionId}/usos/decrementar", promocionId)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public BigDecimal getCostoAdicionalModalidad(Long servicioId, String modalidad) {
        return restClient.get()
                .uri("/api/v1/interna/servicios/{servicioId}/modalidades/{modalidad}/costo-adicional",
                        servicioId, modalidad)
                .retrieve()
                .body(BigDecimal.class);
    }
}
