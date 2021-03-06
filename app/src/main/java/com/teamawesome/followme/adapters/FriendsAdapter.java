package com.teamawesome.followme.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teamawesome.followme.R;
import com.teamawesome.followme.util.Friend;

import java.util.List;

/**
 * Created by Trent Bennett on 3/24/2015.
 */
public class FriendsAdapter extends ArrayAdapter<Friend> {
    private Context mContext;
    private int mLayoutResId;
    private List<Friend> mList;

    public FriendsAdapter(Context context, int resource, List<Friend> objects) {
        super(context, resource, objects);
        mContext = context;
        mList = objects;
        mLayoutResId = resource;
    }

    @Override
    public int getCount()
    {
        return mList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;

        Holder holder = null;

        if(row == null)
        {
            holder = new Holder();

            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResId, parent, false);

            holder.name =  (TextView) row.findViewById(R.id.li_friend_name_tv);

            row.setTag(holder);
        }
        else
        {
            holder = (Holder) row.getTag();
        }

        holder.name.setText(mList.get(position).username);

        return row;
    }

    static class Holder{
        TextView name;
    }
}

