package bo.capital.tec.pet.promocion.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.promocion.dto.PromocionRequestDTO;
import bo.capital.tec.pet.promocion.dto.PromocionResponseDTO;
import bo.capital.tec.pet.promocion.dto.PromocionSummaryDTO;

import java.time.LocalDate;
import java.util.List;

public interface PromocionService {
    PromocionResponseDTO create(PromocionRequestDTO dto);
    PromocionResponseDTO getById(Long id);
    PagedResponse<PromocionSummaryDTO> getAll(String nombre, boolean activo, int page, int size);
    List<PromocionSummaryDTO> getActive();
    List<PromocionSummaryDTO> getActiveByDate(LocalDate fecha);
    PromocionResponseDTO update(Long id, PromocionRequestDTO dto);
    void delete(Long id);
}
