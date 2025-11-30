package com.example.moneytracker;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;

public class StatsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        PieChart chart = findViewById(R.id.pieChart);
        TextView tvNoData = findViewById(R.id.tvNoData);
        Cursor c = new DatabaseHelper(this).getExpensesByCategory();
        ArrayList<PieEntry> entries = new ArrayList<>();

        if (c.moveToFirst()) {
            do { entries.add(new PieEntry(c.getFloat(1), c.getString(0))); } while (c.moveToNext());
        }
        c.close();

        if (entries.isEmpty()) {
            chart.setVisibility(View.GONE); tvNoData.setVisibility(View.VISIBLE);
        } else {
            PieDataSet set = new PieDataSet(entries, "");
            set.setColors(ColorTemplate.MATERIAL_COLORS);
            set.setValueTextSize(14f);
            chart.setData(new PieData(set));
            chart.getDescription().setEnabled(false);
            chart.animateY(1000);
            chart.invalidate();
        }
    }
}