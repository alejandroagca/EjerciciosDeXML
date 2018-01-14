package com.example.usuario.myapplication;

import android.app.ProgressDialog;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class AEMETActivity extends AppCompatActivity {

    public static final String ENLACE = "http://www.aemet.es/xml/municipios/localidad_29067.xml";
    public static final String FICHERO = "aemet.xml";
    TextView txvTmpMaxima;
    TextView txvTmpMinima;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aemet);
        txvTmpMaxima = (TextView) findViewById(R.id.txvTmpMaxima);
        txvTmpMinima = (TextView)findViewById(R.id.txvTmpMinima);
        descarga(ENLACE, FICHERO);

    }

    private void descarga(String rss, String temporal) {
        final ProgressDialog progreso = new ProgressDialog(this);
        final File miFichero = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), temporal);
        RestClient.get(rss, new FileAsyncHttpResponseHandler(miFichero) {
            @Override
            public void onStart() {
                super.onStart();
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Conectando . . .");
                progreso.setCancelable(false);
                progreso.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                progreso.dismiss();
                Toast.makeText(getApplicationContext(), "Fichero descargado correctamente", Toast.LENGTH_LONG).show();
                try {
                    analizarAEMET(miFichero);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                progreso.dismiss();
                Toast.makeText(getApplicationContext(), "Fallo: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void analizarAEMET(File file) throws NullPointerException, XmlPullParserException, IOException {
        boolean dentroHoy = false;
        boolean dentroManana = false;
        boolean dentroTemperatura = false;
        boolean dentroMaxima = false;
        boolean dentroMinima = false;
        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(new FileReader(file));
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (xpp.getName().equals("dia") && xpp.getAttributeValue(null, "fecha").equals("2018-01-14")){
                        dentroHoy = true;
                    }
                    if (dentroHoy && xpp.getName().equals("temperatura")) {
                        dentroTemperatura = true;
                    }
                    if (dentroTemperatura && xpp.getName().equals("maxima")) {
                        dentroMaxima = true;
                    }
                    if (dentroTemperatura && xpp.getName().equals("minima")) {
                        dentroMinima = true;
                    }
                    break;

                case XmlPullParser.TEXT:
                    if (dentroMaxima) {
                         txvTmpMaxima.setText(xpp.getText());
                    }
                    if (dentroMinima){
                        txvTmpMinima.setText(xpp.getText());
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if (xpp.getName().equals("dia")){
                        dentroHoy = false;
                    }
                    if (dentroHoy && xpp.getName().equals("temperatura")) {
                        dentroTemperatura = false;
                    }
                    if (dentroTemperatura && xpp.getName().equals("maxima")) {
                        dentroMaxima = false;
                    }
                    if (dentroTemperatura && xpp.getName().equals("minima")) {
                        dentroMinima = false;
                    }
                    break;

            }
            eventType = xpp.next();

        }
    }
}
