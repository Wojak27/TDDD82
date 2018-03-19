package polis.polisappen;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
        getMessagesFromServer();
    }

    private void getMessagesFromServer()
    {
        RESTApiServer.getMessages(this,this,chatBuddy);
    }

    @Override
    public void onClick(View view){

    }

    @Override
    public void notifyAboutResponseJSONArray(HashMap<String, HashMap<String, String>> response) {
        System.out.println("kom vi hit kanske?");
        for(String key: response.keySet()){
            HashMap<String,String> message = response.get(key);
            Message messageClass = new Message(message.get("message"),message.get("timestamp"),message.get("sender_id"));
            messageList.add(messageClass);
        }
        Collections.sort(messageList);
        messageAdapter.notifyDataSetChanged();
    }
}
