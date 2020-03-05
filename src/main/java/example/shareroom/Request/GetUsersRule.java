package example.shareroom.Request;

public class GetUsersRule {
    private String token;

    private String name;

    private String avatar;

    private Double mark;

    private Double usetime;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar == null ? null : avatar.trim();
    }

    public Double getMark() {
        return mark;
    }

    public void setMark(Double mark) {
        this.mark = mark;
    }

    public Double getUsetime() {
        return usetime;
    }

    public void setUsetime(Double usetime) {
        this.usetime = usetime;
    }
}
