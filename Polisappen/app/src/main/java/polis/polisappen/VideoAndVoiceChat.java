package polis.polisappen;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;

/**
 * Created by linnea on 2018-02-07.
 */

public class VideoAndVoiceChat extends Activity {

    public void initSinch ()
    {

    // Instantiate a SinchClient using the SinchClientBuilder.
    Context context = this.getApplicationContext();
    final SinchClient sinchClient = Sinch.getSinchClientBuilder().context(context)
            .applicationKey("955e2079-38f0-43d5-af9e-80e4f3ade26d")
            .applicationSecret("TZOvC9lH6k2wmJzWHEXh2Q==")
            .environmentHost("sandbox.sinch.com")
            .userId("<user id>")
            .build();

    sinchClient.setSupportCalling(true);
    sinchClient.start();


    Button button = (Button) findViewById(R.id.button);

    button.setOnClickListener(new View.OnClickListener() {
        @Override
        CallClient callClient = sinchClient.getCallClient();
        Call call = callClient.callUser("");
        //sinchClient.getCallClient().callUser("call-recipient-id");
        public void onClick(View view) {

            // make a call!
        }
    });

    sinchClient.addSinchClientListener(new SinchClientListener() {
        public void onClientStarted(SinchClient client) { }
        public void onClientStopped(SinchClient client) { }
        public void onClientFailed(SinchClient client, SinchError error) { }
        public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration registrationCallback) { }
        public void onLogMessage(int level, String area, String message) { }
    });

    sinchClient.stopListeningOnActiveConnection();
    sinchClient.terminate();

    }
}
