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
    public String updateUser(@RequestBody User user) {
        user.setUserId(method.getUseridByToken(user.getUserId()));
        return userservice.updateUsers(user);
    }

    @LoginRequired
    @ApiOperation("给上一个用户打分，这里userId填自己的不是上一个用户")
    @GetMapping("/markLastuser")
    public String markLastUser(String userId,double mark) {
        //userId=method.getUseridByToken(userId);
        return userservice.updateusermark(userId,mark);
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

