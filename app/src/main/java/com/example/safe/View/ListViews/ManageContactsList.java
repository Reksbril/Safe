package com.example.safe.View.ListViews;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.safe.Model.Contact;
import com.example.safe.Model.ContactList;
import com.example.safe.R;
import com.example.safe.View.Activities.ManageContactsActivity;
import com.example.safe.View.Activities.StartActivity.StartActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageContactsList extends ArrayAdapter<Contact> {
    private final Activity context;
    private Map<Integer, Boolean> checkBoxes;


    public ManageContactsList(Activity context,
                              int resource,
                              ContactList contacts) {
        super(context, resource, contacts);
        this.context = context;
        checkBoxes = new HashMap<>();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.list_item_edit, null, true);

        final Contact contact = getItem(position);

        TextView name = rowView.findViewById(R.id.name);
        final TextView message = rowView.findViewById(R.id.message);

        name.setText(contact.getName());
        message.setText(contact.getMessage());

        byte[] img = contact.getImage();
        if (img.length > 0) {
            ((ImageView) rowView.findViewById(R.id.imageView)).setImageBitmap(
                    Contact.decodeImage(img));
        } else {
            ((ImageView) rowView.findViewById(R.id.imageView)).setImageResource(R.mipmap.ic_launcher);
        }

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


        return rowView;
    }

}