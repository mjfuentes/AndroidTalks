package com.mjfuentes.androidandadiciendo;

/**
 * Created by matias on 6/07/13.
 */
public class FacebookAlbum {
    public String name;
    public String uri;
    public int cant;
    public FacebookAlbum(String n, String id,int c)
    {
        name = n;
        uri = "https://graph.facebook.com/" + id + "/photos?fields=source,link&limit=500";
        cant = c;
    }
}
