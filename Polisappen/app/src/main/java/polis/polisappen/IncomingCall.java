package polis.polisappen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;

/**
 * Handles incoming calls, no matter what type.
 */
public class IncomingCall extends VideoAndVoiceChat {

    private Call call;
    private String callType;
    private String caller;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incomingcallscreen);

        Button answer = (Button) findViewById(R.id.answer);
        Button hangup = (Button) findViewById(R.id.hangup);
        TextView incoming = (TextView) findViewById(R.id.incoming);

        Bundle bundle = getIntent().getExtras();
        String callId = bundle.getString("INC_CALLID");
        final boolean isVideo = bundle.getBoolean("IS_VIDEO");

        call = mSinchClient.getCallClient().getCall(callId);
        caller = call.getRemoteUserId();
        if (isVideo){
            callType = "video call";
        }else{
            callType = "voice call";
        }
        incoming.setText("Incoming "+callType+" from "+caller);
        if (!isVideo){
            call.addCallListener(new CallListener() {
                @Override
                public void onCallProgressing(Call call) {

                }

                @Override
                public void onCallEstablished(Call call) {

                }

                @Override
                public void onCallEnded(Call call) {
                    finish();
                }

                @Override
                public void onShouldSendPushNotification(Call call, List<PushPair> list) {

                }
            });
        }else{
            call.addCallListener(new VideoCallListener() {
                @Override
                public void onVideoTrackAdded(Call call) {

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
                public void onCallEstablished(Call call) {

                }

                @Override
                public void onCallEnded(Call call) {
                    finish();
                }

                @Override
                public void onShouldSendPushNotification(Call call, List<PushPair> list) {

                }
            });
        }
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call.answer();
                Intent intent;
                if(isVideo) {
                    intent = new Intent(IncomingCall.this, IncomingVideo.class);
                }else {
                    intent = new Intent(IncomingCall.this, IncomingVoice.class);
                }
                intent.putExtra("CALL_ID", call.getCallId());
                startActivity(intent);
                }

        });

        hangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call.hangup();
                                      }
                                  }
        );
    }

}
