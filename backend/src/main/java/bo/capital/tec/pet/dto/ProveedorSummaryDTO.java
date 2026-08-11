package bo.capital.tec.pet.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProveedorSummaryDTO {
    private Long id;
    private String nombre;
    private String empresa;
    private String personaTelefono;
    private String personaEmail;
    private String descripcion;
    private BigDecimal radioCoberturaKm;
    private BigDecimal calificacion;
    private Boolean verificado;
    private List<String> especialidades;
}
