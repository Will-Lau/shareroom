package example.shareroom.service;



import com.mysql.cj.xdevapi.Collection;
import example.shareroom.Comparator.AppointmentComparator;

import example.shareroom.Entity.Appointment;
import example.shareroom.Entity.AppointmentExample;
import example.shareroom.Entity.Day;
import example.shareroom.Entity.DayExample;
import example.shareroom.Response.AppointmentResponse;
import example.shareroom.TempEntity.ComparableAppoinment;
import example.shareroom.UsefulUtils.Method;
import example.shareroom.dao.AppointmentMapper;
import example.shareroom.dao.DayMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
            time=time+ i;
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
            if(!agreeAppointment(appointment.getaId()).equals("已经同意过！"))
                return appointment.getaId();
            else return "已经同意过该预约";
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
        if(appointment.getStartTime()!=null) criteria.andStartTimeIn(getMoreThanTime(appointment.getStartTime()));
        if(appointment.getEndTime()!=null) criteria.andEndTimeIn(getLessThanTim(appointment.getEndTime()));
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

    //得到一个finish的且actiontime最近的预约
    public Appointment getlastAppointment(){

        AppointmentExample appointmentExample1=new AppointmentExample();
        AppointmentExample.Criteria criteria1=appointmentExample1.createCriteria();
        criteria1.andAIdIsNotNull();
        criteria1.andStateEqualTo("finish");
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

    public String hourHelp(String hour){
        if(Integer.valueOf(hour)>=0&&Integer.valueOf(hour)<10){
            return "0"+hour;
        }else return hour;
    }

    //排查一个预约是否该finish
    public String setAppointmentfinish(Appointment appointment) throws ParseException {

        System.out.println("开始排查");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();
        df.format(date);

        //字符串拼成date
        String dateStr=appointment.getDate()+" "+hourHelp(appointment.getEndTime())+":"+"00:00";
        Date date1=df.parse(dateStr);
        if(date.compareTo(date1)>=0){

            return finishAppointment(appointment.getaId());

        }else {
            return "未到过期时间";
        }
    }


    public String finishAppointment(String aId){
        Appointment appointment=appointmentMapper.selectByPrimaryKey(aId);
        if(appointment==null) return "预约不存在";
        appointment.setState("finish");
        appointmentMapper.updateByPrimaryKeySelective(appointment);
        return "操作完成";

    }
    
    //取消
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

    //相对于这个预约的上一个finish预约的用户的userId


    //相对于这个预约的上一个finish预约aId
    public String getLastFinishedAppointment(Appointment appointment) throws ParseException {
        if(appointment==null) return  null;
        AppointmentExample appointmentExample=new AppointmentExample();
        AppointmentExample.Criteria criteria=appointmentExample.createCriteria();
        criteria.andStateEqualTo("finish");
        List<Appointment> appointments=appointmentMapper.selectByExample(appointmentExample);

        if(appointments==null||appointments.size()==0) return  null;
        List<ComparableAppoinment> comparableAppoinments=new ArrayList<>();
        for(Appointment item:appointments){
            comparableAppoinments.add(new ComparableAppoinment(item.getaId(),item.getDate(),item.getEndTime()));
        }
        ComparableAppoinment target=new ComparableAppoinment(appointment.getaId(),appointment.getDate(),appointment.getEndTime());
        ComparableAppoinment max=new ComparableAppoinment("fake","2000-01-01","1");
        for(ComparableAppoinment item:comparableAppoinments){
            if(item.compareTo(target)<0&&item.compareTo(max)>0) max=item;
        }


        return max.getaId();
    }



    //得到一个用户agree且actiontime最近的预约//已废弃
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


    public List<String> getMoreThanTime(String time){
        List<String> times=new ArrayList<>();
        Integer tim=Integer.valueOf(time);
        for(Integer i=tim;i<24;i++){
            times.add(i.toString());
        }
        return times;
    }
    public List<String> getLessThanTim(String time){
        List<String> times=new ArrayList<>();
        Integer tim=Integer.valueOf(time);
        for(Integer i=tim;i>=0;i--){
            times.add(i.toString());
        }
        return times;
    }

    public void checkIsFinish(){
        Timer timer = new Timer();
        // timer.scheduleAtFixedRate(new MyTask, );

        Calendar currentTime = Calendar.getInstance();
        currentTime.setTime(new Date());

        int currentHour = currentTime.get(Calendar.HOUR);
        int cuurentSec=   currentTime.get(Calendar.SECOND);
/*        currentTime.set(Calendar.HOUR, currentHour+1 );
        currentTime.set(Calendar.MINUTE, 0);

        currentTime.set(Calendar.MILLISECOND, 0);*/
        currentTime.set(Calendar.SECOND, cuurentSec+7);
        Date NextHour = currentTime.getTime();
        // System.out.println(NextHour);

        timer.scheduleAtFixedRate(new MyTask(), NextHour, 1000);
    }

    //检查是否过期
    class MyTask extends TimerTask {
        @Override
        public void run() {
            AppointmentExample appointmentExample=new AppointmentExample();
            appointmentExample.createCriteria().andStateEqualTo("agree");
            List<Appointment> appointments=appointmentMapper.selectByExample(appointmentExample);
            for(Appointment item:appointments){
                try {
                    setAppointmentfinish(item);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}