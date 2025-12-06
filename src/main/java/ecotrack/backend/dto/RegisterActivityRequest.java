package ecotrack.backend.dto;

import lombok.Data;
import java.util.Map;

@Data
public class RegisterActivityRequest {
    private Long userId;
    private String fecha;
    private String tipoActividad;
    private String descripcion;
    
    // Nuevos campos para Climatiq
    private String activityId;
    private String region;
    private Map<String, Object> parameters;
}