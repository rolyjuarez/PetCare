package bo.capital.tec.pet.modules.pago.pasarela;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Pasarela de pago simulada. Aprueba la transacción salvo que el número de
 * tarjeta termine en "0000" (para probar rechazos). Genera un id de intención
 * y una referencia de transacción con prefijo configurable.
 */
@Slf4j
@Component
public class PasarelaSimuladaService implements SimulatedPaymentGateway {

    @Value("${app.pasarela.prefijo-referencia:PTC}")
    private String prefijoReferencia;

    @Override
    public ResultadoPasarela procesar(IntencionPago intencion) {
        String intencionId = "INT-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        boolean aprobado = intencion.getNumeroTarjeta() == null
                || !intencion.getNumeroTarjeta().endsWith("0000");
        if (aprobado) {
            String referencia = prefijoReferencia + "-"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            log.info("Pasarela simulada aprueba pago {} por {}", intencionId, intencion.getMonto());
            return ResultadoPasarela.builder()
                    .aprobado(true)
                    .intencionId(intencionId)
                    .referenciaTransaccion(referencia)
                    .montoCobrado(intencion.getMonto())
                    .mensaje("Transacción aprobada")
                    .build();
        }
        log.warn("Pasarela simulada rechaza pago {}", intencionId);
        return ResultadoPasarela.builder()
                .aprobado(false)
                .intencionId(intencionId)
                .montoCobrado(intencion.getMonto())
                .mensaje("La transacción fue rechazada por la entidad emisora")
                .build();
    }
}
