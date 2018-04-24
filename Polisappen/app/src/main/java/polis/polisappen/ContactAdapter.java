package polis.polisappen;

/**
 * Created by jespercrimefighter on 3/13/18.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<Contact> contactList;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name, id;
        public TextView optionsButton;
        public ViewHolder(View contactView) {
            super(contactView);
            contactView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            name = (TextView) contactView.findViewById(R.id.name);
            id = (TextView) contactView.findViewById(R.id.id);
            optionsButton = (TextView) contactView.findViewById(R.id.optionsbutton);
        }


    }



    public ContactAdapter(Context context, List<Contact> contactList) {
        this.contactList = contactList;
        this.context =  context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_row_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Contact contact = contactList.get(position);
        holder.name.setText(contact.getName());
        holder.id.setText(contact.getId());
        holder.optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, holder.optionsButton);
                popup.inflate(R.menu.contact_options_menu_layout);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.call:
                                Bundle bundle = new Bundle();
                                bundle.putString("calling_to_name",contact.getName());
                                bundle.putString("calling_to_id",contact.getId());
                                ContactsActivity act = (ContactsActivity) context;
                                bundle.putString("my_name",act.getUsername());
                                Intent intent =  new Intent(context,VideoAndVoiceChat.class);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                                break;
                            case R.id.message:
                                Bundle bundle1 = new Bundle();
                                bundle1.putString("buddy_name",contact.getName());
                                bundle1.putString("buddy_id",contact.getId());
                                Intent intent1 =  new Intent(context,MessageActivity.class);
                                intent1.putExtras(bundle1);
                                context.startActivity(intent1);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }


}

