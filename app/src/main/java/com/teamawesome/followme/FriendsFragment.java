package com.teamawesome.followme;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.teamawesome.followme.adapters.FriendsAdapter;
import com.teamawesome.followme.util.Friend;

import java.util.ArrayList;
import java.util.List;


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

}
