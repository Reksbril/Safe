package com.example.safe.View.Activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.safe.R;

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
    }
}



//TODO list
//obracanie ekranu w każdym activity - design
//obracanie ekranu w każdym activity - odpowiednie zachowanie
//PRZYCISK DELETE W EKRANIE USTAWIANIA ACTIVITY W LIŚCIE MA DZIAŁAĆ XD
//wpisywanie wiadomości, która ma zostać wysłana powinna być w większym oknie i ogólnie to jakoś bardziej ogarnięte
//lista kontaktów z telefonu w widoku dodwania kontaktów powinna być ładniejsza
//lista kontaktów w dodawaniu do nowego activity powinna być na całą szerokość
//uniemożliwić dodawanie kilka razy tego samego kontaktu - najlepiej checkboxy żeby były zaznaczone w tych kontaktach które są już dodane, albo jakoś na szaro je zrobić
//czasami nie dziaała obracanie ekranu - to też ogarnąć
//poprawić/dodać sprawdzanie dostępów (lokalizacja, wiadomości, kotakty...)
//dodać komunikaty o błędach w momencie rozpoczynania activity (czas/lokalicacja/kontakty)
//usuwanie klawiatury w odpowiednich momentach, w oknie rozpoczynania activity
//możliwośc anulowania edycji kontaktu w oknie "managecontactsactivity"
//poprawić design okna wybru kontaktu
//stop button powinien raczej robić coś więcej - wyświetlać jakis komunikat czy coś
//ogarnąć coś więcej do activity od trwającego activity - najlepiej jakieś info o czasie czy coś, ewentualnie dystans


//ogólny design aplikacji
