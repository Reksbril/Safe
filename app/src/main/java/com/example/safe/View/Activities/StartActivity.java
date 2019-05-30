package com.example.safe.View.Activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.safe.View.ListViews.ContactsList;
import com.example.safe.View.Background.CurrentActivity;
import com.example.safe.R;

import java.util.ArrayList;


public class StartActivity extends Activity {
    private final static int DEST_SELECT_CODE = 15523;
    Location destination;
    TextView addressView;

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



        ListView list;
        ArrayList<String> web = new ArrayList<String>();
        web.add("asdasdasdsad");

        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(R.drawable.common_full_open_on_phone);

        ContactsList adapter = new
                ContactsList(StartActivity.this, web, ids);
        list=(ListView)findViewById(R.id.contactList);
        list.setAdapter(adapter);
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

}
