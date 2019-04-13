package com.example.findmyrides;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MessageList extends ArrayAdapter<Message> {

    private int resoureId;
    private List<Message> objects;
    private Context context;


    public MessageList(Context context, int resourceId, List<Message> objects) {
        super(context, resourceId, objects);
        // TODO Auto-generated constructor stub
        this.objects=objects;
        this.context=context;
    }

    private static class ViewHolder
    {
        TextView textViewmTime;
        TextView textViewmDay;
        TextView textViewmFrom;
        TextView textViewName;
        TextView textViewDes;
        TextView textAcc;


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return objects.size();
    }

    @Override
    public Message getItem(int position) {
        // TODO Auto-generated method stub
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder = null;
        if(convertView==null)
        {
            viewHolder=new ViewHolder();
            LayoutInflater mInflater=LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.list_message, null);
            viewHolder.textViewmTime = (TextView) convertView.findViewById(R.id.messageTime);
            viewHolder.textViewmDay = (TextView) convertView.findViewById(R.id.messageDay);
            viewHolder.textViewmFrom = (TextView) convertView.findViewById(R.id.messageFrom);
            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.messageName);
            viewHolder.textViewDes = (TextView) convertView.findViewById(R.id.messageDes);
            viewHolder.textAcc = (TextView) convertView.findViewById(R.id.Acc);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Message message = objects.get(position);
        if(null!=message)
        {

            viewHolder.textViewmTime.setText("At "+message.getUserTime());
            viewHolder.textViewmDay.setText("On "+message.getUserDay());
            viewHolder.textViewmFrom.setText("From: "+message.getUserFrom());
            viewHolder.textViewName.setText(message.getUserName());
            viewHolder.textViewDes.setText("To: "+message.getUserDes());
            viewHolder.textAcc.setText(message.getAcc());

        }

        return convertView;
    }
}

