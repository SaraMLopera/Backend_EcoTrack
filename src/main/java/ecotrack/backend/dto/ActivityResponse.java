package ecotrack.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponse {
    private Long activityId;
    private String fecha;
    private String tipoActividad;
    private String descripcion;
    private Double emisiones;
    private String fuente;
}