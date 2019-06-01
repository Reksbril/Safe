package com.example.safe.View.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safe.Database.DbSingleton;
import com.example.safe.Model.Contact;
import com.example.safe.Model.ContactList;
import com.example.safe.Model.Message;
import com.example.safe.View.Activities.AsyncTasks.LoadDb;
import com.example.safe.View.Background.Sms;
import com.example.safe.View.ListViews.ContactsList;
import com.example.safe.View.Background.CurrentActivity;
import com.example.safe.R;
import com.example.safe.View.ListViews.ManageContactsList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StartActivity extends Activity {
    private final static int DEST_SELECT_CODE = 15523;
    private Button accept;
    private Location destination;
    private TextView addressView;
    private EditText time;
    private List<Contact> toAdd;
    private ArrayAdapter<Contact> adapter;
    private ArrayList<Contact> contacts;

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

        time = findViewById(R.id.editTime);
        time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //not used
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!goodTime(s.toString()))
                    time.setTextColor(Color.RED);
                else
                    time.setTextColor(Color.BLACK);
            }
        });

        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(destination == null) {
                    //TODO potrzebny adres
                    return;
                }
                if(!goodTime(time.getText().toString())) {
                    //TODO zły czas
                    return;
                }
                if(contacts.size() == 0) {
                    //TODO za mało kontaktów
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), CurrentActivity.class);
                intent.putExtra(getString(R.string.location_data), destination);
                //duration in milliseconds
                int duration = Integer.parseInt(time.getText().toString()) * 60 * 1000;
                intent.putExtra(getString(R.string.duration), duration);

                ArrayList<Message> messages = new ArrayList<>();
                for(Contact contact : contacts)
                    messages.add(new Sms(contact.getMessage(), contact.getNumber()));
                intent.putExtra(getString(R.string.messages), messages);

                startService(intent);
                finish();

                Intent newActivity = new Intent(getApplicationContext(), OngoingActivity.class);
                startActivity(newActivity);
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

        contacts = new ArrayList<>();
        final ContactsList contactsAdapter = new
                ContactsList(this, contacts);
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

    private boolean goodTime(String time) {
        Pattern p = Pattern.compile("[0-9]|[0-9][0-9]|[0-9][0-9][0-9]|[0-9][0-9][0-9][0-9]");
        Matcher m = p.matcher(time);
        return m.matches();
    }
}
