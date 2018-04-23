package polis.polisappen;

import android.os.Bundle;

import java.util.HashMap;

/**
 * Created by karolwojtulewicz on 2018-04-23.
 */

public class ExceptionAuthAppCompatActivity extends AuthAppCompatActivity {
    @Override
    public void notifyAboutResponseJSONArray(HashMap<String, HashMap<String, String>> response) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedInstanceState = checkAuth(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    private Bundle checkAuth(Bundle savedInstanceState){
        if (!validAuth()){
            if(savedInstanceState == null){
                savedInstanceState = new Bundle();
                savedInstanceState.putString("CALLER","NOAUTHACTIVITY");
            }
        }else {
            savedInstanceState = null;
        }
        return savedInstanceState;
    }
}
