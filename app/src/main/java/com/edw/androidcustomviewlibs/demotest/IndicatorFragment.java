package com.edw.androidcustomviewlibs.demotest;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.edw.androidcustomviewlibs.R;



public class IndicatorFragment extends Fragment {

    private TextView textView;

    public static IndicatorFragment getInstance(String text) {
        IndicatorFragment fragment = new IndicatorFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", text);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_indicatior, container, false);
        textView = inflate.findViewById(R.id.tvff);
        assert getArguments() != null;
        String title = getArguments().getString("title", "啥都没有~~");
        textView.setText(title);
        return inflate;
    }


}