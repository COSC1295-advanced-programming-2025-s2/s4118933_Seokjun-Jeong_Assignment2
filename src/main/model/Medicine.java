package main.model;

import java.io.Serializable;

public class Medicine implements Serializable {
    private final String name;
    private final String dose;

    public Medicine(String name, String dose) {
        this.name = name;
        this.dose = dose;
    }

    public String getName() { return name; }
    public String getDose() { return dose; }

    @Override
    public String toString() {
        return name + " " + dose;
    }
}
