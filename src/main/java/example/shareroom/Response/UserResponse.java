package example.shareroom.Response;

public class UserResponse {

    private String name;

    private String avatar;

    private Double mark;

    private Double usetime;


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
