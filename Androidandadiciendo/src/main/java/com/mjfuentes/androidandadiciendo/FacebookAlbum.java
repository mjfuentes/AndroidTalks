package com.mjfuentes.androidandadiciendo;

/**
 * Created by matias on 6/07/13.
 */
public class FacebookAlbum {
    public String name;
    public String uri;

    public FacebookAlbum(String n, String id)
    {
        name = n;
        uri = "https://graph.facebook.com/" + id + "/photos?fields=source,link";
    }
}
