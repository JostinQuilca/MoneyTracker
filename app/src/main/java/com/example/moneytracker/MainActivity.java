package com.example.moneytracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTotalBalance, tvBudgetLabel, tvDateFilter, tvWelcome;
    private ProgressBar progressBarBudget;
    private FloatingActionButton fabAdd;
    private Button btnStats, btnConfig, btnDateFilter;
    private Spinner spinnerFilter;
    private DatabaseHelper dbHelper;
    private TransactionAdapter adapter;
    private List<Transaction> allTransactions; // Lista completa de la BD
    private long startDate = 0;
    private long endDate = Long.MAX_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Verificación de Bienvenida (Si no hay presupuesto, ir a Ajustes)
        SharedPreferences prefs = getSharedPreferences("MoneyTrackerPrefs", MODE_PRIVATE);
        if (prefs.getFloat("budget", 0) == 0) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            finish();
            return;
        }

        // 2. Inicializar Vistas
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvBudgetLabel = findViewById(R.id.tvBudgetLabel);
        tvWelcome = findViewById(R.id.tvWelcome);
        progressBarBudget = findViewById(R.id.progressBarBudget);
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        btnStats = findViewById(R.id.btnStats);
        btnConfig = findViewById(R.id.btnConfig);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        btnDateFilter = findViewById(R.id.btnDateFilter);
        tvDateFilter = findViewById(R.id.tvDateFilter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new DatabaseHelper(this);

        // Mostrar Nombre
        String name = prefs.getString("username", "Usuario");
        tvWelcome.setText("Hola, " + name + " 👋");

        setupSwipeToDelete();
        setupFilterSpinner();

        // Botones
        fabAdd.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddTransactionActivity.class)));
        btnStats.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, StatsActivity.class)));
        btnConfig.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
        btnDateFilter.setOnClickListener(v -> showDateRangePicker());

        loadData();
    }

    private void showDateRangePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar start = Calendar.getInstance();
            start.set(year, month, dayOfMonth, 0, 0, 0);
            startDate = start.getTimeInMillis();
            tvDateFilter.setText("Desde: " + dayOfMonth + "/" + (month + 1) + "/" + year);
            applyFilters();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override protected void onResume() { super.onResume(); loadData(); }

    private void setupFilterSpinner() {
        spinnerFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Todos", "Ingresos", "Gastos"}));
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) { applyFilters(); }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void loadData() {
        // Cargar TODOS los datos
        allTransactions = dbHelper.getAllTransactions();

        // Calcular Balance
        double balance = dbHelper.getBalance();
        tvTotalBalance.setText("$ " + String.format("%.2f", balance));

        // Calcular Presupuesto y Alertas (Usando la lista completa)
        calculateBudgetProgress();

        // Aplicar filtros visuales a la lista
        applyFilters();
    }

    private void applyFilters() {
        if (allTransactions == null) return;
        List<Transaction> filtered = new ArrayList<>();
        int typeFilter = spinnerFilter.getSelectedItemPosition();

        for (Transaction t : allTransactions) {
            boolean matchesType = (typeFilter == 0) || (typeFilter == 1 && "INCOME".equals(t.getType())) || (typeFilter == 2 && "EXPENSE".equals(t.getType()));
            boolean matchesDate = (t.getDate() >= startDate && t.getDate() <= endDate);
            if (matchesType && matchesDate) filtered.add(t);
        }

        adapter = new TransactionAdapter(filtered, t -> {
            Intent i = new Intent(MainActivity.this, AddTransactionActivity.class);
            i.putExtra("transaction_data", t);
            startActivity(i);
        });
        recyclerView.setAdapter(adapter);
    }

    // --- AQUÍ ESTÁ LA LÓGICA DE LA ALERTA CORREGIDA ---
    private void calculateBudgetProgress() {
        // Obtenemos el límite
        float limit = getSharedPreferences("MoneyTrackerPrefs", MODE_PRIVATE).getFloat("budget", 0);

        // Sumamos TODOS los gastos (sin importar filtros de fecha o tipo)
        double totalExpense = 0;
        if (allTransactions != null) {
            for (Transaction t : allTransactions) {
                if ("EXPENSE".equals(t.getType())) {
                    totalExpense += t.getAmount();
                }
            }
        }

        if (limit > 0) {
            int progress = (int) ((totalExpense / limit) * 100);
            progressBarBudget.setProgress(progress);

            // Calculamos disponible
            double available = limit - totalExpense;

            // Texto actualizado
            tvBudgetLabel.setText("Gastado: $" + String.format("%.2f", totalExpense) + " | Disp: $" + String.format("%.2f", available));

            // SEMÁFORO DE ALERTAS (Colores)
            if (progress >= 100) {
                // ROJO: Te pasaste
                tvBudgetLabel.setTextColor(Color.parseColor("#FF5252"));
                progressBarBudget.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.RED));
            } else if (progress >= 80) {
                // AMARILLO: Cuidado (Alerta del 80%)
                tvBudgetLabel.setTextColor(Color.parseColor("#FFD740"));
                progressBarBudget.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.YELLOW));
            } else {
                // VERDE/BLANCO: Todo bien
                tvBudgetLabel.setTextColor(Color.WHITE);
                progressBarBudget.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.GREEN));
            }
        } else {
            tvBudgetLabel.setText("Presupuesto no definido");
            progressBarBudget.setProgress(0);
        }
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override public boolean onMove(@NonNull RecyclerView r, @NonNull RecyclerView.ViewHolder v, @NonNull RecyclerView.ViewHolder t) { return false; }
            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder v, int d) {
                // Borramos y recargamos
                dbHelper.deleteTransaction(adapter.getTransactionAt(v.getAdapterPosition()).getId());
                loadData();
                Toast.makeText(MainActivity.this, "Eliminado", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }
}