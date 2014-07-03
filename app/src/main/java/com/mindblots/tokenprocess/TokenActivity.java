package com.mindblots.tokenprocess;

import java.io.IOException;
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
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;


public class TokenActivity extends Activity {

    private GridviewAdapter mAdapter;
    private ArrayList<webToken> tokenList;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
        LoadPendingTokens();

        mAdapter = new GridviewAdapter(this, tokenList);

        // Set custom adapter to gridview
        gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setAdapter(mAdapter);

        // Implement On Item click listener
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long arg3) {

                OpenAlert(v,mAdapter.getItem(position));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.token, menu);
        return true;
    }
    public void LoadPendingTokens() {
        tokenList = new ArrayList<webToken>();


        for (int i=1; i<101;i++) {
            webToken w=new webToken(i,"123456890",false);
           tokenList.add(w);
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
    private void OpenAlert(View view,String token) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TokenActivity.this);

        alertDialogBuilder.setTitle(this.getTitle()+ " decision");

        alertDialogBuilder.setMessage("Token# "+token);

        // set positive button: Yes message

        alertDialogBuilder.setPositiveButton("Process",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int id) {


            }

        });

        // set negative button: No message

        alertDialogBuilder.setNegativeButton("Discard",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int id) {

                // cancel the alert box and put a Toast to the user

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



}
