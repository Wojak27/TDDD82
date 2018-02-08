package polis.polisappen;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.editable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static AccountManager AUTHORIZATION;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "skapades typ inte en intent, dunno", Toast.LENGTH_LONG).show();
        setContentView(R.layout.menu_screen);
        logout = (Button)findViewById(R.id.logOutButton);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        Toast.makeText(this, "onClick called", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this,AccountManager.class);
        startActivity(intent);
    }
}
