package polis.polisappen;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
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
import android.util.Log;

/**
 * Created by linnea on 2018-02-07.
 */

public class VideoAndVoiceChat extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String APP_KEY = "955e2079-38f0-43d5-af9e-80e4f3ade26d";
    private static final String APP_SECRET = "TZOvC9lH6k2wmJzWHEXh2Q==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";
    private SinchClient mSinchClient;
    private Call call;
    private final int RECORD_PERMISSION_CODE = 1;
    private final int READ_STATE_PERMISSION_CODE = 2;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestAudioPermissions();
        setContentView(R.layout.call);
        init("a");
        //final Toast toast = Toast.makeText(VideoAndVoiceChat.this, "hej", Toast.LENGTH_LONG);
        final Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            //@Override
            //CallClient callClient = mSinchClient.getCallClient();
            //Call call = callClient.callUser("call-recipient-id");
            //sinchClient.getCallClient().callUser("call-recipient-id");

            public void onClick(View view) {

                if (call == null){
                   // toast.show();
                    call=mSinchClient.getCallClient().callUser("Sebson");

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
            Toast toast2 = Toast.makeText(this, "built client", Toast.LENGTH_LONG);
            toast2.show();
        }
        mSinchClient.setSupportCalling(true);

        mSinchClient.startListeningOnActiveConnection();
//        mSinchClient.checkManifest();
        mSinchClient.start();
/*
    mSinchClient.addSinchClientListener(new SinchClientListener() {
        public void onClientStarted(SinchClient client) {
            Toast toast3 = Toast.makeText(VideoAndVoiceChat.this, "started", Toast.LENGTH_LONG);
        }
        public void onClientStopped(SinchClient client) { }
        public void onClientFailed(SinchClient client, SinchError error) {

        }
        public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration registrationCallback) { }
        public void onLogMessage(int level, String area, String message) { }
    });*/


   // mSinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

    //mSinchClient.stopListeningOnActiveConnection();
    //mSinchClient.terminate();

    }
    public void requestAudioPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            //if recording audio is not permitted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_PERMISSION_CODE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            //if reading phone state is not permitted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_STATE_PERMISSION_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast toast4 = Toast.makeText(this, "Permission to record audio granted", Toast.LENGTH_LONG);
        if(requestCode == RECORD_PERMISSION_CODE){
            toast4.show();
        }
        else if (requestCode == READ_STATE_PERMISSION_CODE){
            toast4.setText("Permission to read state granted");
            toast4.show();
        }
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
