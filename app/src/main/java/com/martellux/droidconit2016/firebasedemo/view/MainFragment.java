package com.martellux.droidconit2016.firebasedemo.view;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.FirebaseError;
import com.martellux.droidconit2016.firebasedemo.R;
import com.martellux.droidconit2016.firebasedemo.presenter.MainFragmentPresenter;
import com.martellux.droidconit2016.firebasedemo.view.protocol.demo.IDemoView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements IDemoView {

    /**
     * The presenter reference
     */
    private MainFragmentPresenter mMainFragmentPresenter;
    /**
     * Date formatter
     */
    private final SimpleDateFormat mSDF = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY);
    /**
     * Views references
     */
    @Bind(R.id.tv_status) TextView mTvStatus;
    @Bind(R.id.tv_output) TextView mTvOutput;
    @Bind(R.id.tv_red) TextView mTvRed;
    @Bind(R.id.tv_green) TextView mTvGreen;
    @Bind(R.id.tv_blue) TextView mTvBlue;
    @Bind(R.id.tv_gray) TextView mTvGray;
    @Bind(R.id.tv_connected_users) TextView mTvConnectedUsers;
    /**
     * Hanlder reference
     */
    private Handler mHandler = new Handler();

    /**
     *
     */
    @OnClick(R.id.tv_red)
    protected void doRedSelection() {
        increaseSelection("red");
    }

    /**
     *
     */
    @OnClick(R.id.tv_green)
    protected void doGreenSelection() {
        increaseSelection("green");
    }

    /**
     *
     */
    @OnClick(R.id.tv_blue)
    protected void doBlueSelection() {
        increaseSelection("blue");
    }

    /**
     *
     */
    @OnClick(R.id.tv_gray)
    protected void doGraySelection() {
        increaseSelection("gray");
    }

    /**
     *
     * @param selectionColor
     */
    private void increaseSelection(String selectionColor) {
        mMainFragmentPresenter.onUserSelectionColor(selectionColor);
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mMainFragmentPresenter = new MainFragmentPresenter(this);
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    /**
     *
     */
    @Override
    public void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
        mMainFragmentPresenter.onViewDestroyed();
    }

    /**
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mMainFragmentPresenter.onViewCreated();
    }

    /**
     *
     */
    @Override
    public void onStart() {
        super.onStart();
        mMainFragmentPresenter.onViewShown();
    }

    /**
     *
     */
    @Override
    public void onStop() {
        super.onStop();
        mMainFragmentPresenter.onViewHidden();
    }

    /**
     *
     * @param colors
     * @param color
     * @return
     */
    private int readColorCounter(Map<String, Object> colors, String color) {
        if(colors.containsKey(color)) {
            return (int) colors.get(color);
        }
        return 0;
    }

    /**
     *
     * @param connectedUsers
     */
    @Override
    public void setConnectedUsers(int connectedUsers) {
        mTvConnectedUsers.setText(getString(R.string.demo_connected_users, connectedUsers));
    }

    /**
     *
     * @param status
     * @param currentTime
     * @param startTime
     * @param endTime
     */
    @Override
    public void setDemoDates(int status, long currentTime, long startTime, long endTime) {
        switch (status) {
            case MainFragmentPresenter.ScheduleStatus.TO_START:
                mTvStatus.setText(getString(R.string.demo_status_start_at, mSDF.format(new Date(startTime))));
                setColorsEnabled(false);
                break;

            case MainFragmentPresenter.ScheduleStatus.LIVE:
                mTvStatus.setText(getString(R.string.demo_status_live));
                setColorsEnabled(true);
                break;

            case MainFragmentPresenter.ScheduleStatus.FINISHED:
                mTvStatus.setText(getString(R.string.demo_status_finished, mSDF.format(new Date(startTime))));
                setColorsEnabled(false);
                break;
        }
    }

    /**
     *
     * @param enabled
     */
    private void setColorsEnabled(boolean enabled) {
        mTvRed.setEnabled(enabled);
        mTvGreen.setEnabled(enabled);
        mTvBlue.setEnabled(enabled);
        mTvGray.setEnabled(enabled);
    }

    /**
     *
     * @param output
     */
    @Override
    public void setDemoOutput(final Map<String, Object> output) {
        if(output != null && output.size() > 0) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    final int redCounter = readColorCounter(output, "red");
                    final int greenCounter = readColorCounter(output, "green");
                    final int blueCounter = readColorCounter(output, "blue");
                    final int grayCounter = readColorCounter(output, "gray");
                    final int total = redCounter + greenCounter + blueCounter + grayCounter;

                    if(total != 0) {
                        int r = ((Color.red(Color.RED) * redCounter) + (Color.red(Color.GREEN) * greenCounter) + (Color.red(Color.BLUE) * blueCounter) + (Color.red(Color.GRAY) * grayCounter)) / total;
                        int g = ((Color.green(Color.RED) * greenCounter) + (Color.green(Color.GREEN) * greenCounter) + (Color.green(Color.BLUE) * blueCounter) + (Color.green(Color.GRAY) * grayCounter)) / total;
                        int b = ((Color.blue(Color.RED) * redCounter) + (Color.blue(Color.GREEN) * greenCounter) + (Color.blue(Color.BLUE) * blueCounter) + (Color.blue(Color.GRAY) * grayCounter)) / total;
                        mTvOutput.setTextColor(Color.rgb(r, g, b));

                        mTvRed.setText(String.valueOf(redCounter));
                        mTvGreen.setText(String.valueOf(greenCounter));
                        mTvBlue.setText(String.valueOf(blueCounter));
                        mTvGray.setText(String.valueOf(grayCounter));

                    } else {
                        mTvOutput.setTextColor(Color.BLACK);
                    }
                }
            });
        }
    }

    /**
     *
     * @param firebaseError
     */
    @Override
    public void setFirebaseError(FirebaseError firebaseError) {
        if(getActivity() != null) {
            Toast.makeText(getActivity(), firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
