package in.kriger.newkrigercampus.classes;

/**
 * Created by poojanrathod on 12/1/17.
 */

public class Contacts {

    private String name,eph;
    private Boolean isEmail;


    public Contacts(String name, String eph,Boolean isEmail) {
        this.name = name;
        this.eph = eph;
        this.isEmail = isEmail;
    }

    public Contacts() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEph() {
        return eph;
    }

    public void setEph(String eph) {
        this.eph = eph;
    }

    public Boolean getEmail() {
        return isEmail;
    }

    public void setEmail(Boolean email) {
        isEmail = email;
    }


}
