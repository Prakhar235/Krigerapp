package in.kriger.newkrigercampus.classes;


/**
 * Created by poojanrathod on 12/31/17.
 */

public class UserDetails  {
    String name, headline, imageurl;
    String uid;

    public UserDetails(String name, String headline, String imageurl) {
        this.name = name;
        this.headline = headline;
        this.imageurl = imageurl;
    }


    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    Boolean selected;

    public UserDetails() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }




}
