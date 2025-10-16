package main.app;

import main.core.CareHome;
import main.core.Roster;
import main.exceptions.ComplianceViolationException;
import main.model.*;
import main.repo.InMemoryRepository;

import java.io.File;
import java.time.DayOfWeek;
import java.util.Scanner;

public class Main {
    private static final File SAVE = new File("carehome.dat");

    public static void main(String[] args) {
        var repo = new InMemoryRepository<Object,String>(o -> {
            if (o instanceof Staff s) return s.getId(); else return o.toString();
        });
        CareHome home = new CareHome(repo, new Roster());
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("\n=== RMIT Care Home (Phase1) ===");
            System.out.println("1) Add Nurse   2) Add Doctor   3) Assign Shift");
            System.out.println("4) Show Day Roster  5) Check Compliance");
            System.out.println("6) Save  7) Load  0) Exit");
            System.out.print("> ");
            switch(sc.nextLine().trim()){
                case "1" -> addNurse(home, sc);
                case "2" -> addDoctor(home, sc);
                case "3" -> assignShift(home, sc);
                case "4" -> showRoster(home, sc);
                case "5" -> check(home);
                case "6" -> { home.saveToFile(SAVE); System.out.println("Saved."); }
                case "7" -> {
                    CareHome loaded = CareHome.loadFromFile(SAVE);
                    System.out.println("Loaded. (You can keep using current instance for demo)");
                }
                case "0" -> { System.out.println("Bye"); return; }
                default -> System.out.println("?");
            }
        }
    }
    static void addNurse(CareHome h, Scanner sc){
        System.out.print("Nurse id name username pwdHash: ");
        String[] t = sc.nextLine().split("\\s+");
        h.staff().save(new Nurse(t[0], t[1], t[2], t[3]));
        System.out.println("Added nurse.");
    }
    static void addDoctor(CareHome h, Scanner sc){
        System.out.print("Doctor id name username pwdHash: ");
        String[] t = sc.nextLine().split("\\s+");
        h.staff().save(new Doctor(t[0], t[1], t[2], t[3]));
        System.out.println("Added doctor.");
    }
    static void assignShift(CareHome h, Scanner sc){
        System.out.print("staffId dayOfWeek(MONDAY..SUNDAY) shift(MORNING/EVENING): ");
        String[] t = sc.nextLine().split("\\s+");
        var d = DayOfWeek.valueOf(t[1].toUpperCase());
        var type = ShiftType.valueOf(t[2].toUpperCase());
        // role은 간단히 입력자에게서 유추: 저장된 Staff의 instanceof 검사
        var staff = h.staff().findById(t[0]).orElseThrow(() -> new RuntimeException("no staff"));
        Role role = (staff instanceof Doctor) ? Role.DOCTOR : Role.NURSE;
        h.roster().assign(new ShiftAssignment(t[0], role, d, type));
        System.out.println("Assigned.");
    }
    static void showRoster(CareHome h, Scanner sc){
        System.out.print("dayOfWeek(MONDAY..SUNDAY): ");
        var day = DayOfWeek.valueOf(sc.nextLine().trim().toUpperCase());
        h.roster().get(day).forEach(a ->
                System.out.println(a.role()+" "+a.staffId()+" "+a.type()+" "+a.hours()+"h"));
    }
    static void check(CareHome h){
        try { h.checkCompliance(); System.out.println("Compliance OK V"); }
        catch (ComplianceViolationException e){ System.out.println("X " + e.getMessage()); }
    }
}
