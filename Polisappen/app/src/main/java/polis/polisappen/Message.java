package polis.polisappen;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jespercrimefighter on 3/19/18.
 */

public class Message implements Comparable<Message>{
    public Message(String text, String timeStamp, String sender){
        this.sender = sender;
        this.text = text;
        this.timeStamp = timeStamp;
    }

    private String text, timeStamp;
    private String sender;

    public String getText() {
        return text;
    }

    public String getTimeStamp() {
        return timeStamp;
    }


    public String getSender() {
        return sender;
    }


    @Override
    public int compareTo(@NonNull Message message) {
        String dateString = message.getTimeStamp();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        try {
            Date otherMessageDate = format.parse(dateString);
            Date thisMessageDate = format.parse(getTimeStamp());
            return thisMessageDate.compareTo(otherMessageDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
