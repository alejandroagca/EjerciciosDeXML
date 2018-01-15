package com.example.usuario.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    Button btnEjercicio1;
    Button btnEjercicio2;
    Button btnEjercicio3;
    Button btnEjercicio4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnEjercicio1 = findViewById(R.id.btnEjercicio1);
        btnEjercicio1.setOnClickListener(this);
        btnEjercicio2 = findViewById(R.id.btnEjercicio2);
        btnEjercicio2.setOnClickListener(this);
        btnEjercicio3 = findViewById(R.id.btnEjercicio3);
        btnEjercicio3.setOnClickListener(this);
        btnEjercicio4 = findViewById(R.id.btnEjercicio4);
        btnEjercicio4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        if (v == btnEjercicio1){
            i = new Intent(this, EmpleadosActivity.class);
            startActivity(i);
        }

        if (v == btnEjercicio2){
            i = new Intent(this, AEMETActivity.class);
            startActivity(i);
        }

        /*if (v == btnEjercicio3){
            i = new Intent(this, Ejercicio3Activity.class);
            startActivity(i);
        }*/



        if (v == btnEjercicio4){
            i = new Intent(this, Noticias2RSSActivity.class);
            startActivity(i);
        }
    }
}
