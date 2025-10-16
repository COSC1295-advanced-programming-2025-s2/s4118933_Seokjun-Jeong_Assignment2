package main.model;

import java.io.Serializable;

public class Bed implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String bedId;
    private Resident occupant; // null이면 비어있음

    public Bed(String bedId){ this.bedId = bedId; }
    public String getBedId(){ return bedId; }
    public Resident getOccupant(){ return occupant; }
    public boolean isOccupied(){ return occupant != null; }
    public void assign(Resident r){ this.occupant = r; }
    public void vacate(){ this.occupant = null; }
}
