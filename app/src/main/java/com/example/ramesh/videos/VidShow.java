package com.example.ramesh.videos;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dailymotion.android.player.sdk.PlayerWebView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.AuthPermission;

public class VidShow extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    PlayerWebView pwv;
    String query;
    String dmURLp1="https://api.dailymotion.com/videos?no_premium=1&search=";
    String dmURLp2="&limit=1";

    String you_key="apihere";
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    String ytURLp1="https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&order=rating&q=";
    String ytURLp2="&key=";
    String ytid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        query = bundle.getString("query");
        String ytsearch=ytURLp1+query+ytURLp2+you_key;
        new YtTask().execute(ytsearch);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vid_show);
        pwv=(PlayerWebView) findViewById(R.id.dm_player_web_view);
        youTubeView = (YouTubePlayerView) findViewById(R.id.you_player_view);
    }

    public void ytstart(View view)
    {
        youTubeView.initialize(you_key, this);
    }

    public void dmstart(View view)
    {
        String dmsearch=dmURLp1+query+dmURLp2;
        new DmTask().execute(dmsearch);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
           pwv.onPause();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            pwv.onResume();
        }
    }
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(ytid);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(you_key, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    private class DmTask extends AsyncTask<String,Void,Void> {
        JSONObject jobj;
        StringBuffer stringBuffer;

        @Override
        protected Void doInBackground(String... params) {
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(params[0]);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                String str = stringBuffer.toString();
                jobj = new JSONObject(str.substring(str.indexOf("{"), str.lastIndexOf("}") + 1));
            } catch (Exception ex) {
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            try {
                JSONArray jlist = jobj.getJSONArray("list");
                String id=jlist.getJSONObject(0).getString("id");
                Map<String, String> playerParams = new HashMap<>();
                playerParams.put("key", "value");
                pwv.load(id, playerParams);
            }
            catch (JSONException e){}
        }
    }

    private class YtTask extends AsyncTask<String ,Void,Void>
    {

        JSONObject jobj;
        StringBuffer stringBuffer;

        @Override
        protected Void doInBackground(String... params) {
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(params[0]);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                String str = stringBuffer.toString();
                jobj = new JSONObject(str.substring(str.indexOf("{"), str.lastIndexOf("}") + 1));
            } catch (Exception ex) {
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            try {
                JSONArray jitems = jobj.getJSONArray("items");
                ytid = jitems.getJSONObject(0).getJSONObject("id").getString("videoId");
                //Toast.makeText(getApplicationContext(), ytid, Toast.LENGTH_LONG).show();

            }
            catch (JSONException e){
                Toast.makeText(getApplicationContext(),"youtube exception", Toast.LENGTH_LONG).show();
            }
        }
    }


}
