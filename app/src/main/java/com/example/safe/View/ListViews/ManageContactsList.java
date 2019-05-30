package com.example.safe.View.ListViews;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.safe.R;

import java.util.ArrayList;

public class ManageContactsList extends ArrayAdapter<String> {
    private ArrayList<Integer> imageId;
    private ArrayList<String> contactInfo;
    private final Activity context;


    public ManageContactsList(Activity context, int resource, ArrayList<String> contactInfo) {
        super(context, resource, contactInfo);
        this.contactInfo = contactInfo;
        this.context = context;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item_edit, null,true);

        System.out.println("????");
        ((TextView)rowView.findViewById(R.id.tekst)).setText("xddad");
        ((ImageView)rowView.findViewById(R.id.imageView)).setImageResource(R.mipmap.ic_launcher);

        rowView.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(contactInfo.get(position));
            }
        });

        return rowView;
    }
}
