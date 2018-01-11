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
        boolean esNombre = false;
        boolean esSueldo = false;
        boolean esPuesto = false;
        boolean esEdad = false;
        StringBuilder cadena = new StringBuilder();
        Double suma = 0.0;
        int contador = 0;
        XmlResourceParser xrp = c.getResources().getXml(R.xml.empleados);
        int eventType = xrp.getEventType();
        while (eventType != XmlPullParser. END_DOCUMENT ) {
            switch (eventType) {
                case XmlPullParser.START_TAG :
                    if(xrp.getName().equals("nombre"))
                        esNombre = true;
                    /*else if(xrp.getName().equals("puesto")){
                        esPuesto = true;
                    }
                    else if(xrp.getName().equals("edad")){
                        esEdad = true;
                    }*/
                    else if(xrp.getName().equals("sueldo")){
                        esSueldo = true;
                    }
                    break;
                case XmlPullParser.TEXT :
                    if(esNombre){
                        cadena.append("Empleado: "+ xrp.getText());
                    }
                    /*else if(esPuesto){
                        cadena.append("Puesto: "+ xrp.getText() + "\n");
                    }
                    else if(esEdad){
                        cadena.append("Edad: "+ xrp.getText() + "\n");
                    }*/
                    else if(esSueldo){
                        cadena.append("Sueldo: "+xrp.getText()+"\n\n");
                        suma += Double.parseDouble(xrp.getText());
                        contador++;
                    }
                    break;
                case XmlPullParser.END_TAG :
                    if (esNombre){
                        esNombre = false;
                        cadena.append("\n");
                    }
                    else if(esSueldo){
                        esSueldo = false;
                    }
                    break;
            }
            eventType = xrp.next();
        }
        cadena.append("Media: "+ String.format("%.2f", suma/contador));
        return cadena.toString();
    }
}
