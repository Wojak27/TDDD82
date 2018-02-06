package polis.polisappen;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.widget.Toast;

public class AccountManager {
    private NfcAdapter adapter;
    private Context context;
    private PendingIntent pendingIntent;

    public AccountManager(){

    }

    public AccountManager(NfcManager manager, Context context){
        adapter = manager.getDefaultAdapter();
        this.context = context;
        if(adapter == null){
            Toast.makeText(context, "Den här enheten har inte NFC", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!adapter.isEnabled()){
            Toast.makeText(context, "Du måste aktivera NFC", Toast.LENGTH_SHORT).show();
            return;
        }
    }


    /* should return the auth-token, if it has not expired, then it should get a new one.*/
    public void getAuthToken(){
        pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, context.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    protected void onResume() {
        adapter.enableForegroundDispatch((Activity) context, pendingIntent, null, null);
    }

    protected void onNewIntent(Intent intent) {
        //setIntent(intent);
        resolveIntent(intent);
    }
    private void resolveIntent(Intent intent) {
        Toast.makeText(context, "WHATERVER", Toast.LENGTH_SHORT).show();
    }

    public boolean isLoggedIn(){
        return false;
    }



}
