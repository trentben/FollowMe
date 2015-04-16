package com.teamawesome.followme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.teamawesome.followme.adapters.FriendsAdapter;

import java.util.ArrayList;
import java.util.List;



public class FriendsActivity extends ActionBarActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_friends);


        mListView = (ListView) findViewById(R.id.friends_listView);
        List<String> list = new ArrayList<String>();
        list.add("Android Jones");
        list.add("Kevin Bacon");
        list.add("Jonny Appleseed");
        FriendsAdapter adapter = new FriendsAdapter(this, R.layout.listitem_friend, list);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                makeDialog().show();
            }
        });
    }
    //Dialog box that pops up when you select a friend on the friendsactivity
    public Dialog makeDialog(){

        final Context context = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pick_follow)
                .setItems(R.array.dialog_array, new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int which) {

                        Log.d("Onclick - ", ""+which);
                        //to follow
                        if (which == 0){

                            Log.d("Onclick - Btn 1", ""+which);
                            Intent i = new Intent(context, MapsActivity.class);
                            startActivity(i);
                            dialog.dismiss();
                        }
                        //to lead
                        if (which == 1){

                            Intent i = new Intent(context, MapsActivity.class);
                            startActivity(i);
                            dialog.dismiss();

                        }
                        //cancel
                        if (which==2) dialog.dismiss();





                    }
                });
        return builder.create();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
