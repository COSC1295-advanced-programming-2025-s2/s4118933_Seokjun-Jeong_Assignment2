package main.model;

import java.io.Serializable;
import java.util.*;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String roomId;
    private final List<Bed> beds = new ArrayList<>();

    public Room(String roomId, int bedCount){
        this.roomId = roomId;
        for(int i=1;i<=bedCount;i++) beds.add(new Bed(roomId + "-B" + i));
    }
    public String getRoomId(){ return roomId; }
    public List<Bed> getBeds(){ return beds; }
}
