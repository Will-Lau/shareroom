package example.shareroom.UsefulUtils;

public class WxError {

    private Integer errcode;
    private String errmsg;

    //由于微信官方接口在调用正确和错误时返回的字段不同，做一个错误处理
    @Override
    public String toString() {
        return "WxError{" +
                "errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }

    public boolean valid(){
        return errcode == null || errcode == 0;
    }
}