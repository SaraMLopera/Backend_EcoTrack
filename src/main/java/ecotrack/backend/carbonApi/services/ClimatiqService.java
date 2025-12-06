package ecotrack.backend.carbonApi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class ClimatiqService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String DATA_VERSION = "^28"; // Versión flexible

    public ClimatiqService(
            @Value("${climatiq.api.url}") String apiUrl,
            @Value("${climatiq.api.key}") String apiKey) {

        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * Calcula emisiones usando Climatiq API
     */
    public Double calculateEmission(String activityId, String region, Map<String, Object> parameters) {
        // Construir emission_factor con data_version
        Map<String, Object> emissionFactor = new HashMap<>();
        emissionFactor.put("activity_id", activityId);
        emissionFactor.put("data_version", DATA_VERSION);
        
        // Solo agregar region si no es GLOBAL
        if (region != null && !region.isEmpty()) {
            emissionFactor.put("region", region);
        }

        Map<String, Object> body = Map.of(
            "emission_factor", emissionFactor,
            "parameters", parameters
        );

        try {
            System.out.println("🔍 Calculando con Climatiq:");
            System.out.println("   Activity ID: " + activityId);
            System.out.println("   Region: " + region);
            System.out.println("   Parameters: " + parameters);
            System.out.println("   Data Version: " + DATA_VERSION);

            Map response = webClient.post()
                    .uri("/estimate")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("co2e")) {
                Double co2e = ((Number) response.get("co2e")).doubleValue();
                System.out.println("✅ Emisión calculada: " + co2e + " kg CO2e");
                return co2e;
            }

            System.err.println(" Respuesta sin campo 'co2e': " + response);
            return 0.0;

        } catch (Exception e) {
            System.err.println(" Error calculando con Climatiq: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Buscar activity IDs disponibles en Climatiq
     */
    public Mono<String> searchActivityIds(String query, String region) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/search")
                    .queryParam("query", query)
                    .queryParam("region", region)
                    .queryParam("data_version", DATA_VERSION)
                    .build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> System.err.println("❌ Error buscando: " + e.getMessage()));
    }
}