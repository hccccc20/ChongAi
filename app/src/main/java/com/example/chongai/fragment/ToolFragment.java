package com.example.chongai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chongai.Activity.Attacking_recognition;
import com.example.chongai.Activity.Rope_recognition;
import com.example.chongai.Activity.Species_recognition;
import com.example.chongai.Class.Tool;
import com.example.chongai.R;

import java.util.ArrayList;
import java.util.List;

public class ToolFragment extends Fragment {

    private List<Tool> toolList = new ArrayList<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tool_fragment,container,false);
        initTools();
        ImageView attcking_image = (ImageView) view.findViewById(R.id.attack_identify);
        ImageView species_image = (ImageView) view.findViewById(R.id.species_identify);
        ImageView rope_image = (ImageView) view.findViewById(R.id.rope_identity);

        attcking_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getActivity(), Attacking_recognition.class);
                startActivity(intent1);
            }
        });
        species_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getActivity(), Species_recognition.class);
                startActivity(intent2);
            }
        });
        rope_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(getActivity(), Rope_recognition.class);
                startActivity(intent3);
            }
        });
        return view;
    }

    private void initTools() {
        Tool tool1 = new Tool("宠物食品",R.drawable.tool_food);
        toolList.add(tool1);
        Tool tool2 = new Tool("医疗保健",R.drawable.tool_health);
        toolList.add(tool2);
        Tool tool3 = new Tool("宠物店铺",R.drawable.tool_shop);
        toolList.add(tool3);
    //    Tool tool4 = new Tool("宠物用品",R.drawable.tool_);
    //    toolList.add(tool4);
    }
}
