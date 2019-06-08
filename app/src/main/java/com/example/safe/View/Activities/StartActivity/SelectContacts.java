package com.example.safe.View.Activities.StartActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.safe.Database.ContactDao;
import com.example.safe.Database.DbSingleton;
import com.example.safe.Model.Contact;
import com.example.safe.R;
import com.example.safe.View.Activities.AsyncTasks.LoadDb;

public class SelectContacts extends Fragment {
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_contacts, container, false);

        ContactDao dao = new DbSingleton(getContext()).database.contactDao();

        new LoadDb(dao, true) {
            @Override
            protected void onPostExecute(ArrayAdapter<Contact> result) {
                ArrayAdapter<Contact> adapter = result;
                ((StartActivity) SelectContacts.this.getActivity()).setAdapter(adapter);
                ListView list = rootView.findViewById(R.id.contactsList);
                list.setAdapter(adapter);
                stopLoadingScreen();
            }
        }.execute(getActivity());

        rootView.findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StartActivity) SelectContacts.this.getActivity()).startNewActivity();
            }
        });

        return rootView;
    }

    private void stopLoadingScreen() {
        final ConstraintLayout layout = rootView.findViewById(R.id.loadingLayout);
        layout.animate().alpha(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                layout.setVisibility(View.INVISIBLE);
            }
        });
    }
}
