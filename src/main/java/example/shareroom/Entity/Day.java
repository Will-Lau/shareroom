package example.shareroom.Entity;

public class Day {
    private String dayId;

    private String busytime;

    public String getDayId() {
        return dayId;
    }

    public void setDayId(String dayId) {
        this.dayId = dayId == null ? null : dayId.trim();
    }

    public String getBusytime() {
        return busytime;
    }

    public void setBusytime(String busytime) {
        this.busytime = busytime == null ? null : busytime.trim();
    }
}