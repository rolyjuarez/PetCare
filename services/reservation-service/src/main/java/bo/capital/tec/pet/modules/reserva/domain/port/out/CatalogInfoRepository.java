package bo.capital.tec.pet.modules.reserva.domain.port.out;

import bo.capital.tec.pet.modules.reserva.dto.ClienteInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.MascotaInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.RegistroVacunacionInfoDTO;

/**
 * Puerto de salida para datos de catálogo necesarios en la composición de
 * reservas: cliente, mascota y registros de vacunación.
 */
public interface CatalogInfoRepository {

    ClienteInfoDTO findCliente(Long clienteId);

    MascotaInfoDTO findMascota(Long mascotaId);

    RegistroVacunacionInfoDTO findRegistroVacunacion(Long registroVacunacionId);
}
