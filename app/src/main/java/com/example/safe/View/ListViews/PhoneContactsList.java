package com.example.safe.View.ListViews;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safe.R;
import com.example.safe.View.Activities.ManageContactsActivity;

public class PhoneContactsList extends CursorAdapter {
    private ContentResolver cr;

    public PhoneContactsList(Context context, Cursor cursor) {
        super(context, cursor, 0);
        cr = context.getContentResolver();
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        String id = cursor.getString(
                cursor.getColumnIndex(ContactsContract.Contacts._ID));

        final String name = cursor.getString(cursor.getColumnIndex(
                ContactsContract.Contacts.DISPLAY_NAME));

        if (cursor.getInt(cursor.getColumnIndex(
                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

            Cursor pCur = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{id}, null);

            pCur.moveToNext();
            final String phoneNo = pCur.getString(pCur.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER));
            pCur.close();

            LayoutInflater inflater = LayoutInflater.from(context);
            View rowView=inflater.inflate(R.layout.list_phone_contact, null,true);
            ((TextView)rowView.findViewById(R.id.name)).setText(name);
            ((TextView)rowView.findViewById(R.id.phoneNumber)).setText(phoneNo);
            (rowView.findViewById(R.id.buttonAdd)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ManageContactsActivity)v.getContext()).addToList(name, phoneNo);
                }
            });
            return rowView;
        }
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
