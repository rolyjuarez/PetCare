package bo.capital.tec.pet.modules.proveedor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorCatalogoDTO {
    private Long id;
    private Long personaId;
    private String personaNombre;
    private String personaTelefono;
    private String personaEmail;
    private String empresa;
    private String descripcion;
    private BigDecimal radioCoberturaKm;
    private BigDecimal calificacion;
    private Boolean verificado;
    private List<String> especialidades;
    private List<Long> servicioIds;
    private List<Long> servicioIdsRequeridos;
}
