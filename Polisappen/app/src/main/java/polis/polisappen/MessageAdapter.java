package polis.polisappen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;




    public class MessageAdapter extends RecyclerView.Adapter<polis.polisappen.MessageAdapter.ViewHolder> {

        private List<Message> messageList;
        private Context context;
        private Contact chatBuddy;

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView text, timeStamp;
            public TextView sender;
            public ViewHolder(View messageView) {
                super(messageView);
                messageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                text = (TextView) messageView.findViewById(R.id.message);
                timeStamp = (TextView) messageView.findViewById(R.id.timeStamp);
                sender = (TextView) messageView.findViewById(R.id.senderName);
            }


        }



        public MessageAdapter(Context context, List<Message> messageList, Contact chatBuddy) {
            this.messageList = messageList;
            this.context =  context;
            this.chatBuddy = chatBuddy;
        }

        @Override
        public polis.polisappen.MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_list_row_layout, parent, false);

            return new polis.polisappen.MessageAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MessageAdapter.ViewHolder holder, int position) {
            final Message message = messageList.get(position);
            MessageActivity activity = (MessageActivity) context;
            if(message.getSender().equals(activity.getUserID())){
                //då tillhör meddelandet oss själva..
                holder.sender.setText(activity.getUsername());
            }
            else{
                holder.sender.setText(chatBuddy.getName());
            }
            holder.timeStamp.setText(message.getTimeStamp());
            holder.text.setText(message.getText());



        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }


    }



