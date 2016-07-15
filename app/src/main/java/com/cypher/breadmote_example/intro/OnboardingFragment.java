package com.cypher.breadmote_example.intro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cypher.breadmote_example.R;

/**
 * Created by scypher on 2/9/16.
 */
public class OnboardingFragment extends Fragment {

    public static final int NUMBER_FRAGMENTS = 6;
    private static final String POSITION = "position";
    private int position;

    public static OnboardingFragment getInstance(int position) {
        OnboardingFragment onboardingFragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        onboardingFragment.setArguments(args);

        return onboardingFragment;
    }

    private static int getLayout(int position) {
        switch (position) {
            case 0:
                return R.layout.tutorial_1;
            case 1:
                return R.layout.tutorial_2;
            case 2:
                return R.layout.tutorial_3;
            case 3:
                return R.layout.tutorial_4;
            case 4:
                return R.layout.tutorial_5;
            case 5:
                return R.layout.tutorial_6;
            default:
                throw new RuntimeException("Invalid position: " + position);
        }
    }

    public void onCreate(Bundle savedIntanceState) {
        super.onCreate(savedIntanceState);
        this.position = getArguments().getInt(POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayout(position), container, false);
    }
}
