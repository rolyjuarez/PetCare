package bo.capital.tec.pet.modules.proveedor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorFullCreateDTO {
    @NotBlank private String username;
    @NotBlank private String password;
    @NotBlank private String nombre;
    @NotBlank private String primerApellido;
    private String segundoApellido;
    @NotBlank private String ci;
    private String telefono;
    private String email;
    private LocalDate fechaNacimiento;
    private String genero;
    private String calle;
    private String numero;
    private String referencia;
    @NotNull private Long ciudadId;
    private BigDecimal latitud;
    private BigDecimal longitud;
    @NotBlank private String empresa;
    private BigDecimal radioCoberturaKm;
    private String descripcion;
    private List<Long> servicioIds;
    private List<DisponibilidadItem> disponibilidades;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisponibilidadItem {
        @NotNull private Long servicioId;
        @NotNull @Min(0) @Max(6) private Integer diaSemana;
        @NotNull private LocalTime horaInicio;
        @NotNull private LocalTime horaFin;
    }
}
