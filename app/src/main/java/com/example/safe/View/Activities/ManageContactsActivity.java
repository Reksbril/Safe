package com.example.safe.View.Activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.safe.Model.ContactList;
import com.example.safe.View.ListViews.ManageContactsList;
import com.example.safe.Model.Contact;
import com.example.safe.Database.DbSingleton;
import com.example.safe.R;

import java.util.ArrayList;
import java.util.List;

public class ManageContactsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_contacts);


        final ArrayAdapter<String> adapter =
                new ManageContactsList(this, R.layout.list_item_edit, new ContactList());
        ListView list = (ListView)findViewById(R.id.contactsList);
        list.setAdapter(adapter);

        Button addButton = new Button(this);
        addButton.setText(R.string.add_contact);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.add("xD");
                System.out.println("xD");
            }
        });


        list.addHeaderView(addButton);
    }

}
