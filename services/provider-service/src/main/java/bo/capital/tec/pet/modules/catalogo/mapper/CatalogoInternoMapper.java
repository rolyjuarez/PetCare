package bo.capital.tec.pet.modules.catalogo.mapper;

import bo.capital.tec.pet.modules.catalogo.dto.DisponibilidadInfoDTO;
import bo.capital.tec.pet.modules.catalogo.dto.ModalidadInfoDTO;
import bo.capital.tec.pet.modules.catalogo.dto.PromocionInfoDTO;
import bo.capital.tec.pet.modules.catalogo.dto.ServicioInfoDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CatalogoInternoMapper {

    ProveedorInfoDTO selectProveedorById(@Param("id") Long id);

    ServicioInfoDTO selectServicioById(@Param("id") Long id);

    List<ModalidadInfoDTO> selectModalidadesByServicio(@Param("servicioId") Long servicioId);

    boolean selectModalidadValida(@Param("servicioId") Long servicioId,
                                  @Param("modalidad") String modalidad);

    BigDecimal selectModalidadCostoAdicional(@Param("servicioId") Long servicioId,
                                             @Param("modalidad") String modalidad);

    Boolean selectRequiereCertificado(@Param("proveedorId") Long proveedorId,
                                      @Param("servicioId") Long servicioId);

    List<DisponibilidadInfoDTO> selectDisponibilidades(@Param("proveedorId") Long proveedorId,
                                                       @Param("servicioId") Long servicioId);

    PromocionInfoDTO selectPromocionActiva(@Param("proveedorId") Long proveedorId,
                                           @Param("servicioId") Long servicioId);

    void incrementarUsosPromocion(@Param("promocionId") Long promocionId);

    void decrementarUsosPromocion(@Param("promocionId") Long promocionId);
}
