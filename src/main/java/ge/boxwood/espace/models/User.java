package ge.boxwood.espace.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.joda.time.DateTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;


@Table(name = "USERS")
@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class User extends BaseStatusAuditEntity implements UserDetails {
    @Column(name = "username", unique = true)
    @NotNull
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email", unique = true)
    @NotNull
    private String email;
    @Column(name = "phone_number", unique = true)
    @NotNull
    private String phoneNumber;
    @Column(name = "country_phone_index")
    private String countryPhoneIndex;
    @JsonIgnore
    @Column(name = "sms_active")
    private Boolean smsActive;
    @JsonIgnore
    @Column(name = "email_active")
    private Boolean emailActive;
    @JsonIgnore
    @Column(name = "enabled")
    private boolean enabled;

    @JsonIgnore
    @Column(name = "last_password_reset_date")
    private Timestamp lastPasswordResetDate;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;

    @Transient
    private List<Long> authIds;

    @Column(name = "activation_prefered_method")
    private int activationPreferedMethod; // 1 for SMS 2 for e-mail | Default is SMS
    @JsonIgnore
    @Column(name = "recovery_prefered_method")
    private int recoveryPreferedMethod = 1; // 1 for SMS 2 for e-mail | Default is SMS

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    private List<Authority> authorities;

    @Column
    private Long profilePicture;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserFile> userFiles;

    @Transient
    private String image;
    @Transient
    private String oldPassword;
    @Transient
    private String newPassword;

    @OneToMany( cascade = { CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE }, fetch = FetchType.LAZY, orphanRemoval = true )
    private List<Car> cars;

    @JsonProperty
    public Long getProfilePicture() {
        return profilePicture;
    }

    @JsonIgnore
    public void setProfilePicture(Long profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<UserFile> getUserFiles() {
        return userFiles;
    }

    public void setUserFiles(List<UserFile> userFiles) {
        this.userFiles = userFiles;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        Timestamp now = new Timestamp(DateTime.now().getMillis());
        this.setLastPasswordResetDate( now );
        this.password = password;
    }

    public Boolean getEmailActive() {
        return emailActive;
    }

    public void setEmailActive(Boolean emailActive) {
        this.emailActive = emailActive;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Timestamp getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(Timestamp lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public List<Long> getAuthIds() {
        return authIds;
    }

    public void setAuthIds(List<Long> authIds) {
        this.authIds = authIds;
    }

    public void setSmsActive(Boolean smsActive) {
        this.smsActive = smsActive;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getSmsActive() {
        return smsActive;
    }

    public int getActivationPreferedMethod() {
        return activationPreferedMethod;
    }

    public void setActivationPreferedMethod(int activationPreferedMethod) {
        this.activationPreferedMethod = activationPreferedMethod;
    }

    public String getCountryPhoneIndex() {
        return countryPhoneIndex;
    }

    public void setCountryPhoneIndex(String countryPhoneIndex) {
        this.countryPhoneIndex = countryPhoneIndex;
    }

    public int getRecoveryPreferedMethod() {
        return recoveryPreferedMethod;
    }

    public void setRecoveryPreferedMethod(int recoveryPreferedMethod) {
        this.recoveryPreferedMethod = recoveryPreferedMethod;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}