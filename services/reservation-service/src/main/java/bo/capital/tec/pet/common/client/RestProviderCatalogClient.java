package bo.capital.tec.pet.common.client;

import bo.capital.tec.pet.modules.reserva.dto.ModalidadInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ProveedorInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ServicioInfoDTO;
import bo.capital.tec.pet.modules.reserva.entity.Disponibilidad;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class RestProviderCatalogClient implements ProviderCatalogClient {

    private final RestClient restClient;

    public RestProviderCatalogClient(
            @Value("${app.microservices.provider.base-url:http://localhost:8082}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public ProveedorInfoDTO getProveedor(Long proveedorId) {
        return restClient.get()
                .uri("/api/v1/interna/proveedores/{proveedorId}", proveedorId)
                .retrieve()
                .body(ProveedorInfoDTO.class);
    }

    @Override
    public ServicioInfoDTO getServicio(Long servicioId) {
        return restClient.get()
                .uri("/api/v1/interna/servicios/{servicioId}", servicioId)
                .retrieve()
                .body(ServicioInfoDTO.class);
    }

    @Override
    public List<ModalidadInfoDTO> getModalidades(Long servicioId) {
        return restClient.get()
                .uri("/api/v1/interna/servicios/{servicioId}/modalidades", servicioId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public boolean isModalidadValida(Long servicioId, String modalidad) {
        Boolean valida = restClient.get()
                .uri("/api/v1/interna/servicios/{servicioId}/modalidades/{modalidad}/valida",
                        servicioId, modalidad)
                .retrieve()
                .body(Boolean.class);
        return Boolean.TRUE.equals(valida);
    }

    @Override
    public Boolean getRequiereCertificado(Long proveedorId, Long servicioId) {
        return restClient.get()
                .uri("/api/v1/interna/proveedores/{proveedorId}/servicios/{servicioId}/requiere-certificado",
                        proveedorId, servicioId)
                .retrieve()
                .body(Boolean.class);
    }

    @Override
    public List<Disponibilidad> getDisponibilidades(Long proveedorId, Long servicioId) {
        return restClient.get()
                .uri("/api/v1/interna/proveedores/{proveedorId}/servicios/{servicioId}/disponibilidades",
                        proveedorId, servicioId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Disponibilidad>>() {
                });
    }
}
