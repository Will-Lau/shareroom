package example.shareroom.Controller;

import example.shareroom.Annotation.LoginRequired;
import example.shareroom.Entity.User;
import example.shareroom.Response.UserTokenResponse;
import example.shareroom.UsefulUtils.Method;
import example.shareroom.service.Userservice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;



@Api(value = "用户管理",tags="User")
@RestController
@RequestMapping(value ="/user/*")
public class UserController {

    @Autowired
    Userservice userservice=new Userservice();

    @Autowired
    Method method;


    @LoginRequired
    @ApiOperation("获取用户信息")
    @GetMapping("/getUsers")
    public List<User> getUsers(User user) {
        user.setUserId(method.getUseridByToken(user.getUserId()));
		return userservice.getUsers(user);
    }


    @ApiOperation(value = "登录", notes = "输入code返回token,得到的token在后续非管理员请求中作为userId的值\n\rtoken在一定时间内有效，过期需重新登录", tags = "User")
    @GetMapping("/login")
    public UserTokenResponse login(String code) throws Exception {

        return userservice.login(code);

    }

    @LoginRequired
    @ApiOperation("更新用户信息")
    @PostMapping("/updateUser")
    public String updateUser(@RequestHeader String userId,@RequestBody User user) {
        user.setUserId(method.getUseridByToken(userId));
        return userservice.updateUsers(user);
    }

    //@LoginRequired
    @ApiOperation("给上一个预约的用户打分，aId填当前预约。")
    @GetMapping("/markLastuser")
    public String markLastUser(String aId,double mark) throws ParseException {
        //userId=method.getUseridByToken(userId);
        return userservice.updateusermark(aId,mark);
    }



    //管理员

    @ApiOperation("管理员获取用户信息")
    @GetMapping("/administratorGetUsers")
    public List<User> administratorGetUsers(User user) {

        return userservice.getUsers(user);
    }


    @ApiOperation("管理员更新用户信息")
    @PostMapping("/administratorUpdateUser")
    public String administratorUpdateUser(@RequestBody User user) {
        //user.setUserId(method.getUseridByToken(user.getUserId()));
        return userservice.updateUsers(user);
    }


}

