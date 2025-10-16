package test;

import main.core.*;
import main.core.CareHome;
import main.core.Roster;
import main.exceptions.ComplianceViolationException;
import main.model.*;
import main.repo.InMemoryRepository;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

public class CareHomeComplianceTest {

    private CareHome compliantHome(){
        var repo = new InMemoryRepository<Object,String>(o -> ((Staff)o).getId());
        Nurse n1 = new Nurse("N1","Alice","alice","hash");
        Nurse n2 = new Nurse("N2","Bob","bob","hash");
        Doctor d1 = new Doctor("D1","DrKim","kim","hash");
        repo.save(n1); repo.save(n2); repo.save(d1);

        Roster roster = new Roster();
        for (DayOfWeek day : DayOfWeek.values()){
            roster.assign(new ShiftAssignment(n1.getId(), Role.NURSE, day, ShiftType.MORNING));
            roster.assign(new ShiftAssignment(n2.getId(), Role.NURSE, day, ShiftType.EVENING));
            roster.assign(new ShiftAssignment(d1.getId(), Role.DOCTOR, day, ShiftType.MORNING));
        }
        return new CareHome(repo, roster);
    }

    @Test
    void compliance_ok(){ assertDoesNotThrow(() -> compliantHome().checkCompliance()); }

    @Test
    void missing_evening_fails(){
        CareHome h = compliantHome();
        var day = DayOfWeek.FRIDAY;
        h.roster().get(day).removeIf(a -> a.role()==Role.NURSE && a.type()==ShiftType.EVENING);
        assertThrows(ComplianceViolationException.class, h::checkCompliance);
    }

    @Test
    void nurse_over_8h_same_day_fails(){
        CareHome h = compliantHome();
        var day = DayOfWeek.MONDAY;
        h.roster().assign(new ShiftAssignment("N1", Role.NURSE, day, ShiftType.EVENING)); // 16h
        assertThrows(ComplianceViolationException.class, h::checkCompliance);
    }

    @Test
    void no_doctor_any_day_fails(){
        CareHome h = compliantHome();
        var day = DayOfWeek.TUESDAY;
        h.roster().get(day).removeIf(a -> a.role()==Role.DOCTOR);
        assertThrows(ComplianceViolationException.class, h::checkCompliance);
    }
}
