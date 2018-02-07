package polis.polisappen;

import android.app.Activity;
import android.content.Context;

import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
/**
 * Created by linnea on 2018-02-07.
 */

public class VideoAndVoiceChat extends Activity {

    public void initSinch ()
    {
    // Instantiate a SinchClient using the SinchClientBuilder.
    Context context = this.getApplicationContext();
    SinchClient sinchClient = Sinch.getSinchClientBuilder().context(context)
            .applicationKey("955e2079-38f0-43d5-af9e-80e4f3ade26d")
            .applicationSecret("TZOvC9lH6k2wmJzWHEXh2Q==")
            .environmentHost("sandbox.sinch.com")
            .userId("<user id>")
            .build();
    }
}
