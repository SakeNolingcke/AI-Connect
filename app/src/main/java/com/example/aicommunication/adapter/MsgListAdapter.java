package com.example.aicommunication.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.Target;
import com.example.aicommunication.Msg;
import com.example.aicommunication.R;

import java.util.List;

import io.noties.markwon.Markwon;
import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.glide.GlideImagesPlugin;

public class MsgListAdapter extends RecyclerView.Adapter<MsgListAdapter.ViewHolder> {
    Context context;
    private final List<Msg> lists;
    Markwon markwon;

    public MsgListAdapter(Context context, List<Msg> lists) {
        this.context = context;
        this.lists = lists;
        markwon = Markwon.builder(context)
                .usePlugin(GlideImagesPlugin.create(context))
                .usePlugin(GlideImagesPlugin.create(Glide.with(context)))
                .usePlugin(GlideImagesPlugin.create(new GlideImagesPlugin.GlideStore() {
                    @Override
                    public void cancel(@NonNull Target<?> target) {
                        Glide.with(context).clear(target);
                    }

                    @NonNull
                    @Override
                    public RequestBuilder<Drawable> load(@NonNull AsyncDrawable drawable) {
                        return Glide.with(context).load(drawable.getDestination());
                    }
                }))
                .build();
    }

    @NonNull
    @Override
    public MsgListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgListAdapter.ViewHolder holder, int position) {
        Msg msg = lists.get(position);
        if (position == 0) {
            if (msg.getOwner() == Msg.OWNER_AI) {
                holder.vg_ai.setVisibility(View.VISIBLE);
                holder.avatar_img_ai.setImageResource(msg.getAvatar_img());
                markwon.setMarkdown(holder.msg_tv_ai, msg.getContent());
                holder.vg_user.setVisibility(View.GONE);
            } else {
                holder.vg_ai.setVisibility(View.GONE);
                holder.vg_user.setVisibility(View.GONE);
            }
        } else {
            if (msg.getOwner() == Msg.OWNER_AI) {
                holder.vg_ai.setVisibility(View.VISIBLE);
                holder.avatar_img_ai.setImageResource(msg.getAvatar_img());
                markwon.setMarkdown(holder.msg_tv_ai, msg.getContent());
                holder.vg_user.setVisibility(View.GONE);
            } else if (msg.getOwner() == Msg.OWNER_USER) {
                holder.vg_user.setVisibility(View.VISIBLE);
                holder.avatar_img_user.setImageResource(msg.getAvatar_img());
                markwon.setMarkdown(holder.msg_tv_user, msg.getContent());
                holder.vg_ai.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout vg_ai;
        FrameLayout vg_user;
        ImageView avatar_img_ai, avatar_img_user;
        TextView msg_tv_ai, msg_tv_user;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar_img_ai = itemView.findViewById(R.id.avatar_img_ai);
            msg_tv_ai = itemView.findViewById(R.id.msg_tv_ai);
            avatar_img_user = itemView.findViewById(R.id.avatar_img_user);
            msg_tv_user = itemView.findViewById(R.id.msg_tv_user);
            vg_ai = itemView.findViewById(R.id.ll_ai);
            vg_user = itemView.findViewById(R.id.fl_user);
        }
    }
}