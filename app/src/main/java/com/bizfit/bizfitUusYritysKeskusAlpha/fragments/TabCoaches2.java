package com.bizfit.bizfitUusYritysKeskusAlpha.fragments;

import android.support.v4.app.Fragment;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizfit.bizfitUusYritysKeskusAlpha.Coach;
import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.activities.MainPage;

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

        // define profile images

        Drawable jari = ResourcesCompat.getDrawable(getResources(), R.drawable.jartsa, null);
        Drawable pasi = ResourcesCompat.getDrawable(getResources(), R.drawable.pasi, null);
        Drawable atte = ResourcesCompat.getDrawable(getResources(), R.drawable.atte, null);
        Drawable mylly = ResourcesCompat.getDrawable(getResources(), R.drawable.mylly, null);
        final TypedArray worksIds = getResources().obtainTypedArray(R.array.work_imgs);

        // add coaches to array

        for(int j=0; j<4; j++) {

            int imageID = worksIds.getResourceId(j, -1);

            if (imageID == -1) {
                coaches.add(new Coach("Jari Myllymäki", mylly, imageID, (int) (Math.random() * 400), "jari.myllymaki@gmail.com", "0447358220"));
            }
            if (j == 0) {
                coaches.add(new Coach("Jari Myllymäki", mylly, imageID, (int) (Math.random() * 400), "jari.myllymaki@gmail.com", "0447358220"));
            }
            if (j == 1) {
                coaches.add(new Coach("Pasi Ojanen", pasi, imageID, (int) (Math.random() * 400), "pasojan@gmail.com", "0407283115"));
            }
            if (j == 2) {
                coaches.add(new Coach("Atte Yliverronen", atte, imageID, (int) (Math.random() * 400), "atte.yliverronen@gmail.com", null));
            }
            if (j == 3) {
                coaches.add(new Coach("Jari Järvenpää", jari, imageID, (int) (Math.random() * 400), "jari.k4rita@gmail.com", null));
            }
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
