package main.model;

import java.io.Serializable;

public abstract class Staff implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private String name;
    private String username;
    private String passwordHash;
    private final Role role;

    protected Staff(String id, String name, String username, String passwordHash, Role role){
        this.id=id; this.name=name; this.username=username; this.passwordHash=passwordHash; this.role=role;
    }
    public String getId(){ return id; }
    public String getName(){ return name; }
    public String getUsername(){ return username; }
    public Role getRole(){ return role; }
    public void setPasswordHash(String hash){ this.passwordHash = hash; }
}
