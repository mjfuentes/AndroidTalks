package com.mjfuentes.androidandadiciendo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private MainAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView list = (ListView)findViewById(R.id.listView);
        adapter = new MainAdapter(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this,PageActivity.class);
                i.putExtra("uri",((FacebookPage)adapter.getItem(position)).uri);
                i.putExtra("name",((FacebookPage)adapter.getItem(position)).name);
                startActivity(i);
            }
        });
    }
}

class MainAdapter extends BaseAdapter
{
    private Context context;
    public MainAdapter(Context cont)
    {
        context = cont;
    }

    private FacebookPage[] pages = new FacebookPage[] {
            new FacebookPage("La Gente anda diciendo","https://graph.facebook.com/LaGenteAndaDiciendo/albums?fields=name,id,count"),
            new FacebookPage("La UNLP anda diciendo","https://graph.facebook.com/LaUnlpAndaDiciendo/albums?fields=name,id,count"),
            new FacebookPage("La UNC anda diciendo","https://graph.facebook.com/LaUNCAndaDiciendo/albums?fields=name,id,count"),
            new FacebookPage("La UTN anda diciendo","https://graph.facebook.com/LaUtnAndaDiciendo/albums?fields=name,id,count")};

    @Override
    public int getCount() {
        return pages.length;
    }

    @Override
    public Object getItem(int position) {
        return pages[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = new TextView(context);
        view.setTextSize(28);
        view.setText(pages[position].name);
        return view;
    }
}
