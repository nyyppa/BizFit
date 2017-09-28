package com.bizfit.release.fragments;

import android.support.v4.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizfit.release.Coach;
import com.bizfit.release.MyApplication;
import com.bizfit.release.R;
import com.bizfit.release.activities.MainPage;
import com.bizfit.release.utils.Constants;
import com.bizfit.release.utils.Utils;

import java.util.ArrayList;

/**
 * Created by iipa on 20.4.2017.
 */

public class TabCoaches2 extends Fragment {

    ArrayList<Coach> coaches;

    public TabCoaches2() {
        // Required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_coaches_container, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // create coach list

        coaches = initCoaches();

        Coach coach = coaches.get(0);

        View card = getActivity().findViewById(R.id.include1);
        card.setOnClickListener(getOnClickListener());

        TextView name = (TextView) card.findViewById(R.id.coach_item_name);
        name.setText(coach.getName());

        ImageView image = (ImageView) card.findViewById(R.id.iVCoach);
        image.setImageDrawable(coach.image);

        TextView info = (TextView) card.findViewById(R.id.coach_item_desc);
        info.setText(coach.getInfo());

        coach = coaches.get(1);

        card = getActivity().findViewById(R.id.include2);
        card.setOnClickListener(getOnClickListener());
        name = (TextView) card.findViewById(R.id.coach_item_name);
        name.setText(coach.getName());
        image = (ImageView) card.findViewById(R.id.iVCoach);
        image.setImageDrawable(coach.image);
        info = (TextView) card.findViewById(R.id.coach_item_desc);
        info.setText(coach.getInfo());

        coach = coaches.get(2);

        card = getActivity().findViewById(R.id.include3);
        card.setOnClickListener(getOnClickListener());
        name = (TextView) card.findViewById(R.id.coach_item_name);
        name.setText(coach.getName());
        image = (ImageView) card.findViewById(R.id.iVCoach);
        image.setImageDrawable(coach.image);
        info = (TextView) card.findViewById(R.id.coach_item_desc);
        info.setText(coach.getInfo());

        /*

        coach = coaches.get(3);

        card = getActivity().findViewById(R.id.include4);
        card.setOnClickListener(getOnClickListener());
        name = (TextView) card.findViewById(R.id.coach_item_name);
        name.setText(coach.getName());
        image = (ImageView) card.findViewById(R.id.iVCoach);
        image.setImageDrawable(coach.image);
        info = (TextView) card.findViewById(R.id.coach_item_desc);
        info.setText(coach.getInfo());

        */

    }

    public ArrayList<Coach> initCoaches() {

        ArrayList<Coach> coaches = new ArrayList<>();

        // add coaches to array

        for(int j=0; j<3; j++) {
            String coach = "";

            if (j == 0) {
                coach = Constants.jari_email;
            }
            if (j == 1) {
                coach = Constants.atte_email;
            }
            if (j == 2) {
                coach = Constants.tapani_email;
            }

            Drawable img = ContextCompat.getDrawable(MyApplication.getContext(), Utils.getDrawableID(coach));

            coaches.add(new Coach(Utils.getCoachName(coach), img, Utils.getDrawableID(coach), (int) (Math.random() * 400), coach, "044123", Utils.getDesc(coach)));
        }

        return coaches;

    }

    public View.OnClickListener getOnClickListener() {

        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View card1 = getActivity().findViewById(R.id.include1);
                View card2 = getActivity().findViewById(R.id.include2);
                View card3 = getActivity().findViewById(R.id.include3);
                //View card4 = getActivity().findViewById(R.id.include4);

                MainPage mp = (MainPage) v.getContext();

                if(v == card1) {
                    mp.coachSelected(coaches.get(0));
                } else if(v == card2) {
                    mp.coachSelected(coaches.get(1));
                } else if(v == card3) {
                    mp.coachSelected(coaches.get(2));
                }
                /*
                else if(v == card4) {
                    mp.coachSelected(coaches.get(3));
                }
                */
            }
        };

        return ocl;

    }

}
