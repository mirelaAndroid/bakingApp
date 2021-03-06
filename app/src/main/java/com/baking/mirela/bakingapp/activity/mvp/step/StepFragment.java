package com.baking.mirela.bakingapp.activity.mvp.step;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baking.mirela.bakingapp.GlobalValues;
import com.baking.mirela.bakingapp.R;
import com.baking.mirela.bakingapp.model.Steps;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mirela on 5/6/2017.
 */

public class StepFragment extends Fragment {

    @BindView(R.id.back) Button back;
    @BindView(R.id.next) Button next;
    @BindView(R.id.stepText) TextView description;
    @BindView(R.id.playerView) SimpleExoPlayerView mPlayerView;
    @BindView(R.id.image) ImageView image;

    private ArrayList<Steps> step;
    private AppCompatActivity appCompatActivity;
    private int id;
    private SimpleExoPlayer mExoPlayer;

    public void setSteps(ArrayList<Steps> step, int id) {
        this.step = step;
        GlobalValues.setSteps(step);
        this.id = id;
    }

    public void setAppCompatActivity(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, rootView);
        releasePlayer();
        if (step == null) {
            step = GlobalValues.getStep();
        }

        if (step.size() > id) {
            if(step.get(id).getThumbnailURL() != null && !step.get(id).getThumbnailURL().isEmpty()) {
                image.setVisibility(View.VISIBLE);
                Picasso.with(getContext()).load(step.get(id).getThumbnailURL()).into(image);
            } else {
                image.setVisibility(View.GONE);
            }
            description.setText(step.get(id).getDescription());
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StepFragment stepFragment = new StepFragment();
                    stepFragment.setSteps(step, id + 1);
                    stepFragment.setAppCompatActivity(appCompatActivity);
                    if (GlobalValues.isTwoPane()) {
                        appCompatActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container2, stepFragment).commit();
                    } else {
                        appCompatActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, stepFragment).commit();
                    }

                }
            });
            if (step.size() - 1 == id) {
                next.setVisibility(View.GONE);
            }
        }

        if (step.get(id).getId() > 0) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StepFragment stepFragment = new StepFragment();
                    stepFragment.setSteps(step, id - 1);
                    stepFragment.setAppCompatActivity(appCompatActivity);
                    if (GlobalValues.isTwoPane()) {
                        appCompatActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container2, stepFragment).commit();
                    } else {
                        appCompatActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, stepFragment).commit();
                    }
                }
            });
        } else back.setVisibility(View.GONE);


        // Initialize the player.
        if (!step.get(id).getVideoURL().isEmpty()) {
            initializePlayer(Uri.parse(step.get(id).getVideoURL()));
        } else {
            mPlayerView.setVisibility(View.GONE);
        }
        return rootView;
    }


    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }


    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer == null) return;
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            if (mPlayerView == null) {
                return;
            }
            mPlayerView.setPlayer(mExoPlayer);
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "ClassicalMusicQuiz");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }
}
