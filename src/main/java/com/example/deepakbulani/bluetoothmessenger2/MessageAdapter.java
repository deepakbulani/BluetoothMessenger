package com.example.deepakbulani.bluetoothmessenger2;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Anurag Gupta on 14/04/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter <MessageAdapter.ViewHolder> {
    private Vector<HashMap<Integer,String> > mDataset;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    public  class ViewHolder extends RecyclerView.ViewHolder {
        public TextView edit1;
        public TextView edit2;
        public ViewHolder(View itemView) {
            super(itemView);
            edit1 =(TextView) itemView.findViewById(R.id.text_message_body);
            edit2 =(TextView) itemView.findViewById(R.id.text_message_time);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MessageAdapter(Vector<HashMap<Integer,String> > myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_sent, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_received, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {


        if (mDataset.elementAt(position).containsKey(1)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }


    @Override
        public void onBindViewHolder (ViewHolder holder,int position) {

            if (mDataset.elementAt(position).containsKey(1)) {
                holder.edit1.setText(mDataset.elementAt(position).get(1));
                holder.edit2.setText("11:24");
            }
            if (mDataset.elementAt(position).containsKey(2)) {
                holder.edit1.setText(mDataset.elementAt(position).get(2));
                holder.edit2.setText("11:24");
            }
        }
        @Override
        public int getItemCount () {
            return mDataset.size();
        }
    }


