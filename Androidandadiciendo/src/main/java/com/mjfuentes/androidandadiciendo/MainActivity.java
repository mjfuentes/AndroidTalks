package com.mjfuentes.androidandadiciendo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
            new FacebookPage("La Gente anda diciendo","https://graph.facebook.com/LaGenteAndaDiciendo/albums?fields=name,id,count&limit=10","/LaGenteAndaDiciendo",R.drawable.gente_icon),
            new FacebookPage("La UNLP anda diciendo","https://graph.facebook.com/LaUnlpAndaDiciendo/albums?fields=name,id,count&limit=10","/LaUnlpAndaDiciendo",R.drawable.unlp_icon),
            new FacebookPage("La UNC anda diciendo","https://graph.facebook.com/LaUNCAndaDiciendo/albums?fields=name,id,count&limit=10","/LaUNCAndaDiciendo",R.drawable.unc_icon),
            new FacebookPage("La UTN anda diciendo","https://graph.facebook.com/LaUtnAndaDiciendo/albums?fields=name,id,count&limit=10","/LaUtnAndaDiciendo",R.drawable.utn_icon)};

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
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout view = (RelativeLayout) inflater.inflate(context.getResources().getLayout(R.layout.imagelist_item),null);
        TextView title = (TextView)view.findViewById(R.id.title);
        TextView subTitle = (TextView)view.findViewById(R.id.subTitle);
        ImageView icon = (ImageView)view.findViewById(R.id.page_icon);
        title.setText(pages[position].name);
        subTitle.setText(pages[position].extra);
        icon.setImageResource(pages[position].resId);

        return view;
    }
}
