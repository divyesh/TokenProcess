package com.mindblots.tokenprocess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;


public class TokenActivity extends Activity {

    private GridviewAdapter mAdapter;
    private ArrayList<webToken> tokenList;
    private GridView gridView;
    private String responseString="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
        LoadPendingTokens();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.token, menu);
        return true;
    }
    public void postData(String id,String state) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://dymo.herokuapp.com/tokens/"+id+"/"+state);

        try {

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }
    public void LoadPendingTokens() {

        try {
            new HttpAsyncTask().execute("http://dymo.herokuapp.com/tokens.json");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void OpenAlert(View view,String id, String token) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TokenActivity.this);

        alertDialogBuilder.setTitle(this.getTitle()+ " decision");

        alertDialogBuilder.setMessage("Token# "+token);

        // set positive button: Yes message

        alertDialogBuilder.setPositiveButton("Process",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int id) {

                postData(id+"","done");
            }

        });

        // set negative button: No message

        alertDialogBuilder.setNegativeButton("Discard",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int id) {

                // cancel the alert box and put a Toast to the user
                postData(id+"","discard");
                dialog.cancel();

                Toast.makeText(getApplicationContext(), "You chose a negative answer",Toast.LENGTH_LONG).show();

            }

        });


        alertDialogBuilder.setNeutralButton("Later",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int id) {

                dialog.cancel();

            }

        });



        AlertDialog alertDialog = alertDialogBuilder.create();

        // show alert

        alertDialog.show();

    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "= "+result, Toast.LENGTH_LONG).show();
            //responseString=result;
            tokenList = new ArrayList<webToken>();
            mAdapter = new GridviewAdapter(TokenActivity.this, tokenList);

            gridView = (GridView) findViewById(R.id.gridView1);
            gridView.setAdapter(mAdapter);

            // Implement On Item click listener
            gridView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View v, int position,
                                        long arg3) {

                  OpenAlert(v,mAdapter.getItemId(position)+"",mAdapter.getItem(position));
                }
            });
            try {

                JSONArray jsonArray = new JSONArray(result);
//                Log.i(HttpAsyncTask.class.getName(),
//                        "Number of entries " + jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id= jsonObject.getString("id");
                    String token= jsonObject.getString("no");
                    String state= jsonObject.getString("state");
                    String hc=jsonObject.getJSONObject("patient").getString("healthnumber");
                    String url= jsonObject.getString("url");
                    if(!state.equals("completed")) {
                        webToken w = new webToken(id, token, hc, state, url);
                        tokenList.add(w);
                    }
                    Log.i("TOKEN RECEIVED",hc.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
           //etResponse.setText(result);
        }
    }

}

