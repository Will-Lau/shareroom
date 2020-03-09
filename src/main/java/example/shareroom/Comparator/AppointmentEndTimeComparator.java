package example.shareroom.Comparator;

import example.shareroom.Entity.Appointment;
import example.shareroom.TempEntity.ComparableAppoinment;

import java.util.Comparator;

public class AppointmentEndTimeComparator implements Comparator<ComparableAppoinment> {

    @Override
    public int compare(ComparableAppoinment a1, ComparableAppoinment a2) {
        return 0-(a1.getEndDate().compareTo(a2.getEndDate()));
    }
}
