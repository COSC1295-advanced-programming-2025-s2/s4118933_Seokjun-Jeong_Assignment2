package main.model;

import java.io.Serializable;
import java.util.*;

public class Ward implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String wardId;
    private final List<Room> rooms = new ArrayList<>();

    public Ward(String wardId, int[] bedCountsPerRoom){ // length=6 가정
        this.wardId = wardId;
        for(int i=0;i<bedCountsPerRoom.length;i++){
            rooms.add(new Room(wardId + "-R" + (i+1), bedCountsPerRoom[i]));
        }
    }
    public String getWardId(){ return wardId; }
    public List<Room> getRooms(){ return rooms; }
}
