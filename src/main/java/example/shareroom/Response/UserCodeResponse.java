package example.shareroom.Response;

import example.shareroom.UsefulUtils.WxError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCodeResponse extends WxError {
    private String openid;
    private String session_key;
    private String unionid;

    @Override
    public String toString(){
        return "UserCode{"+
                "openid='" + openid + '\'' +
                ", session_key='" + session_key + '\'' +
                ", unionid='" + unionid + '\'' +
                '}' + "  " + super.toString();

    }
}
