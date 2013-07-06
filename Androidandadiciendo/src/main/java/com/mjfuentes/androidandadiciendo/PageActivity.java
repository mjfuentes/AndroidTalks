package com.mjfuentes.androidandadiciendo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PageActivity extends Activity {
    private String pageUri;
    private FacebookAlbum[] pageAlbums;
    private albumsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        pageUri = getIntent().getStringExtra("uri");
        loadAlbums();
        ((ListView)findViewById(R.id.albumsList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(PageActivity.this,ImagesActivity.class);
                i.putExtra("uri",((FacebookAlbum)adapter.getItem(position)).uri);
                i.putExtra("name",((FacebookAlbum)adapter.getItem(position)).name);
                startActivity(i);
            }
        });
    }

    public String getPageUri()
    {
        return pageUri;
    }

    private void loadAlbums()
    {
        AlbumLoader loader = new AlbumLoader();
        loader.execute(this);
    }

    public FacebookAlbum[] getAlbums()
    {
        return pageAlbums;
    }

    public void setAlbums(FacebookAlbum[] albums)
    {
        pageAlbums = albums;
        adapter = new albumsAdapter(this);
        ((ListView)findViewById(R.id.albumsList)).setAdapter(adapter);
    }
    
}

class albumsAdapter extends BaseAdapter
{
    private Context context;
    private FacebookAlbum[] albums;
    public albumsAdapter(Context con)
    {
        context = con;
        albums = ((PageActivity)context).getAlbums();
    }
    @Override
    public int getCount() {
        return albums.length;
    }

    @Override
    public Object getItem(int position) {
        return albums[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = new TextView(context);
        view.setTextSize(28);
        view.setText(albums[position].name);
        return view;
    }
}

class AlbumLoader extends AsyncTask
{
    private Context context;

    @Override
    protected void onPostExecute(Object o) {
        ((PageActivity)context).setAlbums((FacebookAlbum[])o);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            ArrayList<FacebookAlbum> albums = new ArrayList<FacebookAlbum>();
            context = (Context)params[0];
            String uri = ((PageActivity)context).getPageUri();
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(uri);
            HttpResponse response = client.execute(get);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder content = new StringBuilder();
            String line;
            while (null != (line = rd.readLine())) {
                content.append(line);
            }
            JSONObject obj = new JSONObject(content.toString());
            JSONArray data = obj.getJSONArray("data");
            for (int i = 0;i<data.length();i++)
            {
                if (data.getJSONObject(i).getInt("count") > 5)
                {
                    albums.add(new FacebookAlbum(data.getJSONObject(i).getString("name"),data.getJSONObject(i).getString("id")));
                }
            }
            return albums.toArray(new FacebookAlbum[albums.size()]);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
