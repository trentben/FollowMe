package com.teamawesome.followme;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.teamawesome.followme.adapters.FriendsAdapter;
import com.teamawesome.followme.util.Friend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView mListView;
    private List<Friend> mFriendsList;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FriendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        //list of hardcoded friends
        mListView = (ListView) view.findViewById(R.id.friends_listView);
        mFriendsList = new ArrayList<>();

        //In the future friends will be retrived from SQL server
        mFriendsList.add(new Friend("Android Jones", 32.988934, -96.771528));
        mFriendsList.add(new Friend("Kevin Bacon", 32.987273, -96.748304));
        mFriendsList.add(new Friend("Sammy Appleseed", 32.989456, -96.750777));
        FriendsAdapter adapter = new FriendsAdapter(getActivity(), R.layout.listitem_friend, mFriendsList);
        mListView.setAdapter(adapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //calls Dialog
                //makeDialog().show();
                Intent i = new Intent(getActivity(), MapsActivity.class);
                i.putExtra(MapsActivity.FRIEND, mFriendsList.get(position));
                startActivity(i);

            }
        });

        DownloadFriendsData downloadFriendsData = new DownloadFriendsData(getActivity());
        downloadFriendsData.execute("http://followme.byethost31.com/getusers.php?user=1&format=json");

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //Dialog box that pops up when you select a friend on the friendsfragment
    public Dialog makeDialog(){
        final Context context = this.getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.pick_follow)
                .setItems(R.array.dialog_array, new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int which) {

                        Log.d("Onclick - ", "" + which);
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



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    private class DownloadFriendsData extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;


        public DownloadFriendsData(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrls) {
            String sUrl = sUrls[0];
            String fileName = "friends.json";

            //Download JSON file
            File outputFile = downloadHelper(sUrl);

            //If the outputFile returned is null, then we'll check the cache for a previous version
            if(outputFile == null)
            {
                File casheFile = new File(context.getCacheDir().getAbsolutePath() + File.separator + fileName);

                if(!casheFile.exists())
                    return "Could not get JSON file";

                outputFile = casheFile;
            }

            //Parse JSON Data
            try {
                mFriendsList = new ArrayList<>();

                Scanner jsonInput = new Scanner(outputFile);
                StringBuilder sBuilder = new StringBuilder();

                while(jsonInput.hasNextLine())
                    sBuilder.append(jsonInput.nextLine());

                String jsonStr = sBuilder.toString();

                JSONObject jsonObj = new JSONObject(jsonStr);

                JSONArray posts = jsonObj.getJSONArray("posts");

                for(int i=0; i < posts.length(); i++)
                {
                    JSONObject s = posts.getJSONObject(i);
                    JSONObject p = s.getJSONObject("post");


                    Friend friend = new Friend("");

                    friend.username = p.getString("username");
                    friend.latitude = p.getDouble("latitude");
                    friend.longitude = p.getDouble("longitude");

                    //downloadHelper(store.storeLogoURL);

                    mFriendsList.add(friend);

                }


            } catch (FileNotFoundException | JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
           // mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            /*mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);*/
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            //mProgressDialog.dismiss();
            if (result != null) {
                //mListener.onStoreListFragInteraction(StoreListFragment.MSG_DOWNLOAD_FAILED);
            }
            else {
                FriendsAdapter adapter = new FriendsAdapter(getActivity(), R.layout.listitem_friend, mFriendsList);
                mListView.setAdapter(adapter);
            }
        }


        private File downloadHelper(String sUrl)
        {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            String fileName = "friends.json";
            File outputFile = new File(context.getCacheDir().getAbsolutePath() + File.separator + fileName);
            try {
                URL url = new URL(sUrl);
                connection = (HttpURLConnection) url.openConnection();

                //Set the connection timeout to 5 sec
                connection.setConnectTimeout(5000);
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("DOWNLOAD", "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage());
                    return null;
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();


                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(outputFile);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();

            }

            return outputFile;
        }

    }

}
