package com.example.usuario.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.usuario.myapplication.network.DownloadTask;
import com.example.usuario.myapplication.pojo.FailureEvent;
import com.example.usuario.myapplication.pojo.MessageEvent;
import com.example.usuario.myapplication.pojo.Noticia;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class NoticiasRSSActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String PERIODICO = "http://ep00.epimg.net/rss/elpais/portada.xml";
    public static final String TEMPORAL = "elpais.xml";
    ListView ltvLista;
    ArrayList<Noticia> arlListaNoticias;
    ArrayAdapter<Noticia> aryAdapter;
    FloatingActionButton fabUpdateNews;
    ProgressDialog pgdProgreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticias_rss);
        ltvLista = (ListView) findViewById(R.id.listView);
        ltvLista.setOnItemClickListener(this);
        fabUpdateNews = (FloatingActionButton) findViewById(R.id.fab_updateNews);
        fabUpdateNews.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == fabUpdateNews)
            descarga(PERIODICO, TEMPORAL);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onMessageEvent(MessageEvent event) {
        Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show();
        try {
            pgdProgreso.dismiss();
            arlListaNoticias = analizarNoticias(event.file);
            mostrar();
        } catch (Exception e) {
            Toast.makeText(this, "¡Error! :(", Toast.LENGTH_SHORT).show();
        }

    }

    @Subscribe
    public void handleFailure(FailureEvent event) {
        pgdProgreso.dismiss();
        Toast.makeText(this, "Algo ha salido mal... :(\n" + "mensaje: " + event.message + "\nstatus: " + event.status,Toast.LENGTH_SHORT).show();

    }

    private void descarga(String periodico, String temporal) {
        pgdProgreso = new ProgressDialog(this);
        pgdProgreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pgdProgreso.setMessage("Descargando . . .");
        pgdProgreso.setCancelable(false);
        pgdProgreso.show();
        DownloadTask.executeDownload(this, periodico, temporal);
    }

    private void mostrar() {
        if (arlListaNoticias != null)
            if (aryAdapter == null) {
                aryAdapter = new ArrayAdapter<Noticia>(this, android.R.layout.simple_list_item_1, arlListaNoticias);
                ltvLista.setAdapter(aryAdapter);
            }
            else {
                aryAdapter.clear();
                aryAdapter.addAll(arlListaNoticias);
            }
        else
            Toast.makeText(getApplicationContext(), "Error al crear la ltvLista", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Uri uri = Uri.parse((String) arlListaNoticias.get(position).getLink());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
        else
            Toast.makeText(getApplicationContext(), "No hay un navegador", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public static ArrayList<Noticia> analizarNoticias(File file) throws XmlPullParserException, IOException {
        int eventType;

        ArrayList<Noticia> noticias = null;
        Noticia actual = null;
        boolean dentroItem = false;

        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(new FileReader(file));
        eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    noticias = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    if (xpp.getName().equals("item")) {
                        dentroItem = true;
                        actual = new Noticia();
                    }

                    if (dentroItem && xpp.getName().equals("title")) {
                        actual.setTitle(xpp.nextText());
                    }
                    if (dentroItem && xpp.getName().equals("link")) {
                        actual.setLink(xpp.nextText());
                    }
                    if (dentroItem && xpp.getName().equals("description")) {
                        actual.setDescription(xpp.nextText());
                    }
                    if (dentroItem && xpp.getName().equals("pubDate")) {
                        actual.setPubDate(xpp.nextText());
                    }

                    break;
                case XmlPullParser.END_TAG:
                    if (xpp.getName().equals("item")) {
                        dentroItem = false;
                        noticias.add(actual);
                    }
                    break;
            }
            eventType = xpp.next();
        }
        return noticias;
    }
}
