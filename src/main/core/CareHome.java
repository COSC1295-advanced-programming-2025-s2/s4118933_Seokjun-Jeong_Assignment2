package main.core;

import main.exceptions.ComplianceViolationException;
import main.exceptions.PersistenceException;
import main.model.Role;
import main.model.ShiftAssignment;
import main.model.ShiftType;
import main.model.Staff;
import main.repo.Repository;

import java.io.*;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

public class CareHome implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Repository<Staff, String> staffRepo;
    private final Roster roster;
    private final java.util.List<main.model.Ward> wards = new java.util.ArrayList<>();
    public java.util.List<main.model.Ward> wards(){ return wards; }
    public void addWard(main.model.Ward w){ wards.add(w); }

    public CareHome(Repository<Staff,String> staffRepo, Roster roster){
        this.staffRepo = staffRepo;
        this.roster = roster;
    }
    public Repository<Staff,String> staff(){ return staffRepo; }
    public Roster roster(){ return roster; }

    public void checkCompliance(){
        // nurse Morning/Evening cover everday
        for (DayOfWeek d : DayOfWeek.values()){
            var todays = roster.get(d).stream().filter(a -> a.role()==Role.NURSE).collect(Collectors.toList());
            boolean hasMorning = todays.stream().anyMatch(a -> a.type()==ShiftType.MORNING);
            boolean hasEvening = todays.stream().anyMatch(a -> a.type()==ShiftType.EVENING);
            if (!hasMorning || !hasEvening)
                throw new ComplianceViolationException("Nurse coverage missing on " + d);
        }
        // not allow more than 8 hours
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
        // see doctor at least once in a day
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
