package in.kriger.newkrigercampus.classes;

import java.util.Date;

public class Message {
    String timestamp,uid,message,thumb,original;

    private long messageTime;



    public Message(String timestamp, String uid, String message,String thumb,String original) {
        this.timestamp = timestamp;
        this.uid = uid;
        this.message = message;
        this.thumb = thumb;
        this.original = original;




        // Initialize to current time
        messageTime = new Date().getTime();


    }

    public Message() {
    }


    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }



    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }
}
