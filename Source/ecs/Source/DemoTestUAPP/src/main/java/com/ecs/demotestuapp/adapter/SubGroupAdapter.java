package com.ecs.demotestuapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ecs.demotestuapp.R;
import com.ecs.demotestuapp.activity.InputActivity;
import com.ecs.demotestuapp.jsonmodel.SubgroupItem;

import java.util.List;

public class SubGroupAdapter extends RecyclerView.Adapter<SubGroupAdapter.ViewHolder> {

    private List<SubgroupItem> subgroupItems;
    private final Context context;

    static final int TYPE_SEPARATOR = 1;
    static final int TYPE_ITEM = 0;

    // RecyclerView recyclerView;
    public SubGroupAdapter(List<SubgroupItem> subgroupItems, Context context) {
        this.subgroupItems = subgroupItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_recycler, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SubgroupItem subgroupItem = subgroupItems.get(position);
        holder.textView.setText(subgroupItem.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoInputActivity(subgroupItem);
            }
        });
    }

    private void gotoInputActivity(SubgroupItem subgroupItem) {
        Intent intent = new Intent(context, InputActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("sub_group",subgroupItem);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return subgroupItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public  View itemView ;

        public ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.textView = (TextView) itemView.findViewById(R.id.tv_item);
        }
    }
}