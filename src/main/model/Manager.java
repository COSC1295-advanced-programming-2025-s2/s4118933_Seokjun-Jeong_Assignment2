package main.model;

public class Manager extends Staff {
    public Manager(String id, String name, String contact, String password) {
        super(id, name, contact, password, Role.MANAGER);
    }
}

