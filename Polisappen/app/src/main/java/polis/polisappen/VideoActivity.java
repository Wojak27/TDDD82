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
 * Handles making a video call, processing it and ending it.
 */
public class VideoActivity extends VideoAndVoiceChat {
    private Call call;
    private String callId;
    private String recipient;
    private LinearLayout videoWindow;
    private View remoteView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoscreen);
        Bundle bundle = getIntent().getExtras();
        callId = bundle.getString("CALL_ID");
        call=mSinchClient.getCallClient().getCall(callId);
        recipient = call.getRemoteUserId();
        call.addCallListener(new SinchVideoListener());
        AudioController audioController = mSinchClient.getAudioController();
        audioController.enableSpeaker();
        Button endVideoCall = (Button) findViewById(R.id.endvideocall);
        endVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call.hangup();
            }
        });

    }

    /**
     * Handles making a video call, setting up the stream and ending it.
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
            progress.setText("Calling "+recipient);

        }

        @Override
        public void onCallEstablished(final Call call) {
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

            progress.setText("In video call with "+recipient);
            Button endVideoCall = (Button) findViewById(R.id.endvideocall);
            endVideoCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call.hangup();

                }
            });
        }

        @Override
        public void onCallEnded(Call call) {
            if(remoteView !=null)
                videoWindow.removeView(remoteView);
            finish();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }
}
