package com.amigos.sachin.AllUsersFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.amigos.sachin.R;


public class LoadingAllUsersFragment extends Fragment {

    Context context;
    ImageView loading_image;

    public LoadingAllUsersFragment() {

    }

    public static LoadingAllUsersFragment newInstance(String param1) {
        LoadingAllUsersFragment fragment = new LoadingAllUsersFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_loading_all_users, container, false);
        context = getActivity();
        loading_image = (ImageView) view.findViewById(R.id.loading_image);

        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        //rotate.setRepeatMode(Animation.RESTART);
        rotate.setRepeatCount(Animation.INFINITE);
        //rotate.setInterpolator(new LinearInterpolator());

        loading_image.startAnimation(rotate);
        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
