package example.shareroom.service;



import com.mysql.cj.xdevapi.Collection;
import example.shareroom.Comparator.AppointmentComparator;
import example.shareroom.Comparator.AppointmentEndTimeComparator;
import example.shareroom.Entity.Appointment;
import example.shareroom.Entity.AppointmentExample;
import example.shareroom.Entity.Day;
import example.shareroom.Entity.DayExample;
import example.shareroom.Response.AppointmentResponse;
import example.shareroom.UsefulUtils.Method;
import example.shareroom.dao.AppointmentMapper;
import example.shareroom.dao.DayMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

;import static example.shareroom.UsefulUtils.DozerUtils.mapList;

@Service
public class Appointmentservice {

    @Autowired
    AppointmentMapper appointmentMapper ;


    Method method=new Method();

    @Autowired
    DayMapper dayMapper;

    public String gettimestring(Appointment appointment){
        int s=Integer.valueOf(appointment.getStartTime());
        int e=Integer.valueOf(appointment.getEndTime());
        String time="";
        for(int i=s;i<e;i++){
            time=time+String.valueOf(i);
            time=time+",";
        }
        return time;
    }


    public String createAppointment(Appointment appointment) throws ParseException {
        Day day = dayMapper.selectByPrimaryKey(appointment.getDate());

        //判断是否冲突
        if(method.Isconflict(gettimestring(appointment),day.getBusytime())) return "时间冲突！";

        //创建appointment
        appointment.setaId(method.getRandomUUid());
        appointment.setState("request");

        Date date=new Date();
        method.setDateByString(date,"2000-01-01 00:00:00");
        appointment.setDoneTime(date);

        Appointment temp = appointmentMapper.selectByPrimaryKey(appointment.getaId());
        if(temp==null) {
            appointmentMapper.insertSelective(appointment);
            return "创建成功";
            }
        else return "创建失败";
    }

    public String agreeAppointment(String aid){
        Appointment appointment=appointmentMapper.selectByPrimaryKey(aid);
        if(appointment.getState().equals("request")==false) return "已经同意过！";


        //否则就同意
        Day day =dayMapper.selectByPrimaryKey(appointment.getDate());
        day.setBusytime(day.getBusytime()+gettimestring(appointment));

        appointment.setState("agree");
        appointment.setKeycode(method.getRandomUUid());

        dayMapper.updateByPrimaryKeySelective(day);
        appointmentMapper.updateByPrimaryKeySelective(appointment);

        return appointment.getKeycode();

    }



    public List<Appointment> getAppointments(Appointment appointment) {
        AppointmentExample appointmentExample =new AppointmentExample();
        AppointmentExample.Criteria criteria=appointmentExample.createCriteria();

        if(appointment.getaId()!=null) criteria.andAIdEqualTo(appointment.getaId());
        if(appointment.getUserId()!=null) criteria.andUserIdEqualTo(appointment.getUserId());
        if(appointment.getDate()!=null) criteria.andDateEqualTo(appointment.getDate());
        if(appointment.getStartTime()!=null) criteria.andStartTimeLike("%"+appointment.getStartTime().substring(0,9)+"%");
        if(appointment.getEndTime()!=null) criteria.andEndTimeLike("%"+appointment.getEndTime().substring(0,9)+"%");
        if(appointment.getState()!=null) criteria.andStateEqualTo(appointment.getState());
        if(appointment.getKeycode()!=null) criteria.andKeycodeEqualTo(appointment.getKeycode());
        if(appointment.getActionTime()!=null) criteria.andActionTimeBetween(method.addMinutes(appointment.getActionTime(),-60)
                                                                           ,method.addMinutes(appointment.getActionTime(),60));
        if(appointment.getDoneTime()!=null) criteria.andDoneTimeBetween(method.addMinutes(appointment.getDoneTime(),-60)
                ,method.addMinutes(appointment.getDoneTime(),60));

        List<Appointment> appointments= appointmentMapper.selectByExample(appointmentExample);

        if(appointments==null||appointments.size()==0) return null;
        else {
            Collections.sort(appointments, new AppointmentComparator());
            return appointments;
        }
    }

    public List<Appointment> getAppointmentsBytime(Date date){
        AppointmentExample appointmentExample=new AppointmentExample();
        AppointmentExample.Criteria criteria=appointmentExample.createCriteria();
        criteria.andActionTimeGreaterThanOrEqualTo(date);
        List<Appointment> appointments=appointmentMapper.selectByExample(appointmentExample);
        if(appointments==null||appointments.size()==0) return null;
        else {
            Collections.sort(appointments, new AppointmentComparator());
            return appointments;
        }
    }


    public Appointment getlastAppointment(){

        AppointmentExample appointmentExample1=new AppointmentExample();
        AppointmentExample.Criteria criteria1=appointmentExample1.createCriteria();
        criteria1.andAIdIsNotNull();
        List<Appointment> appointments=appointmentMapper.selectByExample(appointmentExample1);
        Collections.sort(appointments,new AppointmentComparator());
        return appointments.get(0);
        
    }


    public String updateAppointment(Appointment appointment){
        Appointment temp=appointmentMapper.selectByPrimaryKey(appointment.getaId());
        
        if(temp!=null) 
        {
            appointmentMapper.updateByPrimaryKeySelective(appointment);
            return "操作完成";
        }
        else return "预约不存在";

    }

    

    public String setAppointmentcancel(String aid){
        Appointment temp=appointmentMapper.selectByPrimaryKey(aid);
        
        if(temp!=null) 
        {
            temp.setState("cancel");
            Day tempday=dayMapper.selectByPrimaryKey(temp.getDate());
            tempday.setBusytime(tempday.getBusytime().replaceAll(gettimestring(temp), ""));
            dayMapper.updateByPrimaryKeySelective(tempday);
            appointmentMapper.updateByPrimaryKeySelective(temp);
            return "操作完成";
        }
        else return "预约不存在";

    }

    public Appointment getLastFinishedAppointment(Appointment appointment){
        if(appointment==null) return  null;
        AppointmentExample appointmentExample=new AppointmentExample();
        AppointmentExample.Criteria criteria=appointmentExample.createCriteria();
        criteria.andDateEqualTo(appointment.getDate());
        criteria.andStateEqualTo("agree");
        criteria.andEndTimeLessThan(appointment.getEndTime());
        List<Appointment> appointments=appointmentMapper.selectByExample(appointmentExample);
        if(appointments==null||appointments.size()==0) return  null;
        Collections.sort(appointments,new AppointmentEndTimeComparator());
        return appointments.get(0);


    }



    //得到一个用户最近agree的预约

    public Appointment getLastAgreeAppointment(String userid){
        AppointmentExample appointmentExample=new AppointmentExample();
        AppointmentExample.Criteria criteria=appointmentExample.createCriteria();
        criteria.andUserIdEqualTo(userid);
        criteria.andStateEqualTo("agree");
        List<Appointment> appointments=appointmentMapper.selectByExample(appointmentExample);
        if(appointments==null||appointments.size()==0) return  null;
        Collections.sort(appointments,new AppointmentComparator());
        return appointments.get(0);
    }

}