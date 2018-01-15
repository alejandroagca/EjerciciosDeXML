package com.example.usuario.myapplication;

import android.app.ProgressDialog;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.myapplication.network.RestClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class AEMETActivity extends AppCompatActivity {

    public static final String ENLACE = "http://www.aemet.es/xml/municipios/localidad_29067.xml";
    public static final String FICHERO = "aemet.xml";
    TextView txvTmpHoy;
    TextView txvTmpManana;
    TextView txvDiaHoy;
    TextView txvDiaManana;
    TextView txvPrimerTramo;
    TextView txvSegundoTramo;
    TextView txvTercerTramo;
    TextView txvCuartoTramo;
    ImageView imgPrimerTramoHoy;
    ImageView imgSegundoTramoHoy;
    ImageView imgTercerTramoHoy;
    ImageView imgCuartoTramoHoy;
    ImageView imgPrimerTramoMan;
    ImageView imgSegundoTramoMan;
    ImageView imgTercerTramoMan;
    ImageView imgCuartoTramoMan;
    String fechaHoy;
    Calendar hoy;
    String fechaManana;
    Calendar manana;
    int hora;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aemet);
        txvTmpHoy = (TextView) findViewById(R.id.txvTmpHoy);
        txvTmpManana = (TextView)findViewById(R.id.txvTmpManana);

        txvDiaHoy = (TextView)findViewById(R.id.txvDiaHoy);
        txvDiaManana = (TextView)findViewById(R.id.txvDiaManana);

        txvPrimerTramo = (TextView)findViewById(R.id.txvPrimerTramo);
        txvSegundoTramo = (TextView)findViewById(R.id.tvxSegundoTramo);
        txvTercerTramo = (TextView) findViewById(R.id.txvTercerTramo);
        txvCuartoTramo = (TextView) findViewById(R.id.txvCuartoTramo);

        imgPrimerTramoHoy = (ImageView)findViewById(R.id.imgPrimerTramoHoy);
        imgSegundoTramoHoy = (ImageView)findViewById(R.id.imgSegundoTramoHoy);
        imgTercerTramoHoy = (ImageView)findViewById(R.id.imgTercerTramoHoy);
        imgCuartoTramoHoy = (ImageView)findViewById(R.id.imgCuartoTramoHoy);

        imgPrimerTramoMan = (ImageView)findViewById(R.id.imgPrimerTramoMan);
        imgSegundoTramoMan = (ImageView)findViewById(R.id.imgSegundoTramoMan);
        imgTercerTramoMan = (ImageView)findViewById(R.id.imgTercerTramoMan);
        imgCuartoTramoMan = (ImageView)findViewById(R.id.imgCuartoTramoMan);

        hoy = Calendar.getInstance();
        manana = Calendar.getInstance();
        manana.add(Calendar.DATE, 1);
        fechaHoy = fechaACadena(hoy.getTime());
        fechaManana = fechaACadena(manana.getTime());
        hora = hoy.get(Calendar.HOUR_OF_DAY);

        escribirDias();
        escribirTramoHorario();

        descarga(ENLACE, FICHERO);
    }
    private void escribirTramoHorario(){
        if (hora < 6){
            txvPrimerTramo.setText("00-06");
        }
        if (hora < 12){
            txvSegundoTramo.setText("06-12");
        }
        if (hora < 18){
            txvTercerTramo.setText("12-18");
        }
        if (hora < 24){
            txvCuartoTramo.setText("18-24");
        }
    }
    private void escribirDias(){
        switch (hoy.get(Calendar.DAY_OF_WEEK)){
            case 1:
                txvDiaHoy.setText("dom ");
                txvDiaManana.setText("lun ");
                break;
            case 2:
                txvDiaHoy.setText("lun ");
                txvDiaManana.setText("mar ");
                break;
            case 3:
                txvDiaHoy.setText("mar ");
                txvDiaManana.setText("mié ");
                break;
            case 4:
                txvDiaHoy.setText("mié ");
                txvDiaManana.setText("jue ");
                break;
            case 5:
                txvDiaHoy.setText("jue ");
                txvDiaManana.setText("vie ");
                break;
            case 6:
                txvDiaHoy.setText("vie ");
                txvDiaManana.setText("sáb ");
                break;
            case 7:
                txvDiaHoy.setText("sáb ");
                txvDiaManana.setText("dom ");
                break;
        }
        txvDiaHoy.setText(txvDiaHoy.getText() + String.valueOf(hoy.get(Calendar.DAY_OF_MONTH)));
        txvDiaManana.setText(txvDiaManana.getText() + String.valueOf(manana.get(Calendar.DAY_OF_MONTH)));
    }
    private void descarga(String rss, String temporal) {
        final ProgressDialog progreso = new ProgressDialog(this);
        final File miFichero = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), temporal);
        miFichero.delete();
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
    private void analizarAEMET(File file) throws NullPointerException, XmlPullParserException, IOException {
        boolean dentroHoy = false;
        boolean dentroManana = false;
        boolean dentroTemperatura = false;
        boolean dentroMaxima = false;
        boolean dentroMinima = false;
        boolean dentroEstadoDiaPrimerTramo = false;
        boolean dentroEstadoDiaSegundoTramo = false;
        boolean dentroEstadoDiaTercerTramo = false;
        boolean dentroEstadoDiaCuartoTramo = false;
        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(new FileReader(file));
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (xpp.getName().equals("dia") && xpp.getAttributeValue(null, "fecha").equals(fechaHoy)){
                        dentroHoy = true;
                    }
                    if (xpp.getName().equals("dia") && xpp.getAttributeValue(null, "fecha").equals(fechaManana)){
                        dentroManana = true;
                    }

                    if ((dentroHoy || dentroManana) && xpp.getName().equals("temperatura")) {
                        dentroTemperatura = true;
                    }

                    if (dentroTemperatura && xpp.getName().equals("maxima")) {
                        dentroMaxima = true;
                    }

                    if (dentroTemperatura && xpp.getName().equals("minima")) {
                        dentroMinima = true;
                    }

                    if (hora < 6){
                        if (dentroHoy && xpp.getName().equals("estado_cielo") && xpp.getAttributeValue(null, "periodo").equals("00-06")){
                            dentroEstadoDiaPrimerTramo = true;
                    }
                        if (dentroHoy && xpp.getName().equals("estado_cielo") && xpp.getAttributeValue(null, "periodo").equals("06-12")){
                            dentroEstadoDiaSegundoTramo = true;
                        }

                        if (dentroHoy && xpp.getName().equals("estado_cielo") && xpp.getAttributeValue(null, "periodo").equals("12-18")){
                            dentroEstadoDiaTercerTramo = true;
                        }
                         if (dentroHoy && xpp.getName().equals("estado_cielo") && xpp.getAttributeValue(null, "periodo").equals("18-24")){
                            dentroEstadoDiaCuartoTramo = true;
                        }
                    }

                    if (hora < 12 && hora >= 6){
                        if (dentroHoy && xpp.getName().equals("estado_cielo") && xpp.getAttributeValue(null, "periodo").equals("06-12")){
                            dentroEstadoDiaSegundoTramo = true;
                        }

                        if (dentroHoy && xpp.getName().equals("estado_cielo") && xpp.getAttributeValue(null, "periodo").equals("12-18")){
                            dentroEstadoDiaTercerTramo = true;
                        }
                        if (dentroHoy && xpp.getName().equals("estado_cielo") && xpp.getAttributeValue(null, "periodo").equals("18-24")){
                            dentroEstadoDiaCuartoTramo = true;
                        }
                    }

                    if (hora < 18 && hora >= 12){
                        if (dentroHoy && xpp.getName().equals("estado_cielo") && xpp.getAttributeValue(null, "periodo").equals("12-18")){
                            dentroEstadoDiaTercerTramo = true;
                        }
                        if (dentroHoy && xpp.getName().equals("estado_cielo") && xpp.getAttributeValue(null, "periodo").equals("18-24")){
                            dentroEstadoDiaCuartoTramo = true;
                        }
                    }

                    if (hora >= 18){
                        if (dentroHoy && xpp.getName().equals("estado_cielo") && xpp.getAttributeValue(null, "periodo").equals("18-24")){
                            dentroEstadoDiaCuartoTramo = true;
                        }
                    }


                    if (dentroManana && xpp.getName().equals("estado_cielo") && xpp.getAttributeValue(null, "periodo").equals("00-06")){
                        dentroEstadoDiaPrimerTramo = true;
                    }
                    if (dentroManana && xpp.getName().equals("estado_cielo") && xpp.getAttributeValue(null, "periodo").equals("06-12")){
                        dentroEstadoDiaSegundoTramo = true;
                    }
                    if (dentroManana && xpp.getName().equals("estado_cielo") && xpp.getAttributeValue(null, "periodo").equals("12-18")){
                        dentroEstadoDiaTercerTramo = true;
                    }
                    if (dentroManana && xpp.getName().equals("estado_cielo") && xpp.getAttributeValue(null, "periodo").equals("18-24")){
                        dentroEstadoDiaCuartoTramo = true;
                    }
                    break;

                case XmlPullParser.TEXT:
                    if (dentroMaxima && dentroHoy) {
                         txvTmpHoy.setText(xpp.getText());
                    }
                    if (dentroMaxima && dentroManana){
                        txvTmpManana.setText(xpp.getText());
                    }
                    if (dentroMinima && dentroHoy){
                        txvTmpHoy.setText(xpp.getText() + " / " + txvTmpHoy.getText());
                    }

                    if (dentroMinima && dentroManana){
                        txvTmpManana.setText(xpp.getText() + " / " + txvTmpManana.getText());
                    }

                    if (dentroEstadoDiaPrimerTramo && dentroHoy) {
                        if (xpp.getText().equals("11n")) {
                            imgPrimerTramoHoy.setImageResource(R.drawable.luna);
                        }
                        else if (xpp.getText().equals("12n") || xpp.getText().equals("13n")) {
                            imgPrimerTramoHoy.setImageResource(R.drawable.nublado);

                        }
                        else if (xpp.getText().equals("17n")) {
                            imgPrimerTramoHoy.setImageResource(R.drawable.nublado);
                        }
                        else if (xpp.getText().equals("24n")) {
                            imgPrimerTramoHoy.setImageResource(R.drawable.lluvioso);
                        }
                        else{
                            imgPrimerTramoHoy.setImageResource(R.drawable.error);
                        }
                    }
                    if (dentroEstadoDiaSegundoTramo && dentroHoy) {
                        if (xpp.getText().equals("11")) {
                            imgSegundoTramoHoy.setImageResource(R.drawable.despejado);
                        }
                        else if (xpp.getText().equals("12") || xpp.getText().equals("13")) {
                            imgSegundoTramoHoy.setImageResource(R.drawable.nublado);
                        }
                        else if (xpp.getText().equals("17")) {
                            imgSegundoTramoHoy.setImageResource(R.drawable.nublado);
                        }
                        else if (xpp.getText().equals("24")) {
                            imgSegundoTramoHoy.setImageResource(R.drawable.lluvioso);
                        }
                        else{
                            imgSegundoTramoHoy.setImageResource(R.drawable.error);
                        }
                    }
                    if (dentroEstadoDiaTercerTramo && dentroHoy) {
                         if (xpp.getText().equals("11")) {
                            imgTercerTramoHoy.setImageResource(R.drawable.despejado);
                        }
                        else if (xpp.getText().equals("12") || xpp.getText().equals("13")) {
                            imgTercerTramoHoy.setImageResource(R.drawable.nublado);
                        }
                        else if (xpp.getText().equals("17")) {
                            imgTercerTramoHoy.setImageResource(R.drawable.nublado);
                        }
                        else if (xpp.getText().equals("24")) {
                            imgTercerTramoHoy.setImageResource(R.drawable.lluvioso);
                        }
                         else{
                             imgTercerTramoHoy.setImageResource(R.drawable.error);
                         }
                    }
                    if (dentroEstadoDiaCuartoTramo && dentroHoy) {
                        if (xpp.getText().equals("11n")) {
                            imgCuartoTramoHoy.setImageResource(R.drawable.luna);
                        }
                        else if (xpp.getText().equals("12n") || xpp.getText().equals("13n")) {
                            imgCuartoTramoHoy.setImageResource(R.drawable.nublado);
                        }
                        else if (xpp.getText().equals("17n")) {
                            imgCuartoTramoHoy.setImageResource(R.drawable.nublado);
                        }
                        else if (xpp.getText().equals("24n")) {
                            imgCuartoTramoHoy.setImageResource(R.drawable.lluvioso);
                        }
                        else{
                            imgCuartoTramoHoy.setImageResource(R.drawable.error);
                        }
                    }

                    if (dentroEstadoDiaPrimerTramo && dentroManana) {
                        if (xpp.getText().equals("11n")) {
                            imgPrimerTramoMan.setImageResource(R.drawable.luna);
                        }
                        else if (xpp.getText().equals("12n") || xpp.getText().equals("13n")) {
                            imgPrimerTramoMan.setImageResource(R.drawable.nublado);
                        }
                        else if (xpp.getText().equals("17n")) {
                            imgPrimerTramoMan.setImageResource(R.drawable.nublado);
                        }
                        else if (xpp.getText().equals("24n")) {
                            imgPrimerTramoMan.setImageResource(R.drawable.lluvioso);
                        }
                        else{
                            imgPrimerTramoMan.setImageResource(R.drawable.error);
                        }
                    }
                    if (dentroEstadoDiaSegundoTramo && dentroManana) {
                        if (xpp.getText().equals("11")) {
                            imgSegundoTramoMan.setImageResource(R.drawable.despejado);
                        }
                        else if (xpp.getText().equals("12") || xpp.getText().equals("13")) {
                            imgSegundoTramoMan.setImageResource(R.drawable.nublado);
                        }
                        else if (xpp.getText().equals("17")) {
                            imgSegundoTramoMan.setImageResource(R.drawable.nublado);
                        }
                        else if (xpp.getText().equals("24")) {
                            imgSegundoTramoMan.setImageResource(R.drawable.lluvioso);
                        }
                        else{
                            imgSegundoTramoMan.setImageResource(R.drawable.error);
                        }
                    }
                    if (dentroEstadoDiaTercerTramo && dentroManana) {
                        if (xpp.getText().equals("11")) {
                            imgTercerTramoMan.setImageResource(R.drawable.despejado);
                        }
                        else if (xpp.getText().equals("12") || xpp.getText().equals("13")) {
                            imgTercerTramoMan.setImageResource(R.drawable.nublado);
                        }
                        else if (xpp.getText().equals("17")) {
                            imgTercerTramoMan.setImageResource(R.drawable.nublado);
                        }
                        else if (xpp.getText().equals("24")) {
                            imgTercerTramoMan.setImageResource(R.drawable.lluvioso);
                        }
                        else{
                            imgTercerTramoMan.setImageResource(R.drawable.error);
                        }
                    }
                    if (dentroEstadoDiaCuartoTramo && dentroManana) {
                        if (xpp.getText().equals("11n")) {
                            imgCuartoTramoMan.setImageResource(R.drawable.luna);
                        }
                        else if (xpp.getText().equals("12n") || xpp.getText().equals("13n")) {
                            imgCuartoTramoMan.setImageResource(R.drawable.nublado);
                        }
                        else if (xpp.getText().equals("17n")) {
                            imgCuartoTramoMan.setImageResource(R.drawable.nublado);
                        }
                        else if (xpp.getText().equals("24n")) {
                            imgCuartoTramoMan.setImageResource(R.drawable.lluvioso);
                        }
                        else{
                            imgCuartoTramoMan.setImageResource(R.drawable.error);
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if (xpp.getName().equals("dia")){
                        dentroHoy = false;
                        dentroManana = false;
                    }
                    if (xpp.getName().equals("estado_cielo")){
                        dentroEstadoDiaPrimerTramo = false;
                        dentroEstadoDiaSegundoTramo = false;
                        dentroEstadoDiaTercerTramo = false;
                        dentroEstadoDiaCuartoTramo = false;
                    }
                    if (xpp.getName().equals("temperatura")) {
                        dentroTemperatura = false;
                    }
                    if (xpp.getName().equals("maxima")) {
                        dentroMaxima = false;
                    }
                    if (xpp.getName().equals("minima")) {
                        dentroMinima = false;
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
