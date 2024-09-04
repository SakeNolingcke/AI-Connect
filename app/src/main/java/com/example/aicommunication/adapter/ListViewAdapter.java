package com.example.aicommunication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.aicommunication.DialogueList;
import com.example.aicommunication.R;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<DialogueList> {
    private final View.OnClickListener delete;

    public ListViewAdapter(@NonNull Context context, int resource
            , @NonNull List<DialogueList> objects,View.OnClickListener delete) {
        super(context, resource, objects);
        this.delete = delete;
    }

    @NonNull
    @Override
    @SuppressLint("ViewHolder")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DialogueList dialogueList = getItem(position);
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        if (dialogueList != null) {
            holder.dialogue_name.setText(dialogueList.getDialogue_name());
            holder.tokens_used.setText(dialogueList.getTokens_used());
            holder.delete_bt.setOnClickListener(delete);
            holder.delete_bt.setTag(position);
        }
        return view;
    }


    static class ViewHolder {
        TextView dialogue_name, tokens_used;
        ImageButton delete_bt;

        public ViewHolder(View itemView) {
            dialogue_name = itemView.findViewById(R.id.dia_name_tv);
            tokens_used = itemView.findViewById(R.id.tokens_tv);
            delete_bt = itemView.findViewById(R.id.delete_img_bt);
        }
    }

}