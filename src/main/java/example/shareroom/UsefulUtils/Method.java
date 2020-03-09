package example.shareroom.UsefulUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class Method {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public static String getRandomUUid(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String uploadPic(MultipartFile file){
        if(file.isEmpty()){
            return "上传文件为空";
        }
        String url;

        String fileName=file.getOriginalFilename();
        fileName=getRandomUUid()+"_"+fileName;
        String path="./fileUpload/"+fileName;
        File destination =new File(path);
        if(destination.exists()){
            return "文件已经存在";
        }

        if(!destination.getParentFile().exists()){
            destination.getParentFile().mkdir();
        }
        try{

            file.transferTo(destination.getAbsoluteFile());
            url="http://120.55.161.129:8085/images/"+fileName;
            return url;

        } catch (IOException e) {
            e.printStackTrace();
            return "上传错误";
        }
    }

    public String getUseridByToken(String token){
        String userId=stringRedisTemplate.opsForValue().get(token);

        if(userId==null) return null;
        else return userId;
    }

    public boolean Isconflict(String timeString,String busytime){
        String[] st1=timeString.split(",");
        String[] st2=busytime.split(",");
        //String st1=timeString.replaceAll(",","");
        //String st2=busytime.replaceAll(",","");
        for(int i=0;i<st1.length;i++){
            for(int j=0;j<st2.length;j++){
                if(st1[i].equals(st2[j])) return true;
            }
        }
        return false;
    }

    public Date setDateByString(Date date,String d) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date=sdf.parse(d);
        return date;
    }

    public String getStringOfDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }


    public Date addMinutes(Date date, int num){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE,num);
        Date date1=calendar.getTime();
        return date1;
    }
}
