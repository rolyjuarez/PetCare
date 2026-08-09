package bo.capital.tec.pet.common.client;

import bo.capital.tec.pet.modules.reserva.dto.ModalidadInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ProveedorInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ServicioInfoDTO;
import bo.capital.tec.pet.modules.reserva.entity.Disponibilidad;

import java.util.List;

public interface ProviderCatalogClient {

    ProveedorInfoDTO getProveedor(Long proveedorId);

    ServicioInfoDTO getServicio(Long servicioId);

    List<ModalidadInfoDTO> getModalidades(Long servicioId);

    boolean isModalidadValida(Long servicioId, String modalidad);

    Boolean getRequiereCertificado(Long proveedorId, Long servicioId);

    List<Disponibilidad> getDisponibilidades(Long proveedorId, Long servicioId);
}
