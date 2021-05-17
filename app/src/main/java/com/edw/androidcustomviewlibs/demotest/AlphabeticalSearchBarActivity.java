package com.edw.androidcustomviewlibs.demotest;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.edw.androidcustomviewlibs.R;
import com.edw.androidcustomviewlibs.widget.AlphabeticalSearchBar;

/**
 * **************************************************************************************************
 * Project Name:    AndroidCustomViewLibs
 * <p>
 * Date:            2021-05-17
 * <p>
 * Author：         EdwardWMD
 * <p>
 * Github:          https://github.com/Edwardwmd
 * <p>
 * Blog:            https://edwardwmd.github.io/
 * <p>
 * Description：    ToDo
 * <p>
 * **************************************************************************************************
 */
public class AlphabeticalSearchBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alphabetical_searchbar);
        AlphabeticalSearchBar alphabeticalSearchBar = findViewById(R.id.asbView);
//        RecyclerView mRecy = findViewById(R.id.recy);
        TextView selectedView = findViewById(R.id.selectedLetter);

        alphabeticalSearchBar.setUpdateSelectedListener((item, isActionUp) -> {
            if (isActionUp) {
                if (selectedView.getVisibility() == View.GONE) {
                    return;
                }
                Animation am = new AlphaAnimation(1F, 0F);
                am.setDuration(400);
                selectedView.setAnimation(am);
                selectedView.setVisibility(View.GONE);
            } else {
                selectedView.setVisibility(View.VISIBLE);

            }
            selectedView.setText(item);

        });



    }
}
