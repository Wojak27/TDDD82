package polis.polisappen;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.nfc.Tag;

public class AccountManager extends AppCompatActivity implements View.OnClickListener{
    private NfcAdapter adapter;
    private PendingIntent pendingIntent;
    private boolean isScanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        final Button LogInButton = (Button)findViewById(R.id.logInButton);
        LogInButton.setEnabled(false);

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==4 && isScanned){
                    LogInButton.setEnabled(true);
                }
                else{
                    LogInButton.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        LogInButton.setOnClickListener(this);
        NfcManager manager = (NfcManager)this.getSystemService(NFC_SERVICE);
        adapter = manager.getDefaultAdapter();
        if(adapter == null)
            Toast.makeText(this, "Den här enheten har inte NFC", Toast.LENGTH_SHORT).show();
        else if(!adapter.isEnabled()) //Funkade inte
           Toast.makeText(this, "Du måste aktivera NFC", Toast.LENGTH_SHORT).show();
    }

   @Override
    public void onClick(View view) {
        //setContentView(R.layout.menu_screen); //Måste vara byt activity
    }

    @Override
    protected void onResume() {
        super.onResume();
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        adapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adapter != null) {
            adapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action))
            handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String serial_number = "No data";

        if(tag != null) {
            byte [] byteID = tag.getId();
            int i, j, in;
            String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
            serial_number = "";

            for(j = 0 ; j < byteID.length ; ++j)
            {
                in = (int) byteID[j] & 0xff;
                i = (in >> 4) & 0x0f;
                serial_number += hex[i];
                i = in & 0x0f;
                serial_number += hex[i];
            }
        }

        Toast.makeText(this, serial_number, Toast.LENGTH_LONG).show();
        TextView textview = (TextView)(this).findViewById(R.id.logInText);
        textview.setText("NFC-card scanned, write password");

        isScanned = true;
    }

    public boolean isLoggedIn(){
        return false;
    }

}
