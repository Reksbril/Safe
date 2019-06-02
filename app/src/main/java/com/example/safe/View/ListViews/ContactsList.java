package com.example.safe.View.ListViews;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.safe.Model.Contact;
import com.example.safe.R;

import java.util.ArrayList;

public class ContactsList extends ArrayAdapter<Contact>{
    //todo dodać więcej pól
    private final Activity context;
    public ContactsList(Activity context, ArrayList<Contact> contacts) {
        super(context, R.layout.list_item, contacts);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item, null,true);

        ImageView img = rowView.findViewById(R.id.imageView);
        img.setImageResource(R.drawable.common_google_signin_btn_icon_dark_normal_background);

        TextView textView = rowView.findViewById(R.id.name);
        textView.setText(getItem(position).getName());

        return rowView;
    }
}