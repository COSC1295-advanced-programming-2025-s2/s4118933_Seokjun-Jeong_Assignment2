package main.model;

import java.time.LocalTime;
public enum ShiftType {
    MORNING(LocalTime.of(8,0), LocalTime.of(16,0)),
    EVENING(LocalTime.of(14,0), LocalTime.of(22,0));
    public final LocalTime start; public final LocalTime end;
    ShiftType(LocalTime s, LocalTime e){ this.start=s; this.end=e; }
    public int hours(){ return end.getHour() - start.getHour(); } // 8
}