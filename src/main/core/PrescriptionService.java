package main.core;

import main.model.Prescription;
import main.model.Resident;
import java.io.Serializable;
import java.util.*;

public class PrescriptionService implements Serializable {
    private final Map<String, List<Prescription>> byResident = new HashMap<>();

    // 특정 환자의 처방 목록 가져오기 get particular list patient's prescription
    public List<Prescription> getFor(Resident r) {
        return byResident.getOrDefault(r.getId(), new ArrayList<>());
    }

    // add new prescription
    public void add(Resident r, Prescription p) {
        byResident.computeIfAbsent(r.getId(), k -> new ArrayList<>()).add(p);
    }
}
