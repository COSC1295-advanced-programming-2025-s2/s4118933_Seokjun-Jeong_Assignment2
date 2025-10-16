package main.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Prescription implements Serializable {
    private final Medicine medicine;
    private final String frequency;       // e.g. "Twice daily"
    private final LocalDateTime createdAt = LocalDateTime.now();
    private final String doctorId;        // who did prescription
    private boolean administered;         // administered or not

    public Prescription(Medicine medicine, String frequency, String doctorId) {
        this.medicine = medicine;
        this.frequency = frequency;
        this.doctorId = doctorId;
    }

    public Medicine getMedicine() { return medicine; }
    public String getFrequency() { return frequency; }
    public String getDoctorId() { return doctorId; }
    public boolean isAdministered() { return administered; }
    public void markAdministered() { administered = true; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return medicine + " (" + frequency + ") [" + (administered ? "✓" : "pending") + "]";
    }
}
