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

        // TODO: 나중에 Roster 연동해서 당일 근무 확인
        boolean rostered = true;
        if (!rostered)
            throw new NotRosteredException("Not rostered at " + LocalDateTime.now());
    }
}
