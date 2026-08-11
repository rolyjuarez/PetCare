package bo.capital.tec.pet.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ClienteSummaryDTO {
    private Long id;
    private String nombreCompleto;
    private String ci;
    private String telefono;
    private Long personaId;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String email;
    private LocalDate fechaNacimiento;
    private String genero;
    private Long usuarioId;
    private String username;
    private Long direccionId;
    private String calle;
    private String numero;
    private String referencia;
    private Long ciudadId;
    private String ciudadNombre;
    private BigDecimal latitud;
    private BigDecimal longitud;
}
