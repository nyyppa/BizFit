package com.bizfit.bizfit.utils;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizfit.bizfit.DebugPrinter;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.activities.MessageActivity;
import com.bizfit.bizfit.chat.Conversation;
import com.bizfit.bizfit.scrollCoordinators.EndlessRecyclerOnScrollListener;

import java.util.LinkedList;
import java.util.List;

import static com.bizfit.bizfit.R.id.iVRecipient;
import static com.bizfit.bizfit.User.getContext;

/**
 *
 */
public class RecyclerViewAdapterTabMessages extends RecyclerView.Adapter
{
    private Conversation conversation=null;
    private String recipient =null;
    private ImageView iVRecipient;
    private LinkedList<StoreRow> storeRows;
    private RecyclerViewAdapterCoaches adapter;
    private List<StoreRow> data;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_message_tab_preview, parent, false);

        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.tab_messages_recyclerView);
        LinearLayoutManager mManager = new LinearLayoutManager(getContext());
        adapter = new RecyclerViewAdapterCoaches(storeRows);

        /* mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(adapter);
        //mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mManager)
        {
            @Override
            public void onLoadMore(int current_page) {
                // TODO Request data from server.
            }
        });
        */

                //TODO: Tee tämä!
                showConversationInfo(v, parent);


        return new ViewHolder(v);
    }
    private int getDrawableID(String coach)
    {
        int id=-1;
        switch (coach)
        {
            case Constants.atte_email:
                id=R.drawable.atte;
                break;
            case Constants.jariM_email:
                id=R.drawable.mylly;
                break;
            case Constants.pasi_email:
                id=R.drawable.pasi;
                break;
            case Constants.jari_email:
                id=R.drawable.jartsa;
            default:
                id=R.drawable.tmp2;
                break;

        }
        return R.drawable.mylly;
    }
    // jariJ 20.1.17
    private void showConversationInfo(View v, ViewGroup parent)
    {
        User user= User.getLastUser(new User.UserLoadedListener() {
            @Override
            public void informationUpdated()
            {
            }
        }, null,null);

        List<Conversation> conversations= user.getConversations();
        for(int i=0; i < conversations.size();i++)
        {
            if(conversations!=null && conversations.size() > 0)
            {
                LinkedList<StoreRow.StoreItem> items = new LinkedList<>();

                conversation = conversations.get(i);
                recipient = conversation.getOther();
                items.add(new StoreRow.StoreItem(recipient, null, getDrawableID(recipient), (int) (Math.random()*400), null, null));


                //iVResipient.setImageDrawable(ContextCompat.getDrawable(parent.getContext(), getDrawableID(recipient)));

                TextView tVuName=(TextView) v.findViewById(R.id.tVName);
                String uName = user.getUserName(null, conversation.getOther());
                tVuName.setText(uName);

                conversation.sortConversation();
                if(conversation.getMessages()!=null && conversation.getMessages().size()>0)
                {
                    TextView tVpreview=(TextView) v.findViewById(R.id.tVPreview);
                    tVpreview.setText(conversation.getMessages().get(0).getMessage());
                }
               // storeRows.add(new StoreRow(null, items));
            }

        }


    }
    public static class ScrollableMessages extends RecyclerView.ViewHolder {

        private RecyclerView container;
        private RecyclerViewAdapterStoreRow adapter;

        public ScrollableMessages(View itemView) {
            super(itemView);
            adapter = new RecyclerViewAdapterStoreRow();
            LinearLayoutManager mLayout = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false);
            container = (RecyclerView) itemView.findViewById(R.id.tab_messages_recyclerView);
            container.setLayoutManager(mLayout);
            container.setAdapter(adapter);
            container.setNestedScrollingEnabled(false);

            container.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayout) {
                @Override
                public void onLoadMore(int current_page) {
                    // TODO Request data from server and display a loading
                    // animation.
                }
            });

        }

        /**
         * Changes the look of the view for displaying.
         *
         * @param row Row to display.
         */
        public void prepareForDisplay(StoreRow row) {
            ((RecyclerViewAdapterStoreRow) container.getAdapter()).setData(row.items);
            container.getAdapter().notifyDataSetChanged();
        }
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
