package example.shareroom.dao;

import example.shareroom.Entity.Day;
import example.shareroom.Entity.DayExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DayMapper {
    int countByExample(DayExample example);

    int deleteByExample(DayExample example);

    int deleteByPrimaryKey(String dayId);

    int insert(Day record);

    int insertSelective(Day record);

    List<Day> selectByExample(DayExample example);

    Day selectByPrimaryKey(String dayId);

    int updateByExampleSelective(@Param("record") Day record, @Param("example") DayExample example);

    int updateByExample(@Param("record") Day record, @Param("example") DayExample example);

    int updateByPrimaryKeySelective(Day record);

    int updateByPrimaryKey(Day record);
}