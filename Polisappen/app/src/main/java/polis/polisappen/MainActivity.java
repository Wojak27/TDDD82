package polis.polisappen;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
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

        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        final Button LogInButton = (Button)findViewById(R.id.logInButton);
        LogInButton.setEnabled(false);
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==4){//&& isScanned == true
                    LogInButton.setEnabled(true);
                }
                else{
                    LogInButton.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }
/*
    @Override
    protected void onResume() {
        super.onResume();
        AUTHORIZATION.onResume();
    }
*/
    @Override
    protected void onNewIntent(Intent intent) {
        AUTHORIZATION.onNewIntent(intent);
    }




    //Daniel was here
    //Robin was here
    // Jesper was here
    // På Auth branchen
}
