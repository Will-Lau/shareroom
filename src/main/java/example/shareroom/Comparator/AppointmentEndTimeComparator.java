package example.shareroom.Comparator;

import example.shareroom.Entity.Appointment;

import java.util.Comparator;

public class AppointmentEndTimeComparator implements Comparator<Appointment> {

    @Override
    public int compare(Appointment a1, Appointment a2) {
        return 0-(a1.getEndTime().compareTo(a2.getEndTime()));
    }
}
