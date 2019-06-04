package com.example.safe.View.Activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.safe.R;
import com.getbase.floatingactionbutton.AddFloatingActionButton;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.startNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.manageContacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ManageContactsActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }
}



//TODO list
//sprawdzić czy smsy działają
//usuwanie klawiatury w odpowiednich momentach, w oknie rozpoczynania activity
//możliwośc anulowania edycji kontaktu w oknie "managecontactsactivity"
//poprawić design okna wybru kontaktu
//stop button powinien raczej robić coś więcej - wyświetlać jakis komunikat czy coś - snackbar jest spoko w sumie
//ogarnąć coś więcej do activity od trwającego activity - najlepiej jakieś info o czasie czy coś, ewentualnie dystans
//kliknięcie na kontakt powinno pokazywać całą wiadomość do wysłania
//wydajnośc listview
//coś zbugowane z tą listą gdzie są checkboxy (jak się zamknie z zaznaczonymi, to po otwarciu dalej są jakieś zaznaczone)
//przycisk startactivity powinien być jakiś inny. Na przykład podobnie jak z tym plusem w dodawaniu kontaktów
//tak samo przycisk add new - może być w sumie jako header zamiast footer, ale coś trzeba zmienić
//dodać taki mały "+" w prawym dolnym rogu do dodawania kontaktów
//lista kontaktów z telefonu w widoku dodwania kontaktów powinna być ładniejsza
//lista kontaktów w dodawaniu do nowego activity powinna być na całą szerokość
//ogarnąć wszystkie listy jeżeli chodzi o szerokość tych wszystkich widoków
//najlepiej ustawić MAX_MESSAGE_LEN różne w zależności od szerokości ekranu itd
//ogarnąć to powiadomienie dla niższych wersji - bez notification channel

//ogólny design aplikacji
//jak pojawiąją się jakieś okienka czy coś to zrobic żeby to wyglądało ładnie
