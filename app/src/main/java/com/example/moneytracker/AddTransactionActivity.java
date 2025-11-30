package com.example.moneytracker;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddTransactionActivity extends AppCompatActivity {

    private Spinner spinnerType, spinnerCategory, spinnerPayment; // Payment nuevo
    private EditText etAmount, etDescription;
    private Button btnSave, btnConvert;
    private DatabaseHelper dbHelper;
    private ExchangeRateApi api;
    private Transaction transactionToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        dbHelper = new DatabaseHelper(this);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerPayment = findViewById(R.id.spinnerPayment); // Link nuevo
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);
        btnConvert = findViewById(R.id.btnConvert);

        setupSpinners();
        setupRetrofit();

        if (getIntent().hasExtra("transaction_data")) {
            transactionToEdit = (Transaction) getIntent().getSerializableExtra("transaction_data");
            etAmount.setText(String.valueOf(transactionToEdit.getAmount()));
            etDescription.setText(transactionToEdit.getDescription());
            setSpinnerValue(spinnerType, transactionToEdit.getType());
            setSpinnerValue(spinnerCategory, transactionToEdit.getCategory());
            setSpinnerValue(spinnerPayment, transactionToEdit.getPaymentMethod());
            btnSave.setText("ACTUALIZAR");
        }

        btnSave.setOnClickListener(v -> saveTransaction());
        btnConvert.setOnClickListener(v -> convertCurrency());
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        if (value == null) return;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.exchangerate-api.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(ExchangeRateApi.class);
    }

    private void convertCurrency() {
        String amountStr = etAmount.getText().toString();
        if (amountStr.isEmpty()) return;
        api.getRates().enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(Call<ExchangeRateResponse> call, Response<ExchangeRateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Double rate = response.body().getRates().get("EUR");
                    showDialog(Double.parseDouble(amountStr), Double.parseDouble(amountStr) * rate);
                }
            }
            @Override public void onFailure(Call<ExchangeRateResponse> c, Throwable t) {}
        });
    }

    private void showDialog(double o, double c) {
        new AlertDialog.Builder(this).setMessage(o + " USD = " + String.format("%.2f", c) + " EUR").setPositiveButton("OK", null).show();
    }

    private void setupSpinners() {
        spinnerType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"EXPENSE", "INCOME"}));
        spinnerCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Alimentación", "Transporte", "Salud", "Ocio", "Salario", "Otros"}));
        // Spinner de Pago Nuevo
        ArrayAdapter<String> payAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Efectivo", "Tarjeta Crédito", "Transferencia", "Otro"});
        payAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPayment.setAdapter(payAdapter);
    }

    private void saveTransaction() {
        String amountStr = etAmount.getText().toString();
        if (amountStr.isEmpty()) { etAmount.setError("Requerido"); return; }

        double amount = Double.parseDouble(amountStr);
        String type = spinnerType.getSelectedItem().toString();
        String cat = spinnerCategory.getSelectedItem().toString();
        String pay = spinnerPayment.getSelectedItem().toString();
        String desc = etDescription.getText().toString();

        if (transactionToEdit == null) {
            Transaction t = new Transaction(type, amount, cat, desc, System.currentTimeMillis(), pay);
            if (dbHelper.addTransaction(t)) { finish(); }
        } else {
            transactionToEdit.setType(type);
            transactionToEdit.setAmount(amount);
            transactionToEdit.setCategory(cat);
            transactionToEdit.setDescription(desc);
            transactionToEdit.setPaymentMethod(pay);
            if (dbHelper.updateTransaction(transactionToEdit) > 0) { finish(); }
        }
    }
}