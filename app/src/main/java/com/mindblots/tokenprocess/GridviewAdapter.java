package com.mindblots.tokenprocess;


import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridviewAdapter extends BaseAdapter {
    private ArrayList<webToken> tokens;
    private Activity activity;

    public GridviewAdapter(Activity activity, ArrayList<webToken> tokenList) {
        super();
        this.tokens = tokenList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return tokens.size();
    }

    @Override
    public String getItem(int position) {
        // TODO Auto-generated method stub
        return tokens.get(position).Token+"";
    }

    public String getHealthCard(int position) {
        // TODO Auto-generated method stub
        return tokens.get(position).Healthcard;
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public static class ViewHolder {
        public ImageView imgViewFlag;
        public TextView txtViewTitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder view;
        LayoutInflater inflator = activity.getLayoutInflater();

        if (convertView == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.token_row, null);

            view.txtViewTitle = (TextView) convertView
                    .findViewById(R.id.tokenTxt);


            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }

        view.txtViewTitle.setText(tokens.get(position).Token+"");

        return convertView;
    }
}