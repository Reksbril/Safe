package com.example.safe.View.Activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    private final String locationCode = "DEST_LOCATION";
    private final String addressCode = "DEST_ADDRESS";
    private final String timeCode = "TIME";
    private final String toAddCode = "TO_ADD_ARRAY";
    private final String addViewOpenedCode = "ADD_VIEW_OPENED";
    private final String contactsToAdd = "CONTACTS_TO_ADD";

    private volatile boolean phoneContactsOpened = false;

    private final static int DEST_SELECT_CODE = 15523;
    private Button accept;
    private Location destination;
    private TextView addressView;
    private EditText time;
    private ArrayAdapter<Contact> adapter;
    private ArrayList<Contact> contacts;
    private ContactsList contactsAdapter;

    //to load state
    private boolean addViewOpened = false;
    private List<Contact> toAdd;
    private ArrayList<Integer> toAddIndices;
    private ArrayList<Integer> addedContacts;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        time = findViewById(R.id.editTime);

        addressView = findViewById(R.id.displayLocation);

        findViewById(R.id.chooseLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChoosingLocation();
            }
        });


        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if(destination == null) {
                    Snackbar.make(v, "Destination is needed", Snackbar.LENGTH_LONG)
                            .setAction("Set destination", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startChoosingLocation();
                                }
                            }).show();
                    return;
                }
                if(!goodTime(time.getText().toString())) {
                    Snackbar.make(v, "Setting estimated time is needed", Snackbar.LENGTH_LONG)
                            .setAction("", null).show();
                    return;
                }
                if(contacts.size() == 0) {
                    Snackbar.make(v, "At least one contact must be chosen", Snackbar.LENGTH_LONG)
                            .setAction("Add contacts", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    phoneContactsOpened = true;
                                    openChoosingContacts();
                                    hideKeyboard();
                                }
                            }).show();
                    return;
                }
                //permission do wysyłania sms
                if(ContextCompat.checkSelfPermission(StartActivity.this,
                        Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(StartActivity.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            getResources().getInteger(R.integer.REQUEST_SEND_SMS));
                else
                    startNewActivity();
            }
        });

        findViewById(R.id.addContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneContactsOpened = true;
                openChoosingContacts();
                hideKeyboard();
            }
        });

        LinearLayout footer = new LinearLayout(this);
        footer.setOrientation(LinearLayout.HORIZONTAL);
        accept = new Button(this);
        accept.setVisibility(View.INVISIBLE);


        Button cancel = new Button(this);
        cancel.setText(R.string.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePhoneContacts();
            }
        });

        toAdd = new ArrayList<>();
        toAddIndices = new ArrayList<>();

        footer.addView(cancel);
        footer.addView(accept);

        ((ListView)findViewById(R.id.chooseContacts)).addFooterView(footer);

        contacts = new ArrayList<>();
        addedContacts = new ArrayList<>();
        contactsAdapter = new
                ContactsList(this, contacts);
        ListView listView = findViewById(R.id.contactList);
        listView.setAdapter(contactsAdapter);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactsAdapter.addAll(toAdd);
                addedContacts.addAll(toAddIndices);
                toAdd.clear();
                toAddIndices.clear();
                closeChoosingContacts();
            }
        });


        new LoadDb(new DbSingleton(this).database.contactDao(), true) {
            @Override
            protected void onPostExecute(ArrayAdapter<Contact> result) {
                ListView list = findViewById(R.id.chooseContacts);
                list.setAdapter(result);
                adapter = result;

                if(savedInstanceState != null) {
                    if(savedInstanceState.getBoolean(addViewOpenedCode)) {
                        openChoosingContacts();
                        ArrayList<Integer> tmp = savedInstanceState.getIntegerArrayList(toAddCode);
                        ((ManageContactsList) adapter).checkBoxes(tmp);
                    }

                    //list of added contacts
                    addedContacts = savedInstanceState.getIntegerArrayList(contactsToAdd);
                    for(Integer pos : addedContacts) {
                        contactsAdapter.add(adapter.getItem(pos));
                    }
                }
            }
        }.execute(this);

        //load instance state
        if(savedInstanceState != null) {
            String address = savedInstanceState.getString(addressCode);
            if (address != null && !address.equals("")) {
                ((TextView) findViewById(R.id.displayLocation)).setText(address);
                destination = savedInstanceState.getParcelable(locationCode);
            }
            time.setText(savedInstanceState.getString(timeCode));
        }

        findViewById(R.id.backLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });

        findViewById(R.id.chooseContactsView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePhoneContacts();
            }
        });

    }

    private void startNewActivity() {
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

        Intent newActivity = new Intent(getApplicationContext(), OngoingActivity.class);
        newActivity.putExtra(getString(R.string.destination), destination);
        startActivity(newActivity);

        finish();
    }

    private void closePhoneContacts() {
        phoneContactsOpened = false;
        closeChoosingContacts();
        toAdd.clear();
        toAddIndices.clear();
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

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelable(locationCode, destination);
        bundle.putString(addressCode, addressView.getText().toString());
        bundle.putString(timeCode, time.getText().toString());
        bundle.putIntegerArrayList(toAddCode, toAddIndices);
        bundle.putBoolean(addViewOpenedCode, addViewOpened);
        bundle.putIntegerArrayList(contactsToAdd, addedContacts);
    }

    private void openChoosingContacts() {
        final ConstraintLayout layout = findViewById(R.id.chooseContactsView);
        addViewOpened = true;
        layout.setAlpha(0f);
        layout.setVisibility(View.VISIBLE);
        layout.animate().alpha(1f).setDuration(300).setListener(null);
        adapter.notifyDataSetChanged();
    }

    private void closeChoosingContacts() {
        final ConstraintLayout layout = findViewById(R.id.chooseContactsView);
        layout.animate().alpha(0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                layout.setVisibility(View.INVISIBLE);
                accept.setVisibility(View.INVISIBLE);
                ((ManageContactsList)adapter).uncheckBoxes();
            }
        });
        addViewOpened = false;
    }

    public void checkBox(int position) {
        if(toAdd.size() == 0) {
            accept.setAlpha(0f);
            accept.setVisibility(View.VISIBLE);
            accept.animate().alpha(1f).setDuration(200).setListener(null);
        }
        toAdd.add(adapter.getItem(position));
        toAddIndices.add(position);
        accept.setText("Confirm (" + toAdd.size() + ")");
    }

    public void uncheckBox(int position) {
        toAdd.remove(adapter.getItem(position));
        toAddIndices.remove((Integer)position);
        if(toAdd.size() == 0) {
            accept.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    accept.setVisibility(View.INVISIBLE);
                    accept.setText("Confirm (" + toAdd.size() + ")");
                }
            });
        }
    }

    private void startChoosingLocation() {
        Intent intent = new Intent(getApplicationContext(), SelectDestinationActivity.class);
        if(destination != null) {
            intent.putExtra(getString(R.string.location_available), true);
            intent.putExtra(getString(R.string.location), destination);
            intent.putExtra(getString(R.string.address), addressView.getText());
        } else
            intent.putExtra(getString(R.string.location_available), false);
        startActivityForResult(intent, DEST_SELECT_CODE);
    }

    private boolean goodTime(String time) {
        Pattern p = Pattern.compile("[0-9]|[0-9][0-9]|[0-9][0-9][0-9]|[0-9][0-9][0-9][0-9]");
        Matcher m = p.matcher(time);
        return m.matches();
    }

    public void deleteContact(Contact contact) {
        Integer pos = adapter.getPosition(contact);
        contactsAdapter.remove(contact);
        addedContacts.remove(pos);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        if(phoneContactsOpened)
            closePhoneContacts();
        else
            super.onBackPressed();
    }


    public boolean isAdded(int position) {
        return addedContacts.contains(position);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == getResources().getInteger(R.integer.REQUEST_SEND_SMS)) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startNewActivity();
            }
        }
    }
}
