package com.example.safe.View.ListViews;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.safe.Database.DbSingleton;
import com.example.safe.Model.Contact;
import com.example.safe.Model.ContactList;
import com.example.safe.R;

import java.util.ArrayList;

public class ManageContactsList extends ArrayAdapter<String> {
    private final Activity context;
    private ContactList contacts;


    public ManageContactsList(Activity context,
                              int resource,
                              ContactList contacts) {
        super(context, resource, contacts.getNumbers());
        this.contacts = contacts;
        this.context = context;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item_edit, null,true);

        String shortMessage = contacts.get(position).substring(0, 50);

        ((TextView)rowView.findViewById(R.id.name)).setText(name.get(position));
        ((TextView)rowView.findViewById(R.id.message)).setText(shortMessage);
        ((ImageView)rowView.findViewById(R.id.imageView)).setImageResource(R.mipmap.ic_launcher);

        rowView.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(name.get(position));
            }
        });

        return rowView;
    }
}