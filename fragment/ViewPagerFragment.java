package com.igenesys.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.igenesys.R;

public class ViewPagerFragment extends Fragment {
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);

        // Assign variable
        recyclerView = view.findViewById(R.id.showTocLegendsRecyclerView);

        // return view
        return view;
    }

}
