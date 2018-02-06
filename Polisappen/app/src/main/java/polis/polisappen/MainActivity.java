package polis.polisappen;

import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final AccountManager Auth = new AccountManager();
        NfcManager NFCManager = (NfcManager)this.getSystemService(NFC_SERVICE);
        NfcAdapter NFCAdapter = NFCManager.getDefaultAdapter();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!Auth.isLoggedIn()){
           setContentView(R.layout.login_screen);
        }

    }

    //Daniel was here
    //Robin was here
    // Jesper was here
    // På Auth branchen
}
