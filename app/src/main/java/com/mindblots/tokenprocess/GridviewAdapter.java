package com.mindblots.tokenprocess;


import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.media.Image;
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
        return Long.parseLong(tokens.get(position).Id);
    }

    public static class ViewHolder {
        public ImageView imgViewState;
        public TextView txtViewTitle;
        public TextView txtHealthCard;

    }
    public static String formatHealthcard(String number){
        number  =   number.substring(0, number.length()-3) + " " + number.substring(number.length()-3, number.length());
        number  =   number.substring(0,number.length()-7)+" "+number.substring(number.length()-7,number.length());
        number  =   number.substring(0, number.length()-4)+" "+number.substring(number.length()-4, number.length());
        return number;
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
            view.txtHealthCard = (TextView) convertView
                    .findViewById(R.id.healthCardTxt);
            //view.imgViewState=(ImageView)convertView.findViewById(R.id.statusImg);
            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }

        view.txtViewTitle.setText(tokens.get(position).Token+"");
        view.txtHealthCard.setText(formatHealthcard(tokens.get(position).Healthcard));
        if(tokens.get(position).state.equals("time_in"))
        {
            view.txtHealthCard.setTextColor(Color.parseColor("#f04158"));
            //view.imgViewState.setImageResource(R.drawable.time_in);
        }else
            view.txtHealthCard.setTextColor(Color.parseColor("#2E570D"));
            //view.imgViewState.setImageResource(R.drawable.registered);
        return convertView;
    }
}