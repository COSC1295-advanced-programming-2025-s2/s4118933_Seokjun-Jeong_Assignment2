import main.core.CareHome;
import main.core.Roster;
import main.exceptions.PersistenceException;
import main.model.*;
import main.repo.InMemoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

public class SerializationTest {

    private CareHome sampleHome() {
        var repo = new InMemoryRepository<Staff, String>(Staff::getId);
        Nurse n1 = new Nurse("N1","Alice","alice","hash");
        Nurse n2 = new Nurse("N2","Bob","bob","hash");
        Doctor d1 = new Doctor("D1","DrKim","kim","hash");
        repo.save(n1); repo.save(n2); repo.save(d1);

        Roster roster = new Roster();
        for (DayOfWeek day : DayOfWeek.values()) {
            roster.assign(new ShiftAssignment(n1.getId(), Role.NURSE, day, ShiftType.MORNING));
            roster.assign(new ShiftAssignment(n2.getId(), Role.NURSE, day, ShiftType.EVENING));
            roster.assign(new ShiftAssignment(d1.getId(), Role.DOCTOR, day, ShiftType.MORNING));
        }
        return new CareHome(repo, roster);
    }

    @Test
    void save_then_load_preserves_state(@TempDir Path tempDir) {
        CareHome original = sampleHome();

        File saveFile = tempDir.resolve("carehome.dat").toFile();
        original.saveToFile(saveFile);

        CareHome loaded = CareHome.loadFromFile(saveFile);

        // check not same
        assertNotSame(original, loaded);

        // check the number of staff and type
        assertEquals(
                original.staff().findAll().size(),
                loaded.staff().findAll().size(),
                "staff count should match after load");

        // check the number of entry roaster in a day(for example: MONDAY)
        int originalMon = original.roster().get(DayOfWeek.MONDAY).size();
        int loadedMon   = loaded.roster().get(DayOfWeek.MONDAY).size();
        assertEquals(originalMon, loadedMon, "monday roster size should match");

        // minimum entry(nurse 2 + doctor 1 = 3) sanity check
        assertTrue(loadedMon >= 3, "expected at least 3 assignments on Monday");
    }

    @Test
    void load_nonexistent_file_throws() {
        File notExist = new File("this-file-should-not-exist-12345.dat");
        if (notExist.exists()) assertTrue(notExist.delete());
        assertThrows(PersistenceException.class, () -> CareHome.loadFromFile(notExist));
    }
}

