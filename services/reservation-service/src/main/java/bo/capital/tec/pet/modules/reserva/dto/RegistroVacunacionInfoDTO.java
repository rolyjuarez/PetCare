package bo.capital.tec.pet.modules.reserva.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroVacunacionInfoDTO {
    private Long id;
    private Long mascotaId;
    private Long vacunaId;
    private String vacunaNombre;
    private LocalDate fechaAplicacion;
    private String certificadoUrl;
}
