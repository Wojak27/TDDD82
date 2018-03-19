package polis.polisappen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jespercrimefighter on 3/19/18.
 */

public class MessageActivity extends AuthAppCompatActivity implements View.OnClickListener{
    private List<Message> messageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private Contact chatBuddy;
    private Button sendButton;
    private EditText msgText;
    private Button updateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view2);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            chatBuddy = new Contact(bundle.getString("buddy_name"),bundle.getString("buddy_id"));
        }
        messageAdapter = new MessageAdapter(this, messageList,chatBuddy);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(messageAdapter);
        sendButton = (Button) findViewById(R.id.send_msg_button);
        sendButton.setOnClickListener(this);
        msgText = (EditText) findViewById(R.id.send_msg_text);
        updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(this);
        getMessagesFromServer();
    }

    private void getMessagesFromServer()
    {
        RESTApiServer.getMessages(this,this,chatBuddy);

    }

    @Override
    public void onClick(View view){
        if(view.getId() == sendButton.getId()){
            sendMessageToServer();
        }
        if(view.getId() == updateButton.getId()){
            getMessagesFromServer();
            System.out.println("update");
        }
    }

    private void sendMessageToServer(){
        String msg = msgText.getText().toString();
        String reciever_id = chatBuddy.getId();
        RESTApiServer.sendChatMsg(this, this, msg,reciever_id);

    }
    //used when getting msges
    @Override
    public void notifyAboutResponseJSONArray(HashMap<String, HashMap<String, String>> response) {
        messageList.clear();
        System.out.println("kom vi hit kanske?");
        for(String key: response.keySet()){
            HashMap<String,String> message = response.get(key);
            Message messageClass = new Message(message.get("message"),message.get("timestamp"),message.get("sender_id"));
            messageList.add(messageClass);
        }
        Collections.sort(messageList);
        messageAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(messageList.size()-1);

    }
    //used when sending msges is successfully transmitted and received...
    public void notifyAboutResponse(HashMap<String,String> response){
        Toast.makeText(this,"Msg received by server",Toast.LENGTH_SHORT).show();
        getMessagesFromServer();
        msgText.setText("");
    }
}
