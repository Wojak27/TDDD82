package polis.polisappen;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactsActivity extends AuthAppCompatActivity implements View.OnClickListener{
    private List<Contact> contactList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        contactAdapter = new ContactAdapter(this,contactList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(contactAdapter);
        getContactsFromServer();
    }

    private void getContactsFromServer() {
        RESTApiServer.getContacts(this,this);
    }

    @Override
    public void onClick(View view){

    }
    @Override
    public void onResume(){
        super.onResume();
            getContactsFromServer();

    }

    @Override
    public void notifyAboutResponseJSONArray(HashMap<String, HashMap<String, String>> response) {
        if(!contactList.isEmpty()){
            return;
        }
        for(String key: response.keySet()){
            HashMap<String,String> contact = response.get(key);
            Contact con = new Contact(contact.get("name"),contact.get("id"));
            contactList.add(con);
        }
        contactAdapter.notifyDataSetChanged();
    }
}
