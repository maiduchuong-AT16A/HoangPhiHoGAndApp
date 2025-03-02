package com.example.managerstaff.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.managerstaff.R;
import com.example.managerstaff.activities.FeedBackActivity;
import com.example.managerstaff.activities.ListChatActivity;
import com.example.managerstaff.activities.ManagerFeedBackActivity;
import com.example.managerstaff.activities.NewsActivity;
import com.example.managerstaff.databinding.FragmentUtilitiesBinding;

public class UtilitiesFragment extends Fragment {

    FragmentUtilitiesBinding binding;
    private int IdUser, IdAdmin;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUtilitiesBinding.inflate(inflater, container, false);

        IdUser=getArguments().getInt("id_user");
        IdAdmin=getArguments().getInt("id_admin");

        if(IdUser==IdAdmin){
            binding.icFeatureFeedback.setImageDrawable(requireContext().getDrawable(R.drawable.icon_email));
            binding.txtTitleFeedback.setText(requireContext().getString(R.string.list_feedback));
        }else{
            binding.icFeatureFeedback.setImageDrawable(requireContext().getDrawable(R.drawable.icon_reply));
            binding.txtTitleFeedback.setText(requireContext().getString(R.string.feedback));
        }

        binding.cvFeatureShowMorePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireActivity(), NewsActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user",IdUser);
                intent.putExtra("position",3);
                intent.putExtra("id_admin",IdAdmin);
                startActivity(intent,bndlanimation);
            }
        });

        binding.cvFeatureFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireActivity(), (IdUser==IdAdmin)? ManagerFeedBackActivity.class:FeedBackActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user",IdUser);
                intent.putExtra("id_admin",IdAdmin);
                intent.putExtra("action", "list_feedback");
                startActivity(intent,bndlanimation);
            }
        });

        binding.cvFeatureShowMoreComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireActivity(), ListChatActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user",IdUser);
                intent.putExtra("id_admin",IdAdmin);
                intent.putExtra("position",3);
                startActivity(intent,bndlanimation);
            }
        });

        return binding.getRoot();
    }
}