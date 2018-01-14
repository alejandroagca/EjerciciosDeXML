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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class AEMETActivity extends AppCompatActivity {

    public static final String ENLACE = "http://www.aemet.es/xml/municipios/localidad_29067.xml";
    public static final String FICHERO = "aemet.xml";
    TextView txvTmpMHoy;
    TextView txvTmpManana;
    String fechaHoy;
    Date hoy;
    String fechaManana;
    Calendar manana;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aemet);
        txvTmpMHoy = (TextView) findViewById(R.id.txvTmpHoy);
        txvTmpManana = (TextView)findViewById(R.id.txvTmpManana);
        manana = Calendar.getInstance();
        hoy = new Date();
        manana.add(Calendar.DATE, Integer.valueOf(1));
        fechaHoy = fechaACadena(hoy);
        fechaManana = fechaACadena(manana.getTime());

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
        boolean dentroTemperaturaHoy = false;
        boolean dentroMaximaHoy = false;
        boolean dentroMinimaHoy = false;
        boolean dentroTemperaturaManana = false;
        boolean dentroMaximaManana = false;
        boolean dentroMinimaManana = false;
        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(new FileReader(file));
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (xpp.getName().equals("dia") && xpp.getAttributeValue(null, "fecha").equals(fechaHoy)){
                        dentroHoy = true;
                    }
                    if (dentroHoy && xpp.getName().equals("temperatura")) {
                        dentroTemperaturaHoy = true;
                    }
                    if (dentroTemperaturaHoy && xpp.getName().equals("maxima")) {
                        dentroMaximaHoy = true;
                    }
                    if (dentroTemperaturaHoy && xpp.getName().equals("minima")) {
                        dentroMinimaHoy = true;
                    }
                    if (xpp.getName().equals("dia") && xpp.getAttributeValue(null, "fecha").equals(fechaManana)){
                        dentroManana = true;
                    }
                    if (dentroManana && xpp.getName().equals("temperatura")) {
                        dentroTemperaturaManana = true;
                    }
                    if (dentroTemperaturaManana && xpp.getName().equals("maxima")) {
                        dentroMaximaManana = true;
                    }
                    if (dentroTemperaturaManana && xpp.getName().equals("minima")) {
                        dentroMinimaManana = true;
                    }
                    break;

                case XmlPullParser.TEXT:
                    if (dentroMaximaHoy) {
                         txvTmpMHoy.setText(xpp.getText() + " / ");
                    }
                    if (dentroMinimaHoy){
                        txvTmpMHoy.setText(txvTmpMHoy.getText() + xpp.getText());
                    }

                    if (dentroMaximaManana) {
                        txvTmpManana.setText(xpp.getText() + " / ");
                    }
                    if (dentroMinimaManana){
                        txvTmpManana.setText(txvTmpManana.getText() + xpp.getText());
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if (xpp.getName().equals("dia")){
                        dentroHoy = false;
                    }
                    if (dentroHoy && xpp.getName().equals("temperatura")) {
                        dentroTemperaturaHoy = false;
                    }
                    if (dentroTemperaturaHoy && xpp.getName().equals("maxima")) {
                        dentroMaximaHoy = false;
                    }
                    if (dentroTemperaturaHoy && xpp.getName().equals("minima")) {
                        dentroMinimaHoy = false;
                    }
                    if (dentroManana && xpp.getName().equals("temperatura")) {
                        dentroTemperaturaManana = false;
                    }
                    if (dentroTemperaturaManana && xpp.getName().equals("maxima")) {
                        dentroMaximaManana = false;
                    }
                    if (dentroTemperaturaManana && xpp.getName().equals("minima")) {
                        dentroMinimaManana = false;
                    }
                    break;

            }
            eventType = xpp.next();

        }
    }

    private String fechaACadena(Date d) {
        //pasar una fecha a un string
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return df.format(d);
    }
}
