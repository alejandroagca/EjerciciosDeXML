package com.example.usuario.myapplication;

import android.content.Context;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by usuario on 11/01/18.
 */

public class Analisis {
    public static String analizarEmpleados(Context c) throws XmlPullParserException, IOException {
        StringBuilder cadena = new StringBuilder();
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
                        cadena.append("Nombre: " + xrp.nextText() + "\n");
                    }

                    if (xrp.getName().equals("puesto")) {
                        cadena.append("Puesto: " + xrp.nextText() + "\n");
                    }

                    if (xrp.getName().equals("edad")) {
                        edad = Double.parseDouble(xrp.nextText());
                        cadena.append("Edad: " + String.format("%.0f", edad) + "\n");
                        suma += edad;
                        contador++;
                    }

                    if (xrp.getName().equals("sueldo")) {
                        sueldo = Double.parseDouble(xrp.nextText());
                        cadena.append("Sueldo: " + String.format("%.2f", sueldo)+ "\n\n");
                        if (sueldo > sueldoMax){
                            sueldoMax = sueldo;
                        }
                        if (sueldo < sueldoMin){
                            sueldoMin = sueldo;
                        }

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
