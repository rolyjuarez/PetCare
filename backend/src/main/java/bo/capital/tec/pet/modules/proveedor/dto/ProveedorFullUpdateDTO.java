package bo.capital.tec.pet.modules.proveedor.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProveedorFullUpdateDTO {
    @NotBlank private String nombre;
    @NotBlank private String primerApellido;
    private String segundoApellido;
    @NotBlank private String ci;
    private String telefono;
    private String email;
    private String calle;
    private String numero;
    private String referencia;
    private Long ciudadId;
    private BigDecimal latitud;
    private BigDecimal longitud;
    @NotBlank private String empresa;
    private BigDecimal radioCoberturaKm;
    private String descripcion;
    private List<Long> servicioIds;
    private List<ProveedorFullCreateDTO.DisponibilidadItem> disponibilidades;
}
