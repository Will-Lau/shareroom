package example.shareroom.service;




import example.shareroom.Entity.Appointment;
import example.shareroom.Entity.AppointmentExample;
import example.shareroom.Entity.User;
import example.shareroom.Entity.UserExample;
import example.shareroom.Request.GetUsersRule;
import example.shareroom.Response.UserCodeResponse;
import example.shareroom.Response.UserResponse;
import example.shareroom.Response.UserTokenResponse;
import example.shareroom.UsefulUtils.AesEncryptUtils;
import example.shareroom.UsefulUtils.WxMappingJackson2HttpMessageConverter;
import example.shareroom.dao.AppointmentMapper;
import example.shareroom.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

;import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static example.shareroom.UsefulUtils.DozerUtils.mapList;

@Service
public class Userservice {

    @Autowired
    UserMapper userMapper;

    @Autowired
    AppointmentMapper appointmentMapper;

    @Autowired
    private RestTemplate restTemplate;

    private static ExecutorService executor= Executors.newSingleThreadExecutor();

    private Thread thread= new Thread(new Runnable() {
        @Override
        public void run() {
            appointmentservice.checkIsFinish();
        }
    });

    private int IsRun=0;

    @Autowired
    private Appointmentservice appointmentservice;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource(name = "redisTemplate")
    private ValueOperations<String,Object> valueOperations;

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, Object> hashOperations;

    private String appid="wx413438e9230a1ade";

    private String secret="3def03d9cd71a9dbce967c452301f052";

    private String grantType="authorization_code";

    //使用code换取token，如果是第一次登录就新建用户
    public UserTokenResponse login(String code) throws Exception {

        if(IsRun==0) {thread.start();IsRun=1;}


        //为restTemplate增添对于不同返回值的错误处理
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());

        //向微信官方接口发送请求获得userOpenIdResponse
        String url= "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
        UserCodeResponse userOpenIdResponse=restTemplate.getForObject(url, UserCodeResponse.class);
        if(null == userOpenIdResponse || !userOpenIdResponse.valid()) userOpenIdResponse=null;

        //根据openid和session_key使用aes加密生成session_id,在前端称作token
        String session_id= AesEncryptUtils.encrypt(userOpenIdResponse.getOpenid(),userOpenIdResponse.getSession_key());
        //创建返回给前端的UsersessionResponse
        UserTokenResponse userSessionResponse= UserTokenResponse.builder().token(session_id).build();

        //将session_id存入redis,过期时间为30分钟
        stringRedisTemplate.opsForValue().set(session_id,userOpenIdResponse.getOpenid(),30*60, TimeUnit.SECONDS);

        System.out.println(userOpenIdResponse.getOpenid());

        //检查数据库中有无该用户，没有则新建
        User user=userMapper.selectByPrimaryKey(userOpenIdResponse.getOpenid());
        if(user==null){
            User new_user=new User();
            new_user.setUserId(userOpenIdResponse.getOpenid());
            new_user.setMark((double)20);
            new_user.setUsetime((double)0);
            userMapper.insert(new_user);
        }

        return userSessionResponse;
    }

    public String getUserId(String token){
        if(token==null||token.equals("")) return null;
        String userId=stringRedisTemplate.opsForValue().get(token);
        return userId;
    }


    public String getToken(String userId){
        if(userId==null||userId.equals("")) return "";
        Set<String> tokens= stringRedisTemplate.keys("*");
        Iterator iterator= tokens.iterator();
        while (iterator.hasNext()){
            String temp=(String) iterator.next();
            if(stringRedisTemplate.opsForValue().get(temp)==userId) return temp;
        }
        return null;
    }


    public List<User> getUsers(User user){
        UserExample userExample =new UserExample();
        UserExample.Criteria criteria=userExample.createCriteria();

        if(user.getUserId()!=null) criteria.andUserIdEqualTo(user.getUserId());
        if(user.getName()!=null) criteria.andNameLike("%"+user.getName()+"%");
        if(user.getAvatar()!=null) criteria.andAvatarEqualTo(user.getAvatar());
        if(user.getMark()!=null) criteria.andMarkEqualTo(user.getMark());
        if(user.getUsetime()!=null) criteria.andUsetimeEqualTo(user.getUsetime());
        List<User> users=userMapper.selectByExample(userExample);
        if(users==null||users.size()==0) return null;
        else return users;
    }


    //直接修改
    public String updateUsers(User user){
        User user1=userMapper.selectByPrimaryKey(user.getUserId());
        if(user==null) return "用户不存在";
        else {
            userMapper.updateByPrimaryKeySelective(user);
            return "修改成功";
        }
    }


    public String updateusermark(String aId, double mark) throws ParseException {
        Appointment appointment=appointmentMapper.selectByPrimaryKey(aId);
        if(appointment==null) return "该预约不存在";
        if(appointment.getState().equals("request")) return "该订单无效";
        if(appointment.getState().equals("cancel")) return "该订单已经取消";
        String markAid=appointmentservice.getLastFinishedAppointment(appointment);
        Appointment lastA=appointmentMapper.selectByPrimaryKey(markAid);
        if(lastA==null) return "该预约的上一个预约不存在";


        User temp=userMapper.selectByPrimaryKey(lastA.getUserId());

        if(temp!=null)
        {
            double oldmark=temp.getMark();
            double oldusetime=temp.getUsetime();
            //double newmark=((oldmark*oldusetime)+mark)/(oldusetime+1);
            double newmark=oldmark+mark;
            temp.setMark(newmark);
            temp.setUsetime(oldusetime+1);
            userMapper.updateByPrimaryKeySelective(temp);
            return temp.getUserId();
        }
        else return "上一个预约的用户不存在";

    }




}