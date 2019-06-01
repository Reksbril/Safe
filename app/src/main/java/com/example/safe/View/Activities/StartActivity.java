package com.example.safe.View.Activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.safe.Database.DbSingleton;
import com.example.safe.Model.Contact;
import com.example.safe.Model.ContactList;
import com.example.safe.View.Activities.AsyncTasks.LoadDb;
import com.example.safe.View.ListViews.ContactsList;
import com.example.safe.View.Background.CurrentActivity;
import com.example.safe.R;
import com.example.safe.View.ListViews.ManageContactsList;

import java.util.ArrayList;
import java.util.List;


public class StartActivity extends Activity {
    private final static int DEST_SELECT_CODE = 15523;
    private Button accept;
    Location destination;
    TextView addressView;
    private List<Contact> toAdd;
    ArrayAdapter<Contact> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        addressView = findViewById(R.id.displayLocation);

        findViewById(R.id.chooseLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectDestinationActivity.class);
                if(destination != null) {
                    intent.putExtra(getString(R.string.location_available), true);
                    intent.putExtra(getString(R.string.location), destination);
                    intent.putExtra(getString(R.string.address), addressView.getText());
                } else
                    intent.putExtra(getString(R.string.location_available), false);
                startActivityForResult(intent, DEST_SELECT_CODE);
            }
        });

        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CurrentActivity.class);
                startService(intent);
            }
        });

        findViewById(R.id.addContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChoosingContacts();
            }
        });

        LinearLayout footer = new LinearLayout(this);
        footer.setOrientation(LinearLayout.HORIZONTAL);
        accept = new Button(this);
        accept.setVisibility(View.GONE);


        Button cancel = new Button(this);
        cancel.setText(R.string.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeChoosingContacts();
                toAdd.clear();
            }
        });

        toAdd = new ArrayList<>();

        footer.addView(cancel);
        footer.addView(accept);

        ((ListView)findViewById(R.id.chooseContacts)).addFooterView(footer);

        final ContactsList contactsAdapter = new
                ContactsList(this, new ArrayList<Contact>());
        ListView list = findViewById(R.id.contactList);
        list.setAdapter(contactsAdapter);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactsAdapter.addAll(toAdd);
                toAdd.clear();
                closeChoosingContacts();
            }
        });


        new LoadDb(new DbSingleton(this).database.contactDao(), true) {

            @Override
            protected void onPostExecute(ArrayAdapter<Contact> result) {
                ListView list = findViewById(R.id.chooseContacts);
                list.setAdapter(result);
                adapter = result;
            }
        }.execute(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if(data.getExtras() != null) {
                destination = (Location)data.getExtras().get(getString(R.string.location));
                CharSequence address = (CharSequence)data.getExtras().get(getString(R.string.address));
                addressView.setText(address);
            }
        }
    }

    private void openChoosingContacts() {
        findViewById(R.id.chooseContactsView).setVisibility(View.VISIBLE);
    }

    private void closeChoosingContacts() {
        findViewById(R.id.chooseContactsView).setVisibility(View.INVISIBLE);
        ((ManageContactsList)adapter).uncheckBoxes();
        accept.setVisibility(View.INVISIBLE);
    }

    public void checkBox(int position) {
        if(toAdd.size() == 0)
            accept.setVisibility(View.VISIBLE);
        toAdd.add(adapter.getItem(position));
        accept.setText("Confirm (" + toAdd.size() + ")");
    }

    public void uncheckBox(int position) {
        toAdd.remove(adapter.getItem(position));
        if(toAdd.size() == 0)
            accept.setVisibility(View.INVISIBLE);
        accept.setText("Confirm (" + toAdd.size() + ")");
    }
}
