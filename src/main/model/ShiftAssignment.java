package main.model;

import java.io.Serializable;
import java.time.DayOfWeek;

public class ShiftAssignment implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String staffId;
    private final Role role;
    private final DayOfWeek day;
    private final ShiftType type;

    public ShiftAssignment(String staffId, Role role, DayOfWeek day, ShiftType type){
        this.staffId = staffId; this.role = role; this.day = day; this.type = type;
    }
    public String staffId(){ return staffId; }
    public Role role(){ return role; }
    public DayOfWeek day(){ return day; }
    public ShiftType type(){ return type; }
    public int hours(){ return type.hours(); }
}
