package example.shareroom.service;



import example.shareroom.Entity.Day;
import example.shareroom.Entity.DayExample;
import example.shareroom.dao.DayMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

;

@Service

public class Dayservice {

    @Autowired
    DayMapper dayMapper;


   public String createDay(String dayid){
       Day bo=dayMapper.selectByPrimaryKey(dayid);
       if(bo==null){
           Day day=new Day();
           day.setDayId(dayid);
           day.setBusytime("");
           dayMapper.insertSelective(day);
           return "创建成功";
       }
       else return "不可重复创建";

   }
    
   public Day getDay(String dayid) {
        
    return dayMapper.selectByPrimaryKey(dayid);
    
   }

   public List<Day> getthreeDays(String dayid) {
       String secondday=getnextDayid(dayid);
       String thirdday=getnextDayid(secondday);

       createDay(dayid);
       createDay(secondday);
       createDay(thirdday);

       DayExample dayExample=new DayExample();
       //实现or要分别建立三个criteria
       DayExample.Criteria criteria=dayExample.createCriteria();
       criteria.andDayIdEqualTo(dayid);

       DayExample.Criteria criteria2=dayExample.createCriteria();
       criteria2.andDayIdEqualTo(secondday);

       DayExample.Criteria criteria3=dayExample.createCriteria();
       criteria3.andDayIdEqualTo(thirdday);
       dayExample.or(criteria2);
       dayExample.or(criteria3);

       List<Day> days=dayMapper.selectByExample(dayExample);
       return days;

    
   }



    public String updateDay(Day day){
        Day day1=dayMapper.selectByPrimaryKey(day.getDayId());
        
        if(day1!=null)
        {
            dayMapper.updateByPrimaryKeySelective(day);
            
            return "修改完成";
        }
        else return "该日期不存在";

    }


    public String getnextDayid(String dayid){
        DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date temp = dft.parse(dayid);
            Calendar cld = Calendar.getInstance();
            cld.setTime(temp);
            cld.add(Calendar.DATE, 1);
            temp = cld.getTime();
            //获得下一天日期字符串
            String nextDay = dft.format(temp);
            return nextDay;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}