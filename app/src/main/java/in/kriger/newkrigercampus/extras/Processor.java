package in.kriger.newkrigercampus.extras;

import java.util.ArrayList;

/**
 * Created by poojanrathod on 5/23/17.
 */

public class Processor {
    ArrayList<String> myarraylist = new ArrayList<>();

    public Processor() {
    }

    public ArrayList<String> getarraylist(String input) {

        String[] parts = input.split("#");
        myarraylist.clear();


        for (int i = 0; i < parts.length ; i++ ){

            myarraylist.add(parts[i]);


        }
        myarraylist.remove(0);

        return myarraylist;


    }

    public String getstringlist(ArrayList<String> arrayList) {
        String taglist = new String();
        for (int i = 0; i < arrayList.size(); i++) {
            taglist = taglist + "#" + arrayList.get(i);

        }

        return taglist;
    }

    public static String timestamp(String timestamp) {
        String temp = null;
        String date = timestamp.substring(6, 8);
        String month = null;
        String am = "AM";
        String hour = timestamp.substring(8,10);
        if (timestamp.substring(4, 6).equals("01")) {
            month = "Jan";

        } else if (timestamp.substring(4, 6).equals("02")) {
            month = "Feb";

        } else if (timestamp.substring(4, 6).equals("03")) {
            month = "Mar";

        } else if (timestamp.substring(4, 6).equals("04")) {
            month = "Apr";

        } else if (timestamp.substring(4, 6).equals("05")) {
            month = "May";

        } else if (timestamp.substring(4, 6).equals("06")) {
            month = "Jun";

        } else if (timestamp.substring(4, 6).equals("07")) {
            month = "Jul";

        } else if (timestamp.substring(4, 6).equals("08")) {
            month = "Aug";

        } else if (timestamp.substring(4, 6).equals("09")) {
            month = "Sep";

        } else if (timestamp.substring(4, 6).equals("10")) {
            month = "Oct";

        } else if (timestamp.substring(4, 6).equals("11")) {
            month = "Nov";

        } else if (timestamp.substring(4, 6).equals("12")) {
            month = "Dec";

        }

        if (Integer.valueOf(timestamp.substring(8,10)) >12){
            am = "PM";
            hour = String.valueOf(Integer.valueOf(hour) - 12);

        }


        return month + " " + date + " at " + hour+":"+timestamp.substring(10,12) + " " + am;

    }

    public static String paymentTimestamp(String timestamp) {
        String temp = null;
        String date = timestamp.substring(6, 8);
        String month = null;
        String am = "AM";
        String hour = timestamp.substring(8,10);
        if (timestamp.substring(4, 6).equals("01")) {
            month = "Jan";

        } else if (timestamp.substring(4, 6).equals("02")) {
            month = "Feb";

        } else if (timestamp.substring(4, 6).equals("03")) {
            month = "Mar";

        } else if (timestamp.substring(4, 6).equals("04")) {
            month = "Apr";

        } else if (timestamp.substring(4, 6).equals("05")) {
            month = "May";

        } else if (timestamp.substring(4, 6).equals("06")) {
            month = "Jun";

        } else if (timestamp.substring(4, 6).equals("07")) {
            month = "Jul";

        } else if (timestamp.substring(4, 6).equals("08")) {
            month = "Aug";

        } else if (timestamp.substring(4, 6).equals("09")) {
            month = "Sep";

        } else if (timestamp.substring(4, 6).equals("10")) {
            month = "Oct";

        } else if (timestamp.substring(4, 6).equals("11")) {
            month = "Nov";

        } else if (timestamp.substring(4, 6).equals("12")) {
            month = "Dec";

        }

        if (Integer.valueOf(timestamp.substring(8,10)) >12){
            am = "PM";
            hour = String.valueOf(Integer.valueOf(hour) - 12);

        }


        return date + " " + month + " at " + hour+":"+timestamp.substring(10,12) + " " + am;

    }

}

