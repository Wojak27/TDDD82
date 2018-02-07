package polis.polisappen;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;


public class AccountManager {
    private NfcAdapter adapter;
    private Context context;
    private PendingIntent pendingIntent;
    private Context context1;
    private Context context2;

    public AccountManager(){

    }

    public AccountManager(NfcManager manager, Context context){
        
        adapter = manager.getDefaultAdapter();
        this.context = context;
        if(adapter == null){
            Toast.makeText(context, "Den här enheten har inte NFC", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!adapter.isEnabled()){ //Funkade inte
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
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action))
            handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String info = "No data";

        if(tag != null) {
            byte [] byteID = tag.getId();
            int i, j, in;
            String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
            info = "";

            for(j = 0 ; j < byteID.length ; ++j)
            {
                in = (int) byteID[j] & 0xff;
                i = (in >> 4) & 0x0f;
                info += hex[i];
                i = in & 0x0f;
                info += hex[i];
            }
        }

        Toast.makeText(context, info, Toast.LENGTH_LONG).show();
        TextView textview = (TextView)((Activity)context).findViewById(R.id.LogInTextView);
        textview.setText("NFC-card scanned, write password");
        PasswordListener();
    }

    public boolean isLoggedIn(){
        return false;
    }

    private void PasswordListener(){
        EditText passswordEditText = (EditText) ((Activity)context).findViewById(R.id.passwordEditText);
        passswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //if text is 4, sätt knappen till available
                Button LogInButton = (Button)((Activity)context).findViewById(R.id.logInButton);
                if(charSequence.length()==4){
                    LogInButton.setEnabled(true);
                }
                else{
                    LogInButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


}
