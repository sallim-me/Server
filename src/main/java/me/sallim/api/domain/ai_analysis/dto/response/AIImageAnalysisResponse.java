package me.sallim.api.domain.ai_analysis.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIImageAnalysisResponse {
    private String title;
    private String category;
    @JsonProperty("model_code")
    private String modelCode;
    private String brand;
    private Integer price;
    private String description;
    @JsonProperty("processing_time")
    private Double processingTime;
    private Boolean success;
}
