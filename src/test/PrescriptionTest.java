import main.core.PrescriptionService;
import main.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PrescriptionTest {
    @Test
    void addAndList() {
        PrescriptionService svc = new PrescriptionService();
        Resident r = new Resident("R1", "Alice", Gender.FEMALE);
        Prescription p = new Prescription(new Medicine("Paracetamol", "500mg"), "Twice daily", "D1");

        svc.add(r, p);

        assertEquals(1, svc.getFor(r).size());
        assertEquals("Paracetamol", svc.getFor(r).get(0).getMedicine().getName());
    }
}

