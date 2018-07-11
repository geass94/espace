package ge.boxwood.espace.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "pricing_list")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Pricing extends BaseStatusEntity {
    @Column(nullable = false)
    private Double rangeStart;
    @Column(nullable = false)
    private Double rangeEnd;
    @Column(nullable = false)
    private Float price;
    @Column
    private String name;

    public Double getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(Double rangeStart) {
        this.rangeStart = rangeStart;
    }

    public Double getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(Double rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
