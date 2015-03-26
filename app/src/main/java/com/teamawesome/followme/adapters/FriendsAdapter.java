package com.teamawesome.followme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Trent Bennett on 3/24/2015.
 */
public class FriendsAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private List mList;

    public FriendsAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mList = objects;
    }

    @Override
    public int getCount()
    {
        return mList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;

        if(row == null)
        {
            //LayoutInflater inflater = La

        }

        return row;
    }
}
