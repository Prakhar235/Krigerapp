package in.kriger.newkrigercampus.classes;

import java.util.ArrayList;
import java.util.HashMap;

public class Resource {

    String name,description,thumb,owner,timestamp,valid_till;
    Long type;
    String rate;
    Long exam,subject,fees;
    Double class_type,end_time,fees_type,start_time;

    private ArrayList<HashMap<String,Object>> time;


    public static final String TABLE_NAME = "resource";

    public static final String COLUMN_RESID = "res_id";
    public static final String COLUMN_COUNTENQUIRY = "count_enquiries";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_RESID + " TEXT PRIMARY KEY,"
                    + COLUMN_COUNTENQUIRY + " INTEGER"
                    + ")";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }


    public void setExam(Long exam) {
        this.exam = exam;
    }

    public void setSubject(Long subject) {
        this.subject = subject;
    }

    public Long getExam() {
        return exam;
    }

    public Long getSubject() {
        return subject;
    }

    public Long getFees() {
        return fees;
    }

    public void setFees(Long fees) {
        this.fees = fees;
    }

    public Double getClass_type() {
        return class_type;
    }

    public void setClass_type(Double class_type) {
        this.class_type = class_type;
    }

    public Double getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Double end_time) {
        this.end_time = end_time;
    }

    public Double getFees_type() {
        return fees_type;
    }

    public void setFees_type(Double fees_type) {
        this.fees_type = fees_type;
    }

    public Double getStart_time() {
        return start_time;
    }

    public void setStart_time(Double start_time) {
        this.start_time = start_time;
    }

    public String getValid_till() {
        return valid_till;
    }

    public void setValid_till(String valid_till) {
        this.valid_till = valid_till;
    }

    public ArrayList<HashMap<String, Object>> getTime() {
        return time;
    }

    public void setTime(ArrayList<HashMap<String, Object>> time) {
        this.time = time;
    }
}
