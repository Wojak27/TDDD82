package polis.polisappen;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by linnea on 2018-02-07.
 */

public class VideoAndVoiceChat extends AppCompatActivity {
    private static final String APP_KEY = "955e2079-38f0-43d5-af9e-80e4f3ade26d";
    private static final String APP_SECRET = "TZOvC9lH6k2wmJzWHEXh2Q==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";
    private SinchClient mSinchClient;
    private Call call;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);
        init("a");
        final Toast toast = Toast.makeText(VideoAndVoiceChat.this, "hej", Toast.LENGTH_LONG);
        final Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            //@Override
            CallClient callClient = mSinchClient.getCallClient();
            //Call call = callClient.callUser("call-recipient-id");
            //sinchClient.getCallClient().callUser("call-recipient-id");

            public void onClick(View view) {

                if (call == null){
                    toast.show();
                    call=mSinchClient.getCallClient().callUser("erik");


                    call.addCallListener(new SinchCallListener());
                    button.setText("Hang up");
                }else {
                    call.hangup();
                    call = null;
                    button.setText("Call");
                }
            }
        });

    }
    protected void init (String username) {
        // Instantiate a SinchClient using the SinchClientBuilder.

        android.content.Context context = this.getApplicationContext();

        if (mSinchClient == null) {
            mSinchClient = Sinch.getSinchClientBuilder().context(context)
                    .applicationKey(APP_KEY)
                    .applicationSecret(APP_SECRET)
                    .environmentHost(ENVIRONMENT)
                    .userId(username)
                    .build();
        }
        mSinchClient.setSupportCalling(true);
        mSinchClient.startListeningOnActiveConnection();
        mSinchClient.start();



    }
/*
    mSinchClient.addSinchClientListener(new SinchClientListener() {
        public void onClientStarted(SinchClient client) { }
        public void onClientStopped(SinchClient client) { }
        public void onClientFailed(SinchClient client, SinchError error) { }
        public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration registrationCallback) { }
        public void onLogMessage(int level, String area, String message) { }
    });


    //mSinchClient.stopListeningOnActiveConnection();
    //mSinchClient.terminate();

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
    }*/
    private class SinchCallListener implements CallListener{
        private Button button = (Button) findViewById(R.id.button);
        @Override
        public void onCallProgressing(Call call) {
            button.setText("progressing");

        }

        @Override
        public void onCallEstablished(Call call) {

        }

        @Override
        public void onCallEnded(Call call) {

        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }

}
