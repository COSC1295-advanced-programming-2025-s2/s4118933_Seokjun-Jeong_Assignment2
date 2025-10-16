package main.core;

import main.exceptions.ComplianceViolationException;
import main.exceptions.PersistenceException;
import main.model.Role;
import main.model.ShiftAssignment;
import main.model.ShiftType;
import main.repo.Repository;

import java.io.*;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

public class CareHome implements Serializable {
    private final Repository<Object,String> staffRepo; // Staff 상위 타입 호환을 위해 Object 사용
    private final Roster roster;

    public CareHome(Repository<Object,String> staffRepo, Roster roster){
        this.staffRepo = staffRepo; this.roster = roster;
    }
    public Repository<Object,String> staff(){ return staffRepo; }
    public Roster roster(){ return roster; }

    public void checkCompliance(){
        // 매일 간호사 Morning/Evening 커버
        for (DayOfWeek d : DayOfWeek.values()){
            var todays = roster.get(d).stream().filter(a -> a.role()==Role.NURSE).collect(Collectors.toList());
            boolean hasMorning = todays.stream().anyMatch(a -> a.type()==ShiftType.MORNING);
            boolean hasEvening = todays.stream().anyMatch(a -> a.type()==ShiftType.EVENING);
            if (!hasMorning || !hasEvening)
                throw new ComplianceViolationException("Nurse coverage missing on " + d);
        }
        // 간호사 하루 8시간 초과 금지
        for (DayOfWeek d : DayOfWeek.values()){
            Map<String,Integer> hours = new HashMap<>();
            for (ShiftAssignment a : roster.get(d)){
                if (a.role()==Role.NURSE) hours.merge(a.staffId(), a.hours(), Integer::sum);
            }
            for (var e : hours.entrySet()){
                if (e.getValue() > 8)
                    throw new ComplianceViolationException("Nurse "+e.getKey()+" exceeds 8h on "+d);
            }
        }
        // 매일 의사 최소 1회 배정
        for (DayOfWeek d : DayOfWeek.values()){
            boolean hasDoctor = roster.get(d).stream().anyMatch(a -> a.role()==Role.DOCTOR);
            if (!hasDoctor) throw new ComplianceViolationException("No doctor assigned on " + d);
        }
    }

    public void saveToFile(File f){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))){
            oos.writeObject(this);
        } catch (IOException e){ throw new PersistenceException("save failed", e); }
    }
    public static CareHome loadFromFile(File f){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))){
            return (CareHome) ois.readObject();
        } catch (IOException | ClassNotFoundException e){
            throw new PersistenceException("load failed", e);
        }
    }
}
