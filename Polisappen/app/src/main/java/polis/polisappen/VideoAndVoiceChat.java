package polis.polisappen;

import android.Manifest;
import android.content.BroadcastReceiver;
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
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallDetails;

import java.util.HashMap;

/**
 * For making voice or video calls between users.
 */
public class VideoAndVoiceChat extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener, HttpResponseNotifyable{
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
    private Button loginButton;
    private EditText deviceNameForCall;
    private EditText remoteNameToCall;
    private Button voiceButton;
    private Button videoButton;
    private TextView noVideoTextView;

    @Override
    protected void onResume() {
        super.onResume();
        setTextView();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        setContentView(R.layout.call);
        deviceNameForCall = (EditText) findViewById(R.id.myName);
        remoteNameToCall = (EditText) findViewById(R.id.remoteName);
        loginButton = (Button) findViewById(R.id.login);
        voiceButton = (Button) findViewById(R.id.voiceButton);
        videoButton = (Button) findViewById(R.id.videoButton);
        noVideoTextView = (TextView) findViewById(R.id.no_video_textview);
        voiceButton.setVisibility(View.INVISIBLE);
        videoButton.setVisibility(View.INVISIBLE);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            remoteNameToCall.setText(bundle.getString("calling_to_name"));
        }
        loginButton.setOnClickListener(this);
        voiceButton.setOnClickListener(this);
        videoButton.setOnClickListener(this);
    }

    private void setTextView(){
        if (SystemStatus.getBatteryStatus() == SystemState.BATTERY_LOW){
            noVideoTextView.setVisibility(View.VISIBLE);
        }else {
            noVideoTextView.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

                if(mSinchClient != null) {
            try {
                mSinchClient.terminate();
                mSinchClient = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.videoButton){
            if(SystemStatus.getBatteryStatus() == SystemState.BATTERY_LOW){
                Toast.makeText(getApplicationContext(), "Batterin är för låg, ladda telefonen över 10 procent", Toast.LENGTH_SHORT).show();
                setTextView();
            }else{
                RESTApiServer.validateToken(this,this);
            }

        }else if(v.getId() == R.id.login){
            if(!loggedIn) {
                logInUser();
            }else if(loggedIn){
                logOutUser();
            }
        }else if (v.getId() == R.id.voiceButton){
            call = mSinchClient.getCallClient().callUser(recipient);
            Intent intent = new Intent(VideoAndVoiceChat.this, CallActivity.class);
            intent.putExtra("CALL_ID",call.getCallId());
            startActivity(intent);
        }
    }

    private void logInUser(){
        userName = deviceNameForCall.getText().toString();
        recipient = remoteNameToCall.getText().toString();
        if(loggedIn != true){
            init(userName);
        }
        loggedIn = true;
        switchButtonsToLogOut();
    }

    private void switchButtonsToLogOut(){
        loginButton.setText("Log out");
        voiceButton.setVisibility(View.VISIBLE);
        videoButton.setVisibility(View.VISIBLE);
    }

    private void switchButtonsToLogIn(){
        loginButton.setText("Log in");
        voiceButton.setVisibility(View.INVISIBLE);
        videoButton.setVisibility(View.INVISIBLE);
    }

    private void logOutUser(){
        loggedIn = false;
        switchButtonsToLogIn();
        mSinchClient.stopListeningOnActiveConnection();
        try{
            mSinchClient.terminate();
            mSinchClient = null;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void notifyAboutResponse(HashMap<String, String> response) {
        //nu undrar ju ni varför vi inte gör nått med response
        //men det är ju för att vi skiter i resultatet, vi ville bara att
        //det skulle loggas i serverns log!
        //String result = response.get("valid_token");
            call = mSinchClient.getCallClient().callUserVideo(recipient);
            Intent intent = new Intent(VideoAndVoiceChat.this, VideoActivity.class);
            intent.putExtra("CALL_ID", call.getCallId());
            startActivity(intent);
    }

    @Override
    public void notifyAboutResponseJSONArray(HashMap<String, HashMap<String, String>> response) {
        //not used in this class
    }

    @Override
    public void notifyAboutFailedRequest() {
        //not used in this class (yet)
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
