package example.shareroom.Controller;


import example.shareroom.Annotation.LoginRequired;
import example.shareroom.Entity.Credit;
import example.shareroom.UsefulUtils.Method;
import example.shareroom.service.Creditservice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "Credit管理",tags="Credit")
@RestController
@RequestMapping("/credit/*")
public class CreditController {

    @Autowired
    Creditservice creditservice=new Creditservice();

    @Autowired
    Method method;

    @LoginRequired
    @ApiOperation(value ="根据条件获取Credit",notes = "不需要的条件可不填,都不填就获取所有")
    @GetMapping("/getCredits")
    public List<Credit> getCredits(Credit credit) {
        credit.setUserId(method.getUseridByToken(credit.getUserId()));
        return creditservice.getCredit(credit);
    }

    @LoginRequired
    @ApiOperation("创建Credit")
    @PostMapping("/createCredit")
    public String createBody(@RequestBody Credit credit) {
        credit.setUserId(method.getUseridByToken(credit.getUserId()));
        return creditservice.createCredit(credit);
    }

    @LoginRequired
    @ApiOperation("更新Credit,不更新的值不用写")
    @PostMapping("/updateCredit")
    public String updateCredit(@RequestBody Credit credit) {
        credit.setUserId(method.getUseridByToken(credit.getUserId()));
        return creditservice.updateCredit(credit);
        
    }


    //管理员
    @ApiOperation(value ="管理员根据条件获取Credit",notes = "不需要的条件可不填,都不填就获取所有")
    @GetMapping("/administratorGetCredits")
    public List<Credit> administratorGetCredits(Credit credit) {
        //credit.setUserId(method.getUseridByToken(credit.getUserId()));
        return creditservice.getCredit(credit);
    }

    @ApiOperation("管理员创建Credit")
    @PostMapping("/administratorCreateCredit")
    public String administratorCreateCredit(@RequestBody Credit credit) {
        //credit.setUserId(method.getUseridByToken(credit.getUserId()));
        return creditservice.createCredit(credit);
    }

    @ApiOperation("管理员更新Credit,不更新的值不用写")
    @PostMapping("/administratorUpdateCredit")
    public String administratorUpdateCredit(@RequestBody Credit credit) {
        //credit.setUserId(method.getUseridByToken(credit.getUserId()));
        return  creditservice.updateCredit(credit);

    }
}

