package com.example.theresia.tas_pmm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {
    //private static String url = "https://archive.org/advancedsearch.php?q=jazz+AND+mediatype:movies+AND+licenseurl:[http://creativecommons.org/a+TO+http://creativecommons.org/z]&fl[]=identifier,title&rows=15&output=json";
    private static String url ="https://archive.org/advancedsearch.php?q=mediatype%3A%22movies%22+AND+title%3A%22jazz%22&fl%5B0%5D=date&fl%5B1%5D=description&fl%5B2%5D=downloads&fl%5B3%5D=identifier&fl%5B4%5D=subject&fl%5B5%5D=title&fl%5B6%5D=year&sort%5B0%5D&sort%5B1%5D&sort%5B2%5D&rows=50&page=1&output=json";
    ListView list;
    String videoURL;
    Intent i;
    Bundle b;
    ProgressDialog pDialog;
    List<videoMain> listVideoMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listVideoMain = new ArrayList<videoMain>();
        //
        new GetVideo().execute();
    }

    public class videoMain {
        String title;
        String identifier;
        String download;
        String videoURL;
    }

    private class GetVideo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONObject response = jsonObj.getJSONObject("response");
                    JSONArray docs = response.getJSONArray("docs");

                    // looping through All Contacts
                    for (int i = 0; i < docs.length(); i++) {
                        JSONObject c = docs.getJSONObject(i);
                        videoMain newVideoMain = new videoMain();
                        newVideoMain.title = c.getString("title");
                        newVideoMain.identifier = c.getString("identifier");
                        newVideoMain.download=c.getString("downloads");
                        listVideoMain.add(newVideoMain);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            for (int j = 0; j < listVideoMain.size(); j++) {
                String jsonStr2 = sh.makeServiceCall("https://archive.org/metadata/" + listVideoMain.get(j).identifier, ServiceHandler.GET);
                if (jsonStr2 != null) {
                    try {
                        JSONObject jsonObj2 = new JSONObject(jsonStr2);
                        JSONArray files = jsonObj2.getJSONArray("files");

                        for (int i = 0; i < files.length(); i++) {
                            JSONObject c = files.getJSONObject(i);
                            if (c.getString("format").contains("MPEG4")) {
                                listVideoMain.get(j).videoURL = "https://archive.org/download/" + listVideoMain.get(j).identifier + "/" + c.getString("name");
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                }
                if (listVideoMain.get(j).videoURL == null) {
                    listVideoMain.set(j, null);
                    listVideoMain.remove(j);
                    j--;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            String tampil = "";
            for (int j = 0; j < listVideoMain.size(); j++) {
                tampil += (listVideoMain.get(j).title + " - " + listVideoMain.get(j).identifier +" - " + listVideoMain.get(j).videoURL);
            }
            //Toast.makeText(MainActivity.this, tampil, Toast.LENGTH_SHORT).show();

            list = (ListView) findViewById(R.id.listVideo);
            String[] title = new String[listVideoMain.size()];
            for (int j = 0; j < listVideoMain.size(); j++) {
                title[j] = listVideoMain.get(j).title;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, title);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    videoURL = listVideoMain.get(position).videoURL;
                    i = new Intent(MainActivity.this, SecondActivity.class);
                    b = new Bundle();
                    b.putString("parse_url", videoURL);
                    b.putString("title", listVideoMain.get(position).title);
                    b.putString("downloads",listVideoMain.get(position).download);
                    i.putExtras(b);
                    startActivity(i);
                }
            });
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }


}
