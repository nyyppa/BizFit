package com.bizfit.bizfitUusYritysKeskusAlpha.RecyclerViews;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.User;
import com.bizfit.bizfitUusYritysKeskusAlpha.activities.MessageActivity;
import com.bizfit.bizfitUusYritysKeskusAlpha.chat.Conversation;
import com.bizfit.bizfitUusYritysKeskusAlpha.scrollCoordinators.EndlessRecyclerOnScrollListener;
import com.bizfit.bizfitUusYritysKeskusAlpha.utils.StoreRow;

import java.util.LinkedList;
import java.util.List;

import static com.bizfit.bizfitUusYritysKeskusAlpha.utils.Utils.getDrawableID;

/**
 *
 */
public class RecyclerViewAdapterTabMessages extends RecyclerView.Adapter
{
    //CURRENTLY NOT IN USE
    private Conversation conversation=null;
    private String recipient =null;
    private ImageView iVRecipient;
    private LinkedList<StoreRow> storeRows;
    private RecyclerViewAdapterCoaches adapter;
    private List<Conversation> data;

    public RecyclerViewAdapterTabMessages(List<Conversation> data)
    {
        this.data=data;
    }
    private RecyclerView parent;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        this.parent = (RecyclerView) parent;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_message_tab_preview, parent, false);



                //TODO: Tee tämä!
                showConversationInfo(v, parent);


        return new ViewHolder(v);
    }

    // jariJ 20.1.17
    private void showConversationInfo(View v, ViewGroup parent)
    {
        User user= User.getLastUser(null, null,null);

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
           // adapter = new RecyclerViewAdapterTabMessages();
            LinearLayoutManager mLayout = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false);
            container = (RecyclerView) itemView.findViewById(R.id.tab_messages_recyclerView);
            container.setLayoutManager(mLayout);
            //container.setAdapter(adapter);
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
            //((RecyclerViewAdapterTabMessages) container.getAdapter()).setData(row.items);
            container.getAdapter().notifyDataSetChanged();
        }
    }




    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
//        ((ScrollableMessages) holder).prepareForDisplay(null);
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
            Intent intent = new Intent();
            intent.putExtra("coachID", "atte.yliverronen@gmail.com");
            intent.putExtra("coachName", "Pentti Holappa");
            intent.putExtra("imgID", getDrawableID(recipient));
            MessageActivity.startChat(v.getContext(), intent);
        }
    }
}
