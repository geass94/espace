package ge.boxwood.espace.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "partners")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Partner extends BaseStatusAuditEntity {
    @Column
    private String name;
    @Transient
    private String image;
    @Column
    private String description;
    @Column
    private String url;
    @Column
    private Long imageId;
    @Column
    private Integer orderIndex;
    @Column
    private String logo;

    @JsonIgnore
    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL)
    private Set<PartnerFile> partnerFileList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<PartnerFile> getPartnerFileList() {
        return partnerFileList;
    }

    public void setPartnerFileList(Set<PartnerFile> partnerFileList) {
        this.partnerFileList = partnerFileList;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
