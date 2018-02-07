package polis.polisappen;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static AccountManager AUTHORIZATION;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NfcManager NFCManager = (NfcManager)this.getSystemService(NFC_SERVICE);
        AUTHORIZATION = new AccountManager(NFCManager,this);

        if(!AUTHORIZATION.isLoggedIn()){
           setContentView(R.layout.login_screen);
            AUTHORIZATION.getAuthToken();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AUTHORIZATION.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        AUTHORIZATION.onNewIntent(intent);
    }




    //Daniel was here
    //Robin was here
    // Jesper was here
    // På Auth branchen
}
