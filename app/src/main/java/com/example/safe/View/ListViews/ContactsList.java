package com.example.safe.View.ListViews;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.safe.Model.Contact;
import com.example.safe.R;
import com.example.safe.View.Activities.StartActivity;

import org.w3c.dom.Text;

import java.net.URI;
import java.util.ArrayList;

public class ContactsList extends ArrayAdapter<Contact>{
    //todo dodać więcej pól
    private final Activity context;

    public ContactsList(Activity context, ArrayList<Contact> contacts) {
        super(context, R.layout.list_item, contacts);
        this.context = context;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final int MAX_MESSAGE_LEN = 30;

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item, null, true);


        Contact contact = getItem(position);

        TextView textView = rowView.findViewById(R.id.name);
        textView.setText(getItem(position).getName());

        String message = getItem(position).getMessage();
        ((TextView) rowView.findViewById(R.id.shortMessage)).setText(message);

        ImageView img = rowView.findViewById(R.id.imageView);
        byte[] image = contact.getImage();
        if(image.length > 0) {
            img.setImageBitmap(Contact.decodeImage(image));
        } else {
            img.setImageResource(R.mipmap.ic_launcher);
        }

        rowView.findViewById(R.id.buttonDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StartActivity) v.getContext()).deleteContact(getItem(position));
            }
        });


        return rowView;
    }
}