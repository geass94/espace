package ge.boxwood.espace.models;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Table(name = "chargers")
@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Charger extends BaseEntity {
    @Column(name = "chargerId", unique = true, nullable = false)
    private Long chargerId;
    @Column
    private Double longitude;
    @Column
    private Double latitude;
    @Column
    private Integer status;
    @Column
    private Integer type;
    @Column
    private String description;
    @Column
    private String code;
    @Transient
    private Long categoryId;

    @JsonIgnore
    @OneToMany(mappedBy = "charger", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToMany(cascade = CascadeType.MERGE,
            fetch = FetchType.LAZY,
            mappedBy = "charger")
    private List<Connector> connectors;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id", nullable = true)
    private Category category;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Long getChargerId() {
        return chargerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Connector> getConnectors() {
        return connectors;
    }

    public void setConnectors(List<Connector> connectors) {
        this.connectors = connectors;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setChargerId(Long chargerId) {
        this.chargerId = chargerId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public HashMap getHashMap(){
        HashMap ret = new HashMap();
        ret.put("id", this.id);
        ret.put("chargerId", this.chargerId);
        ret.put("latitide", this.latitude);
        ret.put("longitude", this.longitude);
        ret.put("status", this.status);
        ret.put("type", this.type);
        ret.put("latitide", this.latitude);
        ret.put("category", this.category.getId());
        ret.put("code", this.code);
        return ret;
    }
}
