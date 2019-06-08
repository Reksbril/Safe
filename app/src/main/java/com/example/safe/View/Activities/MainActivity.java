package com.example.safe.View.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.safe.R;
import com.example.safe.View.Activities.StartActivity.StartActivity;

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

//firebase
//lepsza mapa - przybliżona trasa np z polyline
//w wiadomości dodać lokalizację
//zamiast przycisków back, zmienić zachowanie przycisku na panelu






//TODO list
//poprawić design okna wybru kontaktu
//stop button powinien raczej robić coś więcej - wyświetlać jakis komunikat czy coś - snackbar jest spoko w sumie
//ogarnąć coś więcej do activity od trwającego activity - najlepiej jakieś info o czasie czy coś, ewentualnie dystans
//kliknięcie na kontakt powinno pokazywać całą wiadomość do wysłania
//wydajnośc listview
//tak samo przycisk add new - może być w sumie jako header zamiast footer, ale coś trzeba zmienić
//lista kontaktów z telefonu w widoku dodwania kontaktów powinna być ładniejsza
//lista kontaktów w dodawaniu do nowego activity powinna być na całą szerokość
//ogarnąć wszystkie listy jeżeli chodzi o szerokość tych wszystkich widoków

//ogólny design aplikacji
//jak pojawiąją się jakieś okienka czy coś to zrobic żeby to wyglądało ładnie
