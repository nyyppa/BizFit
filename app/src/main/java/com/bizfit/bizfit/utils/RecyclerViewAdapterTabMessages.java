package com.bizfit.bizfit.utils;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.activities.MessageActivity;

/**
 *
 */
public class RecyclerViewAdapterTabMessages extends RecyclerView.Adapter
{
    //private User user;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_message_tab_preview, parent, false);
                 ImageView iVResipient=(ImageView)v.findViewById(R.id.iVRecipient);
                //TODO: Tee tämä!
                //hae kirjautuneen käyttäjän keskustelut (jos tarvii)
                //lisää keskustelut listaan, hae keskustelulle other ja vertaa sitä owneriin
                // jos näin, niin luodaan siitä muuttuja ja haetaan sille coachID:n perusteella tiedot
                //ulosta tiedot

                 iVResipient.setImageDrawable(ContextCompat.getDrawable(parent.getContext(), R.drawable.tmp2));

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }


    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            v.getContext().startActivity(new Intent(v.getContext(), MessageActivity.class));
        }
    }
}
