package com.example.usuario.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Noticias2RSSActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView lista;
    ArrayList<String> periodicos;
    ArrayAdapter<String> adapter;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticias2_rss);
        periodicos = new ArrayList<String>();
        periodicos.add("El Pais");
        periodicos.add("El Mundo");
        periodicos.add("Linux Magazine");
        periodicos.add("PCWorld");
        lista = (ListView) findViewById(R.id.listView);
        lista.setOnItemClickListener(this);
        mostrar();
    }

    private void mostrar() {
        if (periodicos != null)
            if (adapter == null) {
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, periodicos);
                lista.setAdapter(adapter);
            }
            else {
                adapter.clear();
                adapter.addAll(periodicos);
            }
        else
            Toast.makeText(getApplicationContext(), "Error al crear la lista", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String periodicoPulsado = periodicos.get(position);
        switch (periodicoPulsado){
            case "El Pais":
                intent = new Intent(this, NoticiasRSSActivity.class);
                intent.putExtra("direccion", "http://ep00.epimg.net/rss/elpais/portada.xml");
                startActivity(intent);
                break;
            case "El Mundo":
                intent = new Intent(this, NoticiasRSSActivity.class);
                intent.putExtra("direccion", "http://estaticos.elmundo.es/elmundo/rss/espana.xml");
                startActivity(intent);
                break;
            case "Linux Magazine":
                intent = new Intent(this, NoticiasRSSActivity.class);
                intent.putExtra("direccion", "http://www.linux-magazine.com/rss/feed/lmi_news");
                startActivity(intent);
                break;
            case "PCWorld":
                intent = new Intent(this, NoticiasRSSActivity.class);
                intent.putExtra("direccion", "https://www.pcworld.com/index.rss");
                startActivity(intent);
                break;
        }
    }

}
