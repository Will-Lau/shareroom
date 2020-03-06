package example.shareroom.Controller;


import example.shareroom.Annotation.LoginRequired;
import example.shareroom.Entity.Appointment;
import example.shareroom.UsefulUtils.Method;
import example.shareroom.service.Appointmentservice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;


@Api(value = "预约管理", tags="Appointment")
@RestController
@RequestMapping("/body/*")
public class AppointmentController {

    @Autowired
    Appointmentservice appointmentservice=new Appointmentservice();

    @Autowired
    Method method;

    @LoginRequired
    @ApiOperation(value="根据条件查询预约记录", notes = "不需要的条件可不填,都不填就获取所有")
    @GetMapping("/getAppointments")
    public List<Appointment> getAppointments(Appointment appointment) {
        appointment.setUserId(method.getUseridByToken(appointment.getUserId()));
        return appointmentservice.getAppointments(appointment);
    }


    @ApiOperation("获取上一个完成的记录")
    @GetMapping("getlastAppointment/")
    public Appointment getlastAppointment() {
        
        return appointmentservice.getlastAppointment();
    }

    @LoginRequired
    @ApiOperation("创建Appointment")
    @PostMapping("/createAppointment")
    public String createAppointment(@RequestHeader String userId, @RequestBody Appointment appointment) throws ParseException {
        appointment.setUserId(method.getUseridByToken(userId));
        return appointmentservice.createAppointment(appointment);
    }

    @LoginRequired
    @ApiOperation("更新Appointment")
    @PostMapping("/updateAppointment")
    public String updateAppointment(@RequestHeader String userId,@RequestBody Appointment appointment) {
        appointment.setUserId(method.getUseridByToken(userId));
        return appointmentservice.updateAppointment(appointment);
        
    }

    @ApiOperation("同意Appointment")//todo:改成判断而不是同意，并返回相应的key
    @GetMapping("/agreeAppointment")
    public String agreeAppointment( String aid) {
        return appointmentservice.agreeAppointment(aid);
        
    }

    @ApiOperation("取消Appointment")//todo:改成判断而不是同意，并返回相应的key
    @GetMapping("/dropAppointment")
    public String dropAppointment(String aid) {
        return appointmentservice.setAppointmentcancel(aid);
        
    }

    @ApiOperation(value="获取给定时间之后的Appointment",notes = "这里的date形式为 yyyy-mm-dd hh:mm:ss")
    @GetMapping("/getAppointmentsBytime")
    public List<Appointment> getAppointmentsBytime(String date) throws ParseException {
        Date date1=new Date();
        date1=method.setDateByString(date1,date);
        return appointmentservice.getAppointmentsBytime(date1);
        
    }

    //  管理员接口

    @ApiOperation(value="管理员根据条件查询预约记录", notes = "不需要的条件可不填,都不填就获取所有")
    @GetMapping("/administratorGetAppointments")
    public List<Appointment> administratorGetAppointments(Appointment appointment) {

        return appointmentservice.getAppointments(appointment);
    }

    @ApiOperation("管理员创建Appointment")
    @PostMapping("/administratorCreateAppointment")
    public String administratorCreateAppointment(@RequestBody Appointment appointment) throws ParseException {
        //appointment.setUserId(method.getUseridByToken(appointment.getUserId()));
        return appointmentservice.createAppointment(appointment);
    }

    @ApiOperation("管理员更新Appointment")
    @PostMapping("/administratorUpdateAppointment")
    public String administratorUpdateAppointment(@RequestBody Appointment appointment) {
       // appointment.setUserId(method.getUseridByToken(appointment.getUserId()));
        return appointmentservice.updateAppointment(appointment);

    }

}

