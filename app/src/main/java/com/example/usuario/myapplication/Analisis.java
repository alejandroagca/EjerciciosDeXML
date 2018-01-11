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
        double suma = 0.0;
        double sueldo = 0.0;
        int contador = 0;
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
                        cadena.append("Edad: " + xrp.nextText() + "\n");
                    }

                    if (xrp.getName().equals("sueldo")) {
                        sueldo = Double.parseDouble(xrp.nextText());
                        cadena.append("Nota: " + Double.toString(sueldo) + "\n\n");
                        suma += sueldo;
                        contador++;
                    }

                    break;
            }
            eventType = xrp.next();
        }
        cadena.append("Media: "+ String.format("%.2f", suma/contador));
        return cadena.toString();
    }
}
