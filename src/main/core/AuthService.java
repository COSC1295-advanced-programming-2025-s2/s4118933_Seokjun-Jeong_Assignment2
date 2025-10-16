package main.core;

import main.exceptions.NotRosteredException;
import main.exceptions.UnauthorizedActionException;
import main.model.Role;
import main.model.Staff;
import java.time.LocalDateTime;

public class AuthService {
    private final Session session;

    public AuthService(Session session) { this.session = session; }

    public void require(Role role) {
        if (!session.isLoggedIn())
            throw new UnauthorizedActionException("Sign-in required.");

        Staff s = session.current();
        if (s.getRole() != role)
            throw new UnauthorizedActionException("Requires role: " + role);

        // TODO: integrate with roster later to verify on-duty staff current day
        boolean rostered = true;
        if (!rostered)
            throw new NotRosteredException("Not rostered at " + LocalDateTime.now());
    }
}
