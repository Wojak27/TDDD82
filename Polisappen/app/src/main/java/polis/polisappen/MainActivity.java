package polis.polisappen;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private NfcAdapter adapter;
    private PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final AccountManager Auth = new AccountManager();
        NfcManager NFCManager = (NfcManager)this.getSystemService(NFC_SERVICE);
        adapter = NFCManager.getDefaultAdapter();
        if(adapter == null){
            Toast.makeText(this, "Den här enheten har inte NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if(!adapter.isEnabled()){
            Toast.makeText(this, "Du måste aktivera NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if(!Auth.isLoggedIn()){
           setContentView(R.layout.login_screen);
        }
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }
    private void resolveIntent(Intent intent) {
        Toast.makeText(this, "WHATERVER", Toast.LENGTH_SHORT).show();
    }

    //Daniel was here
    //Robin was here
    // Jesper was here
    // På Auth branchen
}
