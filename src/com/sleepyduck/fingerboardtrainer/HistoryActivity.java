
package com.sleepyduck.fingerboardtrainer;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HistoryActivity extends Activity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        SharedPreferences prefs = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        Set<String> history = new HashSet<String>();
        history = prefs.getStringSet("history", history);
        ArrayList<String> data = new ArrayList<String>(history);
        Collections.sort(data);
        Collections.reverse(data);

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, data));
    }

}
