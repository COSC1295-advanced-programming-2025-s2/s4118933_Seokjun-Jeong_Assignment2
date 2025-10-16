package main.core;

import main.model.Role;
import main.model.Staff;

public class Session {
    private Staff current;

    public boolean isLoggedIn() { return current != null; }

    public Staff current() { return current; }

    public void signIn(Staff staff) { this.current = staff; }

    public void signOut() { this.current = null; }

    public boolean hasRole(Role r) {
        return isLoggedIn() && current.getRole() == r;
    }
}
