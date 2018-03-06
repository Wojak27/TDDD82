package polis.polisappen;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

/**
 * Handles the process setting up a voice call.
 * Only used when the user is the one making the call.
 */
public class CallActivity extends VideoAndVoiceChat{
    private Call call;
    private String recipient;
    private Button endCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callscreen);

        endCall = (Button) findViewById(R.id.endcall);
        endCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call.hangup();
            }
        });
        Bundle bundle = getIntent().getExtras();
        String callId = bundle.getString("CALL_ID");
        call = mSinchClient.getCallClient().getCall(callId);
        call.addCallListener(new SinchCallListener());
        recipient = call.getRemoteUserId();
    }

    /**
     * Handles making a voice call, processing it and ending it.
     */
    private class SinchCallListener implements CallListener {
        TextView progress = (TextView) findViewById(R.id.progress);

        @Override
        public void onCallProgressing(Call call) {
            progress.setText("Calling "+recipient+"...");
            endCall.setText("Cancel call");

        }

        @Override
        public void onCallEstablished(Call call) {
            progress.setText("In call with "+recipient);
            endCall.setText("End call");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallEnded(Call call) {
            finish();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {
        }
    }


}
