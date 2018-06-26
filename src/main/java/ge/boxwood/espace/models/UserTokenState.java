package ge.boxwood.espace.models;

public class UserTokenState {
    private String access_token;
    private String refresh_token;
    private Long expires_in;
    private boolean userActivated;

    public UserTokenState() {
        this.access_token = null;
        this.refresh_token = null;
        this.expires_in = null;
        this.userActivated = false;
    }

    public UserTokenState(String access_token, String refresh_token, long expires_in, boolean userActivated) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.expires_in = expires_in;
        this.userActivated = userActivated;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
    }

    public boolean isUserActivated() {
        return userActivated;
    }

    public void setUserActivated(boolean userActivated) {
        this.userActivated = userActivated;
    }
}