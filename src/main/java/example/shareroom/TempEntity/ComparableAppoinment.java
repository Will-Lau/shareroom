package example.shareroom.TempEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ComparableAppoinment implements Comparable<ComparableAppoinment>{
    private String aId;

    private Date endDate;

    public ComparableAppoinment(String aId,String date,String end) throws ParseException {
        this.aId=aId;
        String newend;
        if(Integer.valueOf(end)>=0&&Integer.valueOf(end)<10)
             newend="0"+end;
        else newend=end;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr=date+" "+newend+":"+"00:00";
        endDate=df.parse(dateStr);
    }
    @Override
    public int compareTo(ComparableAppoinment comparableAppoinment){
        return this.endDate.compareTo(comparableAppoinment.getEndDate());
    }



    public String getaId() {
        return aId;
    }

    public void setaId(String aId) {
        this.aId = aId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
