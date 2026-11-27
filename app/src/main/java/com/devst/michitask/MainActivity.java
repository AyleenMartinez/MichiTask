package com.devst.michitask;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnNuevoTrabajo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets barras = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());

                    int margen = (int) (24 *
                            getResources().getDisplayMetrics().density);

                    v.setPadding(
                            barras.left + margen,
                            barras.top + margen,
                            barras.right + margen,
                            barras.bottom + margen
                    );

                    return insets;
                });

        btnNuevoTrabajo = findViewById(R.id.btnNuevoTrabajo);

        btnNuevoTrabajo.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    TrabajoActivity.class
            );

            startActivity(intent);
        });
    }
}
