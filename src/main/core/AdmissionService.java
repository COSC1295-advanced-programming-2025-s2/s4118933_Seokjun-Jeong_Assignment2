package main.core;

import main.exceptions.BedOccupiedException;
import main.exceptions.GenderIsolationException;
import main.model.*;

public class AdmissionService {

    // same gender in same room
    public void assign(Resident r, Room room, Bed bed){
        if (bed.isOccupied()) throw new BedOccupiedException("Bed occupied: " + bed.getBedId());

        boolean conflict = room.getBeds().stream()
                .filter(Bed::isOccupied)
                .anyMatch(b -> b.getOccupant().getGender() != r.getGender());
        if (conflict) throw new GenderIsolationException("Gender conflict in room " + room.getRoomId());

        bed.assign(r);
    }

    public void move(Room targetRoom, Bed targetBed, Resident r){
        if (targetBed.isOccupied()) throw new BedOccupiedException("Target bed occupied");
        boolean conflict = targetRoom.getBeds().stream()
                .filter(Bed::isOccupied)
                .anyMatch(b -> b.getOccupant().getGender() != r.getGender());
        if (conflict) throw new GenderIsolationException("Gender conflict in target room");
        targetBed.assign(r);
    }
}
