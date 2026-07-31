package bo.capital.tec.pet.modules.vacuna.application.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.vacuna.dto.RegistroVacunacionRequestDTO;
import bo.capital.tec.pet.modules.vacuna.dto.RegistroVacunacionResponseDTO;
import bo.capital.tec.pet.modules.vacuna.dto.RegistroVacunacionSummaryDTO;

import java.time.LocalDate;

public interface RegistroVacunacionService {
    RegistroVacunacionResponseDTO create(RegistroVacunacionRequestDTO dto);
    RegistroVacunacionResponseDTO getById(Long id);
    PagedResponse<RegistroVacunacionSummaryDTO> getAll(Long mascotaId, Long vacunaId,
            LocalDate fechaDesde, LocalDate fechaHasta, int page, int size);
    RegistroVacunacionResponseDTO update(Long id, RegistroVacunacionRequestDTO dto);
    void delete(Long id);
}
