package polis.polisappen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallDetails;
import com.sinch.android.rtc.calling.CallListener;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;

/**
 * For making voice or video calls between users.
 */
public class VideoAndVoiceChat extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private static final String APP_KEY = "955e2079-38f0-43d5-af9e-80e4f3ade26d";
    private static final String APP_SECRET = "TZOvC9lH6k2wmJzWHEXh2Q==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";
    public static SinchClient mSinchClient;
    private Call call;
    private Call incomingCall;
    private final int RECORD_PERMISSION_CODE = 1;
    private final int READ_STATE_PERMISSION_CODE = 2;
    private final int OPEN_CAMERA_CODE = 3;
    private String userName="me";
    private String recipient="recipient";
    private boolean loggedIn = false;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        setContentView(R.layout.call);
        final EditText myName = (EditText) findViewById(R.id.myName);
        final EditText remoteName = (EditText) findViewById(R.id.remoteName);
        final Button login = (Button) findViewById(R.id.login);
        final Button voiceButton = (Button) findViewById(R.id.voiceButton);
        final Button videoButton = (Button) findViewById(R.id.videoButton);
        voiceButton.setVisibility(View.INVISIBLE);
        videoButton.setVisibility(View.INVISIBLE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!loggedIn) {
                    userName = myName.getText().toString();
                    recipient = remoteName.getText().toString();
                    init(userName);
                    loggedIn = true;
                    login.setText("Log out");
                    voiceButton.setVisibility(View.VISIBLE);
                    videoButton.setVisibility(View.VISIBLE);
                }else{
                    loggedIn = false;
                    login.setText("Log in");
                    voiceButton.setVisibility(View.INVISIBLE);
                    videoButton.setVisibility(View.INVISIBLE);
                    mSinchClient.stopListeningOnActiveConnection();
                    mSinchClient.terminate();
                    mSinchClient = null;
                }
            }
        });

        voiceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                call = mSinchClient.getCallClient().callUser(recipient);
                Intent intent = new Intent(VideoAndVoiceChat.this, CallActivity.class);
                intent.putExtra("CALL_ID",call.getCallId());
                startActivity(intent);
            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call = mSinchClient.getCallClient().callUserVideo(recipient);
                Intent intent = new Intent(VideoAndVoiceChat.this, VideoActivity.class);
                intent.putExtra("CALL_ID", call.getCallId());
                startActivity(intent);
            }
        });
    }

    /**
     * Initializes the sinchClient.
     * @param userName The name of the logged in user
     */
    protected void init (String userName) {
        android.content.Context context = this.getApplicationContext();
        if (mSinchClient == null) {
            mSinchClient = Sinch.getSinchClientBuilder().context(context)
                    .applicationKey(APP_KEY)
                    .applicationSecret(APP_SECRET)
                    .environmentHost(ENVIRONMENT)
                    .userId(userName)
                    .build();
        }
        mSinchClient.setSupportCalling(true);
        mSinchClient.startListeningOnActiveConnection();
        mSinchClient.start();
        mSinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());


    }

    public void requestPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            //if recording audio is not permitted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_PERMISSION_CODE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            //if reading phone state is not permitted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_STATE_PERMISSION_CODE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //if using the camera is not permitted
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, OPEN_CAMERA_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast requestResults = Toast.makeText(this, "Permission to record audio granted", Toast.LENGTH_LONG);
        if(requestCode == RECORD_PERMISSION_CODE){
            requestResults.show();
        }
        if (requestCode == READ_STATE_PERMISSION_CODE){
            requestResults.setText("Permission to read state granted");
            requestResults.show();
        }
        if (requestCode == OPEN_CAMERA_CODE){
            requestResults.setText("Permission to camera granted");
            requestResults.show();
        }
    }

    /**
     * Handles incoming calls.
     */
    private class SinchCallClientListener implements CallClientListener{
        @Override
        public void onIncomingCall(CallClient callClient, Call incCall) {
            incomingCall = incCall;
            CallDetails cd = incomingCall.getDetails();
            boolean isVideo = cd.isVideoOffered();
            Intent intent = new Intent (VideoAndVoiceChat.this, IncomingCall.class);
            intent.putExtra("INC_CALLID", incomingCall.getCallId());
            intent.putExtra("IS_VIDEO", isVideo);
            startActivity(intent);
        }
    }

}
