package main.core;

import main.model.ShiftAssignment;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.*;

public class Roster implements Serializable {
    private final Map<DayOfWeek, List<ShiftAssignment>> byDay = new EnumMap<>(DayOfWeek.class);
    public Roster(){ for (DayOfWeek d : DayOfWeek.values()) byDay.put(d, new ArrayList<>()); }
    public void assign(ShiftAssignment a){ byDay.get(a.day()).add(a); }
    public List<ShiftAssignment> get(DayOfWeek day){ return byDay.get(day); }
    public Map<DayOfWeek, List<ShiftAssignment>> all(){ return Collections.unmodifiableMap(byDay); }
}
