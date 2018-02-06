package polis.polisappen;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final AccountManager Auth = new AccountManager();
        NfcManager NFCManager = (NfcManager)this.getSystemService(NFC_SERVICE);
        NfcAdapter NFCAdapter = NFCManager.getDefaultAdapter();
        if(NFCAdapter == null){
            Toast.makeText(this, "Den här enheten har inte NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if(!NFCAdapter.isEnabled()){
            Toast.makeText(this, "Du måste aktivera NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if(!Auth.isLoggedIn()){
           setContentView(R.layout.login_screen);
        }
        handleNFC(getIntent());
    }

    private void handleNFC(Intent intent){
        
    }

    //Daniel was here
    //Robin was here
    // Jesper was here
    // På Auth branchen
}
