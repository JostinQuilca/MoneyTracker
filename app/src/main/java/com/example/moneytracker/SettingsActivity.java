package com.example.moneytracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText etName = findViewById(R.id.etName); // Nuevo
        EditText etBudget = findViewById(R.id.etBudget);
        Button btnSave = findViewById(R.id.btnSaveSettings);
        Button btnReset = findViewById(R.id.btnReset);

        SharedPreferences prefs = getSharedPreferences("MoneyTrackerPrefs", MODE_PRIVATE);

        // Cargar datos actuales
        float currentBudget = prefs.getFloat("budget", 0);
        String currentName = prefs.getString("username", ""); // Cargar nombre

        if(currentBudget > 0) etBudget.setText(String.valueOf(currentBudget));
        etName.setText(currentName);

        // 1. Guardar
        btnSave.setOnClickListener(v -> {
            String budgetVal = etBudget.getText().toString();
            String nameVal = etName.getText().toString();

            if(!budgetVal.isEmpty() && !nameVal.isEmpty()){
                SharedPreferences.Editor editor = prefs.edit();
                editor.putFloat("budget", Float.parseFloat(budgetVal));
                editor.putString("username", nameVal); // Guardar nombre
                editor.apply();

                Toast.makeText(this, "¡Listo! Configuración guardada.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Completa nombre y presupuesto", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Restablecer
        btnReset.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("¿Reiniciar App?")
                    .setMessage("Se borrará todo y volverás a la pantalla de inicio.")
                    .setPositiveButton("Sí, Borrar Todo", (dialog, which) -> {
                        new DatabaseHelper(this).resetDatabase();
                        prefs.edit().clear().apply();

                        Toast.makeText(this, "App Restablecida", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }
}