package polis.polisappen;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;

import java.util.concurrent.TimeUnit;

/**
 * Created by linnea on 2018-02-07.
 */

public class VideoAndVoiceChat extends AppCompatActivity {
    private static final String APP_KEY = "955e2079-38f0-43d5-af9e-80e4f3ade26d";
    private static final String APP_SECRET = "TZOvC9lH6k2wmJzWHEXh2Q==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";
    private SinchClient mSinchClient;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);
        init("a");
    }
    protected void init (String username){
    // Instantiate a SinchClient using the SinchClientBuilder.

        //Button button = (Button) findViewById(R.id.button);
        //testing();
        //button.setText("här");

        android.content.Context context = this.getApplicationContext();
    if (mSinchClient == null) {
        mSinchClient = Sinch.getSinchClientBuilder().context(context)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .userId(username)
                .build();
       // mSinchClient.start();
    }
/*
    sinchClient.setSupportCalling(true);
    sinchClient.start();

    button.setOnClickListener(new View.OnClickListener() {
        //@Override
        CallClient callClient = sinchClient.getCallClient();
        Call call = callClient.callUser("call-recipient-id");
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
*/
    }

    public void testing (){
        try
        {
            Thread.sleep(5000);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }
}
