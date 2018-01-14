package com.example.usuario.myapplication;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class EmpleadosActivity extends AppCompatActivity {

    TextView txvTextoEmpleados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empleados);
        txvTextoEmpleados = findViewById(R.id.txvTextoEmpleados);
        try {
            txvTextoEmpleados.setText(analizarEmpleados(this));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            txvTextoEmpleados.setText(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            txvTextoEmpleados.setText(e.getMessage());
        }
    }

    public static String analizarEmpleados(Context c) throws XmlPullParserException, IOException {
        StringBuilder cadena = new StringBuilder();
        boolean dentroNombre = false;
        boolean dentroPuesto = false;
        boolean dentroEdad = false;
        boolean dentroSueldo = false;
        double suma = 0;
        double edad = 0;
        double sueldo = 0;
        int contador = 0;
        double sueldoMin = Integer.MAX_VALUE;
        double sueldoMax = Integer.MIN_VALUE;
        XmlResourceParser xrp = c.getResources().getXml(R.xml.empleados);
        int eventType = xrp.getEventType();
        while (eventType != XmlPullParser. END_DOCUMENT ) {
            switch (eventType) {
                case XmlPullParser.START_TAG :
                    if (xrp.getName().equals("nombre")) {
                        dentroNombre = true;
                    }

                    if (xrp.getName().equals("puesto")) {
                        dentroPuesto = true;
                    }

                    if (xrp.getName().equals("edad")) {
                        dentroEdad = true;
                    }

                    if (xrp.getName().equals("sueldo")) {
                        dentroSueldo = true;
                    }
                    break;

                case XmlPullParser.TEXT:
                    if (dentroNombre){
                        cadena.append("Nombre: " + xrp.getText() + "\n");
                    }
                    if (dentroPuesto){
                        cadena.append("Puesto: " + xrp.getText() + "\n");
                    }
                    if (dentroEdad){
                        edad = Double.parseDouble(xrp.getText());
                        cadena.append("Edad: " + String.format("%.0f", edad) + "\n");
                        suma += edad;
                        contador++;
                    }
                    if (dentroSueldo){
                        sueldo = Double.parseDouble(xrp.getText());
                        cadena.append("Sueldo: " + String.format("%.2f", sueldo)+ "\n\n");
                        if (sueldo > sueldoMax){
                            sueldoMax = sueldo;
                        }
                        if (sueldo < sueldoMin){
                            sueldoMin = sueldo;
                        }
                    }
                    break;
                case  XmlPullParser.END_TAG:
                    if (xrp.getName().equals("nombre")) {
                        dentroNombre = false;
                    }

                    if (xrp.getName().equals("puesto")) {
                        dentroPuesto = false;
                    }

                    if (xrp.getName().equals("edad")) {
                        dentroEdad = false;
                    }

                    if (xrp.getName().equals("sueldo")) {
                        dentroSueldo = false;
                    }
                    break;
            }
            eventType = xrp.next();
        }
        cadena.append("Media de edad: "+ String.format("%.2f", suma/contador) + "\n");
        cadena.append("Sueldo máximo: " + String.format("%.2f", sueldoMax) + "\n");
        cadena.append("Sueldo mínimo: " + String.format("%.2f", sueldoMin));

        return cadena.toString();
    }
}
