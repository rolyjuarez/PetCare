package bo.capital.tec.pet.proveedor.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProveedorResponseDTO {
    private Long id;
    private Long personaId;
    private String personaNombre;
    private String personaTelefono;
    private String personaEmail;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private BigDecimal radioCoberturaKm;
    private String descripcion;
    private Boolean verificado;
    private BigDecimal calificacion;
    private List<String> especialidades;
    private LocalDateTime createdAt;
}
