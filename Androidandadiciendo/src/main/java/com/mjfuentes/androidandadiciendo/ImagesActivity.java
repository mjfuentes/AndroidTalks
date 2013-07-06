package com.mjfuentes.androidandadiciendo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

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

public class ImagesActivity extends Activity {

    private String albumUri;
    private FacebookImage[] albumImages;
    private ImagesAdapter adapter;
    private FacebookImage selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        albumUri = getIntent().getStringExtra("uri");
        final TextView index = (TextView)findViewById(R.id.index);
        TextView title = (TextView)findViewById(R.id.title);
        Gallery gallery =(Gallery)findViewById(R.id.gallery);
        title.setText(getIntent().getStringExtra("name"));
        loadImages();
        ((ImageView)findViewById(R.id.share)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage();
            }
        });
        setTitle(getIntent().getStringExtra("pageName"));
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (adapter != null){
                index.setText((position +1) + "/" + adapter.getCount());
                    selectedImage = (FacebookImage)adapter.getItem(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public String getAlbumUri()
    {
        return albumUri;
    }

    private void shareImage()
    {
        if (selectedImage!= null)
        {
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, "Link");
            share.putExtra(Intent.EXTRA_TEXT, selectedImage.shareLink);
            startActivity(Intent.createChooser(share, "Share Image"));
        }
    }

    private void loadImages()
    {
        ImagesLoader loader = new ImagesLoader();
        loader.execute(this);
        ImageLoader.getInstance();
    }

    public FacebookImage[] getImages()
    {
        return albumImages;
    }

    public void setImages(FacebookImage[] images)
    {
        albumImages = images;
        adapter = new ImagesAdapter(this);
        ((Gallery)findViewById(R.id.gallery)).setAdapter(adapter);
        findViewById(R.id.progressImages).setVisibility(View.INVISIBLE);
    }

}

class ImagesLoader extends AsyncTask
{
    private Context context;
    @Override
    protected void onPostExecute(Object o) {
        ((ImagesActivity)context).setImages((FacebookImage[]) o);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            ArrayList<FacebookImage> images = new ArrayList<FacebookImage>();
            context = (Context)params[0];
            String uri = ((ImagesActivity)context).getAlbumUri();
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
                    images.add(new FacebookImage(data.getJSONObject(i).getString("source"),data.getJSONObject(i).getString("link")));
            }
            return images.toArray(new FacebookImage[images.size()]);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

class ImagesAdapter extends BaseAdapter
{
    private final Context context;
    private FacebookImage[] images;
    public ImagesAdapter(Context con)
    {
        context = con;
        images = ((ImagesActivity)context).getImages();
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return images[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout view = (RelativeLayout)inflater.inflate(context.getResources().getLayout(R.layout.image_layout), null);

        final ImageView imageView = (ImageView)view.findViewById(R.id.image);
        final ProgressBar bar = (ProgressBar)view.findViewById(R.id.progressBar);
        ImageLoader.getInstance().loadImage(images[position].uri, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imageView.setImageBitmap(loadedImage);
                bar.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }
}
