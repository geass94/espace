package ge.boxwood.espace.models;

import java.util.List;

public class ChargerImportDTO {
    private Long chargerId;
    private Double longitude;
    private Double latitude;
    private Integer status;
    private Integer type;
    private String description;
    private String code;
    private List<ConnectorImportDTO> connectors;
    private Long category;

    public Long getChargerId() {
        return chargerId;
    }

    public void setChargerId(Long chargerId) {
        this.chargerId = chargerId;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ConnectorImportDTO> getConnectors() {
        return connectors;
    }

    public void setConnectors(List<ConnectorImportDTO> connectors) {
        this.connectors = connectors;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }
}
