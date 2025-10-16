package main.core;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogService implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Action implements Serializable {
        public final LocalDateTime when;
        public final String staffId;
        public final String action;
        public final String details;
        public Action(LocalDateTime when, String staffId, String action, String details) {
            this.when = when; this.staffId = staffId; this.action = action; this.details = details;
        }
        @Override public String toString() {
            return when + " [" + staffId + "] " + action + " - " + details;
        }
    }

    private final List<Action> entries = new ArrayList<>();

    public void log(String staffId, String action, String details) {
        entries.add(new Action(LocalDateTime.now(), staffId, action, details));
    }

    public List<Action> entries() { return entries; }

    public void save(File f) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f))) {
            out.writeObject(this);
        } catch (IOException e) { /* ignore for now */ }
    }

    public static LogService load(File f) {
        if (!f.exists()) return new LogService();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (LogService) in.readObject();
        } catch (Exception e) {
            return new LogService();
        }
    }
}

