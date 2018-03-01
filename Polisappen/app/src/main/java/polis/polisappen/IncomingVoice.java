package polis.polisappen;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

/**
 * Handles the setup of a voice call, that a remote user
 * has requested and this user has answered.
 */
public class IncomingVoice extends VideoAndVoiceChat{
    private String callId;
    private String caller;
    private Call call;
    private Button endCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callscreen);
        Bundle bundle = getIntent().getExtras();
        callId = bundle.getString("CALL_ID");
        call = mSinchClient.getCallClient().getCall(callId);
        call.addCallListener(new SinchCallListener());
        caller = call.getRemoteUserId();

        endCall = (Button) findViewById(R.id.endcall);
        endCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call.hangup();
            }
        });
    }

    /**
     * Listens to changes of an incoming call and decides what will happen
     * when it is answered or ended.
     */
    private class SinchCallListener implements CallListener {
        TextView progress = (TextView) findViewById(R.id.progress);

        @Override
        public void onCallProgressing(Call call) {

        }

        @Override
        public void onCallEstablished(Call call) {
            progress.setText("In call with "+caller);
            endCall.setText("End call");
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
