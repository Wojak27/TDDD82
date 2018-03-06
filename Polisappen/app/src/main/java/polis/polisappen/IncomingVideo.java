package polis.polisappen;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;

/**
 * Handles the setup of a video call, that a remote user
 * has requested and this user has answered.
 */
public class IncomingVideo extends VideoAndVoiceChat {
    private String callId;
    private String caller;
    private Call call;
    LinearLayout videoWindow;
    View remoteView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoscreen);
        Bundle bundle = getIntent().getExtras();
        callId = bundle.getString("CALL_ID");
        call = mSinchClient.getCallClient().getCall(callId);
        AudioController audioController = mSinchClient.getAudioController();
        audioController.enableSpeaker();
        call.addCallListener(new SinchVideoListener());
        caller = call.getRemoteUserId();
        Button endCall = (Button) findViewById(R.id.endvideocall);
        endCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call.hangup();
            }
        });
    }

    /**
     * Listens to changes of an incoming video call and handles what
     * should happen when a video track is added or the call is ended.
     */
    private class SinchVideoListener implements VideoCallListener {
        TextView progress = (TextView) findViewById(R.id.progressing);
        @Override
        public void onVideoTrackAdded(Call call) {
            videoWindow = (LinearLayout) findViewById(R.id.remoteView);
            VideoController videoController = mSinchClient.getVideoController();
            videoController.setCaptureDevicePosition(android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK);
            remoteView = videoController.getRemoteView();
            videoWindow.addView(remoteView);
        }

        @Override
        public void onVideoTrackPaused(Call call) {

        }

        @Override
        public void onVideoTrackResumed(Call call) {

        }

        @Override
        public void onCallProgressing(Call call) {

        }

        @Override
        public void onCallEstablished(final Call call) {
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            progress.setText("In video call with "+caller);
            Button endVideoCall = (Button) findViewById(R.id.endvideocall);
            endVideoCall.setText("End video call");
            endVideoCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call.hangup();
                }
            });
        }

        @Override
        public void onCallEnded(Call call) {
            if (remoteView != null)
                videoWindow.removeView(remoteView);
            finish();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }
}
