package main.model;

public class Doctor extends Staff {
    public Doctor(String id, String name, String user, String hash){
        super(id,name,user,hash,Role.DOCTOR);
    }
}
