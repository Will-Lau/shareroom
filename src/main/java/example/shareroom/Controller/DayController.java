package example.shareroom.Controller;


import example.shareroom.Annotation.LoginRequired;
import example.shareroom.Entity.Day;
import example.shareroom.service.Dayservice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "日期管理",tags="Day")
@RestController
@RequestMapping("/day/*")
public class DayController {

    @Autowired
    Dayservice dayservice=new Dayservice();



    @ApiOperation("获取该日预约信息")
    @GetMapping("/getDay")
    public Day getDay(String dayid) {
        
        return dayservice.getDay(dayid);
    }


    @ApiOperation("获取从该日起三天的预约信息")
    @GetMapping("/getThreeDays")
    public List<Day> getThreeDays(String dayid) {
        
        return dayservice.getthreeDays(dayid);
    }


   /* @ApiOperation("创建该日默认预约信息")
    @GetMapping("createdefaultDay")
    public String createdefaultDay(String dayid) {
        return dayservice.createDay(dayid);
    }*/


    @ApiOperation("更新该日预约信息,不更新的值不用写")
    @PostMapping(value="updateDay")
    public String updateDay(@RequestBody Day day) {
        return dayservice.updateDay(day);
        
    }


}

