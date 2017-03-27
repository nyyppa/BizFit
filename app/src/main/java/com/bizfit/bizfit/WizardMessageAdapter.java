package com.bizfit.bizfit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bizfit.bizfit.activities.OrderChat;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by iipa on 16.3.2017.
 */

public class WizardMessageAdapter extends BaseAdapter {

        private static final int TYPE_MESSAGE_USER = 0;
        private static final int TYPE_MESSAGE_WIZARD = 1;
        private static final int TYPE_BUTTON = 2;
        private static final int TYPE_MAX_COUNT = TYPE_BUTTON + 1;

        private ArrayList<String> mData = new ArrayList<>();
        private ArrayList<String> mPhaseTagData = new ArrayList<>();
        private ArrayList<String> mAnswerTagData = new ArrayList<>();

        private LayoutInflater mInflater;

        private TreeSet mMessageWizardSet = new TreeSet();
        private TreeSet mMessageUserSet = new TreeSet();
        private TreeSet mButtonSet = new TreeSet();

        private OrderChat wizard;

        public WizardMessageAdapter() {
            // mandatory empty constructor
        }

        public WizardMessageAdapter(View v, OrderChat wizard) {
            this.wizard = wizard;
            Context context = v.getContext();
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addWizardMessage(final String item) {
            mData.add(item);
            mMessageWizardSet.add(mData.size() - 1);
            mPhaseTagData.add("wizmsg");
            mAnswerTagData.add("wizmsg");
            notifyDataSetChanged();
        }

        public void addUserMessage(final String item) {
            mData.add(item);
            mMessageUserSet.add(mData.size() - 1);
            mPhaseTagData.add("usrmsg");
            mAnswerTagData.add("usrmsg");
            notifyDataSetChanged();
        }
        public void addButton(final String item, final String phaseTag, final String answerTag) {
            mData.add(item);
            mButtonSet.add(mData.size() - 1);
            mPhaseTagData.add(phaseTag);
            mAnswerTagData.add(answerTag);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            int viewType = -1;
            if(mMessageWizardSet.contains(position)) {
                viewType = TYPE_MESSAGE_WIZARD;
            } else if(mMessageUserSet.contains(position)) {
                viewType = TYPE_MESSAGE_USER;
            } else {
                viewType = TYPE_BUTTON;
            }
            return viewType;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            int type = getItemViewType(position);
            System.out.println("getView " + position + " " + convertView + " type = " + type);
            final int pos = position;
            if (convertView == null) {
                holder = new ViewHolder();
                switch (type) {
                    case TYPE_MESSAGE_WIZARD:
                        convertView = mInflater.inflate(R.layout.list_item_message_received, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.message);
                        break;
                    case TYPE_MESSAGE_USER:
                        convertView = mInflater.inflate(R.layout.list_item_message_sent, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.message);
                        break;
                    case TYPE_BUTTON:
                        convertView = mInflater.inflate(R.layout.list_item_message_button, null);
                        holder.textView = (TextView) convertView.findViewById(R.id.message_button);
                        holder.textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String phase = mPhaseTagData.get(pos);
                                String answer = mAnswerTagData.get(pos);
                                removeItems(phase, answer, pos);
                                wizard.handleAnswer(answer);
                            }
                        });
                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.textView.setText(mData.get(position));
            holder.textView.setMaxWidth((wizard.getListView().getWidth()/4) * 3);

            return convertView;
        }

        private void removeItems(String phase, String answer, int pos) {

            if(phase.equals("WELCOME")) {
                if(answer.equals("YES")) {
                    // remove answer
                    mData.remove(pos);
                    mButtonSet.remove(pos);
                    mPhaseTagData.remove(pos);
                    mAnswerTagData.remove(pos);

                    // remove option after
                    mData.remove(pos);
                    mButtonSet.remove(pos);
                    mPhaseTagData.remove(pos);
                    mAnswerTagData.remove(pos);
                } else {
                    // remove answer
                    mData.remove(pos);
                    mButtonSet.remove(pos);
                    mPhaseTagData.remove(pos);
                    mAnswerTagData.remove(pos);

                    // remove option after
                    mData.remove(pos-1);
                    mButtonSet.remove(pos-1);
                    mPhaseTagData.remove(pos-1);
                    mAnswerTagData.remove(pos-1);
                }
            } else if(phase.equals("NEED")) {
                if(answer.equals("PROBLEM")) {
                    // remove answer
                    mData.remove(pos);
                    mButtonSet.remove(pos);
                    mPhaseTagData.remove(pos);
                    mAnswerTagData.remove(pos);

                    // remove other option(s)
                    mData.remove(pos);
                    mButtonSet.remove(pos);
                    mPhaseTagData.remove(pos);
                    mAnswerTagData.remove(pos);
                } else {
                    // remove answer
                    mData.remove(pos);
                    mButtonSet.remove(pos);
                    mPhaseTagData.remove(pos);
                    mAnswerTagData.remove(pos);

                    // remove other option(s)
                    mData.remove(pos-1);
                    mButtonSet.remove(pos-1);
                    mPhaseTagData.remove(pos-1);
                    mAnswerTagData.remove(pos-1);
                }
            } else if(phase.equals("SKILL")) {
                if(answer.equals("BEGINNER")) {
                    // remove answer
                    mData.remove(pos);
                    mButtonSet.remove(pos);
                    mPhaseTagData.remove(pos);
                    mAnswerTagData.remove(pos);

                    // remove other option(s)
                    mData.remove(pos);
                    mButtonSet.remove(pos);
                    mPhaseTagData.remove(pos);
                    mAnswerTagData.remove(pos);

                    mData.remove(pos);
                    mButtonSet.remove(pos);
                    mPhaseTagData.remove(pos);
                    mAnswerTagData.remove(pos);

                } else if(answer.equals("INTERMEDIATE")) {
                    // remove answer
                    mData.remove(pos);
                    mButtonSet.remove(pos);
                    mPhaseTagData.remove(pos);
                    mAnswerTagData.remove(pos);

                    // remove other option(s)
                    mData.remove(pos);
                    mButtonSet.remove(pos);
                    mPhaseTagData.remove(pos);
                    mAnswerTagData.remove(pos);

                    mData.remove(pos-1);
                    mButtonSet.remove(pos-1);
                    mPhaseTagData.remove(pos-1);
                    mAnswerTagData.remove(pos-1);

                } else {
                    // remove answer
                    mData.remove(pos);
                    mButtonSet.remove(pos);
                    mPhaseTagData.remove(pos);
                    mAnswerTagData.remove(pos);

                    // remove other option(s)
                    mData.remove(pos-1);
                    mButtonSet.remove(pos-1);
                    mPhaseTagData.remove(pos-1);
                    mAnswerTagData.remove(pos-1);

                    mData.remove(pos-2);
                    mButtonSet.remove(pos-2);
                    mPhaseTagData.remove(pos-2);
                    mAnswerTagData.remove(pos-2);

                }
            } else if(phase.equals("CONFIRM")) {
                if(answer.equals("ACCEPT")) {
                    mData.remove(pos);
                    mButtonSet.remove(pos);
                    mPhaseTagData.remove(pos);
                    mAnswerTagData.remove(pos);
                }
            }

            notifyDataSetChanged();
        }

    public static class ViewHolder {
        public TextView textView;
    }
}
