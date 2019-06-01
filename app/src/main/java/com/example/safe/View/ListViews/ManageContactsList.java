package com.example.safe.View.ListViews;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.safe.Database.DbSingleton;
import com.example.safe.Model.Contact;
import com.example.safe.Model.ContactList;
import com.example.safe.R;
import com.example.safe.View.Activities.ManageContactsActivity;
import com.example.safe.View.Activities.StartActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class ManageContactsList extends ArrayAdapter<Contact> {
    private final Activity context;
    private final boolean choose;
    private Set<CheckBox> checkBoxes;


    public ManageContactsList(Activity context,
                              int resource,
                              ContactList contacts,
                              boolean choose) {
        super(context, resource, contacts);
        this.context = context;
        this.choose = choose;
        checkBoxes = new HashSet<>();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item_edit, null,true);

        String fullMessage = getItem(position).getMessage();
        //String shortMessage = fullMessage.substring(0, Integer.min(fullMessage.length(), 50));
        String shortMessage = fullMessage;

        ((TextView)rowView.findViewById(R.id.name)).setText(getItem(position).getName());
        ((TextView)rowView.findViewById(R.id.message)).setText(shortMessage);
        ((ImageView)rowView.findViewById(R.id.imageView)).setImageResource(R.mipmap.ic_launcher);

        if(choose) {
            rowView.findViewById(R.id.deleteButton).setVisibility(View.INVISIBLE);
            rowView.findViewById(R.id.editButton).setVisibility(View.INVISIBLE);
            CheckBox box = rowView.findViewById(R.id.checkBox);
            box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((CheckBox)v).isChecked())
                        ((StartActivity)context).checkBox(position);
                    else
                        ((StartActivity)context).uncheckBox(position);
                }
            });
            checkBoxes.add(box);
        }
        else {
            rowView.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog(position);
                }
            });
            rowView.findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ManageContactsActivity) v.getContext()).editListElement(position);
                }
            });
            rowView.findViewById(R.id.checkBox).setVisibility(View.INVISIBLE);
        }

        return rowView;
    }

    private void showDeleteDialog(final int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Contact toRemove = getItem(position);
                        ((ManageContactsActivity)context).remove(toRemove);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void uncheckBoxes() {
        for(CheckBox box : checkBoxes)
            box.setChecked(false);
    }



}