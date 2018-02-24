package polis.polisappen;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.preference.PreferenceManager;
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

import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class AccountManager extends AppCompatActivity implements View.OnClickListener, HttpResponseNotifyable {
    private NfcAdapter adapter;
    private PendingIntent pendingIntent;
    private boolean isScanned = false;
    private Button LogInButton;
    private EditText passwordEditText;
    private TextView textView;
    private final int AUTH_EXPIRY_TIME = 5; //In seconds
    public static String USER_AUTH_TIMESTAMP = "USER_AUTH_TIMESTAMP";
    public static String USER_AUTH_STATUS = "USER_AUTH_STATUS";
    public static String USER_AUTHENTICATED = "USER_AUTHENTICATED";
    public static String USER_NOT_AUTHENTICATED = "USER_NOT_AUTHENTICATED";
    public static String USER_AUTH_TOKEN = "USER_AUTH_TOKEN";
    private String tmpNfcCardNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        LogInButton = (Button)findViewById(R.id.logInButton);
        textView = (TextView)(this).findViewById(R.id.logInText);
        LogInButton.setEnabled(false);

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkLoginStatus();
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        LogInButton.setOnClickListener(this);
        NfcManager manager = (NfcManager)this.getSystemService(NFC_SERVICE);
        adapter = manager.getDefaultAdapter(); // throw exception?
        if(adapter == null)
            Toast.makeText(this, "Den här enheten har inte NFC", Toast.LENGTH_SHORT).show();
        else if(!adapter.isEnabled())
           Toast.makeText(this, "Du måste aktivera NFC", Toast.LENGTH_SHORT).show();
    }


   @Override
   /*This method can only be called when checkLoginStatus has already been called.*/
    public void onClick(View view) {
       String pin = passwordEditText.getText().toString();
       validateRequest(tmpNfcCardNumber,pin);
    }

    private String getAuthTokenExpiry(){
        long currentTime = System.currentTimeMillis(); //the difference, measured in milliseconds, between the current time and midnight, January 1, 1970 UTC.
        currentTime = currentTime + 1000*AUTH_EXPIRY_TIME; //
        return String.valueOf(currentTime);
    }

    private void validateRequest(String nfcCardNumber, String pin){
        try {
            JSONObject jsonParams = new JSONObject();
            //jsonParams.put("id", "robin");
            //jsonParams.put("password", "12345");
            jsonParams.put("id", nfcCardNumber);
            jsonParams.put("password", pin);
            RESTApiServer.post(this,"/login",jsonParams, RESTApiServer.getDefaultHandler(this));
            /*
            RequestParams params = new RequestParams();
            params.put("nfcID", nfcCardNumber);
            params.put("pin", pin);
            */
        }
        catch (Exception e){
            Toast.makeText(this, "Exception..", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void notifyAboutResponse(HashMap<String,String> response){
        if(responseOK(response)) { //if response was successfull....
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(AccountManager.USER_AUTH_STATUS, USER_AUTHENTICATED);
            editor.putString(AccountManager.USER_AUTH_TIMESTAMP, getAuthTokenExpiry());
            editor.putString(AccountManager.USER_AUTH_TOKEN, response.get("token"));
            System.out.println(response.get("token"));
            editor.apply();
            finish();
        }
        else{
            Toast.makeText(this, "Felaktig PIN!", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean responseOK(HashMap<String,String> response){
        //determine weither the response was OK, and if so return true or else return false.
        return response.get(USER_AUTH_STATUS).equals(USER_AUTHENTICATED);
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
            tmpNfcCardNumber = null;
            handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if(!NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){ //The app was started or some other random intent
            return;
        }
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
        if(!serial_number.equals("No data")){
            isScanned = true;
            tmpNfcCardNumber = serial_number;
        }
        checkLoginStatus();



    }

    @Override
    public void onBackPressed(){}

    public void checkLoginStatus(){//Kolla vilken info som matats in
        if(passwordEditText.getText().length()==4 && isScanned){
            LogInButton.setEnabled(true);
            textView.setText(getResources().getString(R.string.loginReady));
        }
        else if (passwordEditText.getText().length() ==4)
            textView.setText(getResources().getString(R.string.loginPasswordReady));

        else if (isScanned){
            LogInButton.setEnabled(false);
            textView.setText(getResources().getString(R.string.loginScannedCard));
        }
        else{
            LogInButton.setEnabled(false);
            textView.setText(getResources().getString(R.string.logInText));
        }
    }
}
