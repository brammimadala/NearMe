package com.lasys.app.nearme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lasys.app.nearme.R;

import static com.lasys.app.nearme.intrface.AppConstants.itemImages;
import static com.lasys.app.nearme.intrface.AppConstants.itemNames;

public class DashBoardAdapter extends BaseAdapter
{
    Context mcontext ;
    LayoutInflater inflter;

    public DashBoardAdapter(Context context)
    {
        mcontext = context ;
        inflter = (LayoutInflater.from(context));
    }
    @Override
    public int getCount()
    {
        return itemNames.length;
    }

    @Override
    public Object getItem(int i)
    {
        return null;
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        view = inflter.inflate(R.layout.grid_style, null);

        TextView txtName =  view.findViewById(R.id.txt_name);
        ImageView imgPic =  view.findViewById(R.id.img_pic);

        txtName.setText(itemNames[i]);
        //imgPic.setImageDrawable(mcontext.getResources().getDrawable(Images[i]));
        imgPic.setImageResource(itemImages[i]);

        return view;
    }
}
