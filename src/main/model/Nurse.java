package main.model;

public class Nurse extends Staff {
    public Nurse(String id, String name, String user, String hash){
        super(id,name,user,hash,Role.NURSE);
    }
}
