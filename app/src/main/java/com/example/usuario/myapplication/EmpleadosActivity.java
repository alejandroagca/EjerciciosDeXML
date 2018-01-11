package com.example.usuario.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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
            txvTextoEmpleados.setText(Analisis.analizarEmpleados(this));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            txvTextoEmpleados.setText(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            txvTextoEmpleados.setText(e.getMessage());
        }
    }
}
