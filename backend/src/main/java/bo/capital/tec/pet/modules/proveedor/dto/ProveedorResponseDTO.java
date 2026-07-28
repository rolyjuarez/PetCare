package bo.capital.tec.pet.modules.proveedor.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProveedorResponseDTO {
    private Long id;
    private Long personaId;
    private Long usuarioId;
    private String personaNombre;
    private String personaTelefono;
    private String personaEmail;
    private String ci;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private LocalDate fechaNacimiento;
    private String genero;
    private Long direccionId;
    private String calle;
    private String numero;
    private String referencia;
    private Long ciudadId;
    private String empresa;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private BigDecimal radioCoberturaKm;
    private String descripcion;
    private Boolean verificado;
    private BigDecimal calificacion;
    private List<String> especialidades;
    private List<Long> servicioIds;
    private List<DisponibilidadDTO> disponibilidades;
    private LocalDateTime createdAt;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DisponibilidadDTO {
        private Long id;
        private Long servicioId;
        private String servicioNombre;
        private Integer diaSemana;
        private String diaSemanaNombre;
        private String horaInicio;
        private String horaFin;
    }
}
