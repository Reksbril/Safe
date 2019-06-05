package com.example.safe.View.ListViews;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class ManageContactsList extends ArrayAdapter<Contact> {
    private final Activity context;
    private final boolean choose;
    private Map<Integer, Boolean> checkBoxes;


    public ManageContactsList(Activity context,
                              int resource,
                              ContactList contacts,
                              boolean choose) {
        super(context, resource, contacts);
        this.context = context;
        this.choose = choose;
        checkBoxes = new HashMap<>();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item_edit, null,true);

        Contact contact = getItem(position);

        ((TextView)rowView.findViewById(R.id.name)).setText(contact.getName());
        ((TextView)rowView.findViewById(R.id.message)).setText(contact.getMessage());

        byte[] img = contact.getImage();
        if(img.length > 0) {
            ((ImageView)rowView.findViewById(R.id.imageView)).setImageBitmap(
                    Contact.decodeImage(img));
        } else {
            ((ImageView)rowView.findViewById(R.id.imageView)).setImageResource(R.mipmap.ic_launcher);
        }

        if(choose) {
            rowView.findViewById(R.id.deleteButton).setVisibility(View.INVISIBLE);
            rowView.findViewById(R.id.editButton).setVisibility(View.INVISIBLE);
            CheckBox box = rowView.findViewById(R.id.checkBox);
            if(((StartActivity)context).isAdded(position)) {
                rowView.setForeground(
                        new ColorDrawable(context.getResources().getColor(R.color.grey, null)));
                box.setClickable(false);
            } else {
                box.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(((CheckBox)v).isChecked())
                            ((StartActivity) context).checkBox(position);
                        else
                            ((StartActivity) context).uncheckBox(position);
                        checkBoxes.put(position, ((CheckBox)v).isChecked());
                    }
                });
            }
            Boolean oldBox = checkBoxes.get(position);
            if(oldBox != null) {
                if(oldBox)
                    box.setChecked(true);
            } else
                checkBoxes.put(position, false);
        }
        else {
            rowView.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ManageContactsActivity) v.getContext()).showDeleteDialog(position);
                }
            });
            rowView.findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ManageContactsActivity) v.getContext()).editListElement(
                            position,
                            getItem(position).getMessage());
                }
            });
            rowView.findViewById(R.id.checkBox).setVisibility(View.INVISIBLE);
        }

        return rowView;
    }

    public void uncheckBoxes() {
        checkBoxes.clear();
    }

    public void checkBoxes(List<Integer> indices) {
        for(int ind : indices) {
            checkBoxes.put(ind, true);
            ((StartActivity) context).checkBox(ind);
        }
    }
}