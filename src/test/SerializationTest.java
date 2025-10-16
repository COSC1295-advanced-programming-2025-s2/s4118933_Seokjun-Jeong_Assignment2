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

        // 같은 객체가 아님을 확인
        assertNotSame(original, loaded);

        // 스태프 수/타입 보존 확인
        assertEquals(
                original.staff().findAll().size(),
                loaded.staff().findAll().size(),
                "staff count should match after load");

        // 하루치 로스터 엔트리 수 보존 확인 (예: MONDAY)
        int originalMon = original.roster().get(DayOfWeek.MONDAY).size();
        int loadedMon   = loaded.roster().get(DayOfWeek.MONDAY).size();
        assertEquals(originalMon, loadedMon, "monday roster size should match");

        // 최소한 기대 엔트리(간호사 2 + 의사 1 = 3)가 존재하는지 sanity check
        assertTrue(loadedMon >= 3, "expected at least 3 assignments on Monday");
    }

    @Test
    void load_nonexistent_file_throws() {
        File notExist = new File("this-file-should-not-exist-12345.dat");
        if (notExist.exists()) assertTrue(notExist.delete());
        assertThrows(PersistenceException.class, () -> CareHome.loadFromFile(notExist));
    }
}

