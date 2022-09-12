package com.example.chongai.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chongai.Class.Tool;
import com.example.chongai.R;

import java.util.List;

public class ToolAdapter extends RecyclerView.Adapter<ToolAdapter.ViewHolder> {

    private List<Tool> mtoolList;

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.tool_photo);
            textView = (TextView) itemView.findViewById(R.id.tool_title);
        }
    }
    public ToolAdapter(List<Tool> toolList) {
        mtoolList = toolList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tool_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tool tool = mtoolList.get(position);
        holder.imageView.setImageResource(tool.getImageId());
        holder.textView.setText(tool.getTitle());
    }

    @Override
    public int getItemCount() {
        return mtoolList.size();
    }


}
