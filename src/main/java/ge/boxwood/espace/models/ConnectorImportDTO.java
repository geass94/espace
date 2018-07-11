package ge.boxwood.espace.models;

public class ConnectorImportDTO {
    private Long connectorId;
    private String type;

    public Long getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(Long connectorId) {
        this.connectorId = connectorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
