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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
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



    public void LoadPendingTokens() {

        try {
            new GetTokenAsyncTask().execute("http://dymo.herokuapp.com/tokens.json");

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
    private void OpenAlert(View view,final String tokenId, String token) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TokenActivity.this);

        alertDialogBuilder.setTitle(this.getTitle()+ " decision");

        alertDialogBuilder.setMessage("Token# "+token);

        // set positive button: Yes message

        alertDialogBuilder.setPositiveButton("Process",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int id) {
                new ProcessTokenAsyncTask().execute(tokenId+"","done");

            }

        });

        // set negative button: No message

        alertDialogBuilder.setNegativeButton("Discard",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int id) {

                // cancel the alert box and put a Toast to the user
                //postData(id+"","discard");
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

    private class ProcessTokenAsyncTask extends AsyncTask<String, Integer, Double>{

        @Override
        protected Double doInBackground(String... params) {
            // TODO Auto-generated method stub
            //Toast.makeText(getApplicationContext(), params[0], Toast.LENGTH_LONG).show();
            Log.i("OHIP Entered",params[0]);
            final String json=postData(params[0],params[1]);

            runOnUiThread(new Runnable() {
                public void run() {
                    webToken w= null;
                    try {
                        w = toToken(json);
                        //ohipActivity.this.tokenNum.setText(w.Token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });

            Log.i("OHIP SENT",params[0]);
            return null;
        }

        protected void onPostExecute(Double result){

            Toast.makeText(getApplicationContext(), "Contacting Server", Toast.LENGTH_LONG).show();
        }


        public String  postData(String id, String state) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://dymo.herokuapp.com/tokens.json");

            try {
                // Add your data
                JSONObject data = new JSONObject();
                data.put("id",id);
                data.put("state",state);
                StringEntity se = new StringEntity(data.toString());

                //sets the post request as the resulting string
                httppost.setEntity(se);
                //sets a request header so the page receving the request
                //will know what to do with it
                httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json");

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String json = reader.readLine();

                return json;
            } catch (ClientProtocolException e) {
                Log.i("OHIP Response ERROR",e.toString());
            } catch (IOException e) {
                Log.i("OHIP Response ERROR",e.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        public  webToken toToken(String json) throws JSONException {

            JSONObject jo =new JSONObject(json);
            String id = jo.getString("id");
            String token = jo.getString("no");
            String state = jo.getString("state");
            String hc = jo.getJSONObject("patient").getString("healthnumber");

            if (!state.equals("completed")) {
                webToken w = new webToken(id, token, hc, state, "");

                return w;
            }
            return null;
        }
    }
    private class GetTokenAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "= "+result, Toast.LENGTH_LONG).show();
            //responseString=result;
            tokenList = new ArrayList<webToken>();


            // Implement On Item click listener

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
                    mAdapter = new GridviewAdapter(TokenActivity.this, tokenList);

                    gridView = (GridView) findViewById(R.id.gridView1);
                    gridView.setAdapter(mAdapter);
                    gridView.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View v, int position,
                                                long arg3) {
                            String idd=mAdapter.getItemId(position)+"";
                            OpenAlert(v,idd,mAdapter.getItem(position));
                        }
                    });
                    //Log.i("TOKEN RECEIVED",hc.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
           //etResponse.setText(result);
        }
    }

}

