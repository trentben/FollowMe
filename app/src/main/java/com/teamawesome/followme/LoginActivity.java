/**
 * LoginActivity
 * Created by Trent and Alex
 * This activity will only display on launch if a user has not logged in before.
 */

package com.teamawesome.followme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;


public class LoginActivity extends ActionBarActivity {

    public static String USER_FILE = "user_info.txt";

    private EditText mUserNameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        File userFile = new File(getFilesDir(), USER_FILE);

        if(userFile.exists()){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }

        mUserNameEdit = (EditText) findViewById(R.id.username_editText);

    }

    public void createAccountClicked(View view){

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(USER_FILE, Context.MODE_PRIVATE);
            outputStream.write(mUserNameEdit.getText().toString().trim().getBytes());
            outputStream.close();

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
