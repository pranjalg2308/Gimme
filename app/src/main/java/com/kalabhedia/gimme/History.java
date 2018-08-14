package com.kalabhedia.gimme;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class History extends AppCompatActivity {
    private ArrayList<HistoryArray> list;
    private ListView listView;
    private Button noHistory;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("History");
        setContentView(R.layout.activity_history);
        noHistory = findViewById(R.id.noHistory);
        listView = findViewById(R.id.lvItemHistory);
        list = new ArrayList<>();
        HistoryDataBaseHelper db = new HistoryDataBaseHelper(getApplicationContext());
        db.getWritableDatabase();
        Cursor cr = db.getAllData();
        if (cr != null && cr.getCount() > 0) {
            noHistory.setVisibility(View.GONE);
            cr.moveToFirst();
            while (!cr.isAfterLast()) {
                list.add(new HistoryArray(cr.getString(0), cr.getString(1), cr.getString(2)));
                cr.moveToNext();
            }
        } else {
            noHistory.setVisibility(View.VISIBLE);
        }
        adapter = new HistoryAdapter(getApplicationContext(), R.id.lvItemHistory, list);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (list.size() == 0) {
                    Toast.makeText(History.this, "No history found", Toast.LENGTH_SHORT).show();
                } else {
                    HistoryDataBaseHelper dbHistory;
                    dbHistory = new HistoryDataBaseHelper(this);
                    dbHistory.getWritableDatabase();
                    adapter.notifyDataSetChanged();
                    dbHistory.deleteAllData();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                break;
            default:
                finish();
                break;
        }

        return true;
    }
}
