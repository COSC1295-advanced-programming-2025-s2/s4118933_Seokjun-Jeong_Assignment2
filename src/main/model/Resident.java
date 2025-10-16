package main.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Resident implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;
    private final Gender gender;
    private final LocalDate admittedOn;

    public Resident(String id, String name, Gender gender) {
        this.id = id; this.name = name; this.gender = gender; this.admittedOn = LocalDate.now();
    }
    public String getId(){ return id; }
    public String getName(){ return name; }
    public Gender getGender(){ return gender; }
    public LocalDate getAdmittedOn(){ return admittedOn; }
}
