package com.example.safe.View.ListViews;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safe.Model.Contact;
import com.example.safe.Model.ContactBasic;
import com.example.safe.R;
import com.example.safe.View.Activities.ManageContactsActivity;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class PhoneContactsList extends ArrayAdapter<Contact> {
    private final Activity context;


    public PhoneContactsList(Activity context, int resource, ArrayList<Contact> list) {
        super(context, resource, list);
        this.context = context;
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.list_phone_contact, null,true);

            final Contact contact = getItem(position);
            if(contact != null) {
                ((TextView) rowView.findViewById(R.id.name)).setText(contact.getName());
                ((TextView) rowView.findViewById(R.id.phoneNumber)).setText(contact.getNumber());

                if(contact.getImage().length > 0) {
                    ((ImageView) rowView.findViewById(R.id.imageView)).setImageBitmap(
                            Contact.decodeImage(contact.getImage()));
                }
                else
                    ((ImageView) rowView.findViewById(R.id.imageView)).setImageResource(R.mipmap.ic_launcher);


                (rowView.findViewById(R.id.buttonAdd)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ManageContactsActivity) v.getContext())
                                .addToList(contact.getName(),
                                        contact.getNumber(),
                                        "",
                                        contact.getImage());
                    }
                });
            }
            return rowView;

    }
}
