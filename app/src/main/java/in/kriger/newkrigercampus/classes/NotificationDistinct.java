package in.kriger.newkrigercampus.classes;

public class NotificationDistinct {

    String position,type,origin_id,destination_id;


    public NotificationDistinct() {
    }

    public NotificationDistinct(String position, String type, String origin_id, String destination_id) {
        this.position = position;
        this.type = type;
        this.origin_id = origin_id;
        this.destination_id = destination_id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrigin_id() {
        return origin_id;
    }

    public void setOrigin_id(String origin_id) {
        this.origin_id = origin_id;
    }

    public String getDestination_id() {
        return destination_id;
    }

    public void setDestination_id(String destination_id) {
        this.destination_id = destination_id;
    }

}
