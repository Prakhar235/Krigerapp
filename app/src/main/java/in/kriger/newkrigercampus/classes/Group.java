package in.kriger.newkrigercampus.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable{

    String group_name,group_about,group_tag1,group_tag2,group_tag3,group_tag4,owner;
    Long timestamp;
    String isEveryone;
    String url;




    private ArrayList<Integer> prepration_exam;

    private ArrayList<Integer> prepration_subject;

    public Group(String group_name, String group_about,String group_tag1,String group_tag2,String group_tag3,String group_tag4, Long timestamp, Boolean isAll,String url,String owner) {
        this.group_name = group_name;
        this.group_about = group_about;
        this.group_tag1 = group_tag1;
        this.group_tag2 = group_tag2;
        this.group_tag3 = group_tag3;
        this.group_tag4 = group_tag4;
        this.timestamp = timestamp;
        this.isEveryone = isEveryone;
        this.url = url;
        this.owner=owner;
    }

    public Group() {
    }

    public String getEveryone() {
        return isEveryone;
    }

    public void setEveryone(String isEveryone) {
        this.isEveryone = isEveryone;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_about() {
        return group_about;
    }

    public void setGroup_about(String group_about) {
        this.group_about = group_about;
    }

    public String getGroup_tag1() {
        return group_tag1;
    }

    public void setGroup_tag1(String group_tag1) {
        this.group_tag1 = group_tag1;
    }

    public String getGroup_tag2() {
        return group_tag2;
    }

    public void setGroup_tag2(String group_tag2) {
        this.group_tag2 = group_tag2;
    }

    public String getGroup_tag3() {
        return group_tag3;
    }

    public void setGroup_tag3(String group_tag3) {
        this.group_tag3 = group_tag3;
    }

    public String getGroup_tag4() {
        return group_tag4;
    }

    public void setGroup_tag4(String group_tag4) {
        this.group_tag4 = group_tag4;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getIsEveryone() {
        return isEveryone;
    }

    public void setIsEveryone(String isEveryone) {
        this.isEveryone = isEveryone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOwner(){return owner;}
    public void setOwner(String owner)
    {
        this.owner=owner;
    }

    public ArrayList<Integer> getPrepration_exam() {
        return prepration_exam;
    }

    public void setPrepration_exam(ArrayList<Integer> prepration_exam) {
        this.prepration_exam = prepration_exam;
    }

    public ArrayList<Integer> getPrepration_subject() {
        return prepration_subject;
    }

    public void setPrepration_subject(ArrayList<Integer> prepration_subject) {
        this.prepration_subject = prepration_subject;
    }



}

