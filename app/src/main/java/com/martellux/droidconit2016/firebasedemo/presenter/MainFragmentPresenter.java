package com.martellux.droidconit2016.firebasedemo.presenter;

import android.os.Handler;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.martellux.droidconit2016.firebasedemo.Constants;
import com.martellux.droidconit2016.firebasedemo.view.protocol.demo.IDemoView;
import com.martellux.droidconit2016.firebasedemo.view.protocol.demo.IDemoViewLifecycle;

import java.util.Map;

/**
 * Created by alessandromartellucci on 27/02/16.
 */
public class MainFragmentPresenter implements IDemoViewLifecycle {

    /**
     * Schedules types
     */
    public final class ScheduleStatus {
        public static final int TO_START = 0;
        public static final int LIVE = 1;
        public static final int FINISHED = 2;
    }

    /**
     * Firebase reference
     */
    private final Firebase mFirebaseRootRef = new Firebase(Constants.FIREBASE_ROOT);
    /**
     * View reference
     */
    private IDemoView mView;
    /**
     * Talk times & info
     */
    private long mCurrentTime;
    private long mStartTime;
    private long mEndtime;
    private int mAttendantCounter;
    private boolean mEnabled;

    /**
     *
     * @param view
     */
    public MainFragmentPresenter(IDemoView view) {
        mView = view;
    }

    /**
     *
     */
    private void addMeToConnectedUser() {
        mFirebaseRootRef.child("status/connectedUsers").runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mAttendantCounter = 1;
                } else {
                    mAttendantCounter = mutableData.getValue(Integer.class) + 1;
                }
                mutableData.setValue(mAttendantCounter);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                if (mView != null) {
                    if (firebaseError != null) {
                        mView.setFirebaseError(firebaseError);
                        mAttendantCounter = 0;

                    } else {
                        mAttendantCounter = dataSnapshot.getValue(Integer.class);
                    }
                    mView.setConnectedUsers(mAttendantCounter);
                    startListeningForConnectedUsers();
                }
            }
        }, false);
    }

    /**
     *
     * @return
     */
    private int currentStatus() {
        if(mCurrentTime < mStartTime) {
            return ScheduleStatus.TO_START;
        } else if(mCurrentTime >= mStartTime && mCurrentTime <= mEndtime) {
            return ScheduleStatus.LIVE;
        } else if(mCurrentTime > mEndtime) {
            return ScheduleStatus.FINISHED;
        }
        return ScheduleStatus.FINISHED;
    }

    /**
     *
     */
    @Override
    public void onOptionActionRefreshSelected() {

    }

    /**
     *
     * @param selectionColor
     */
    @Override
    public void onUserSelectionColor(String selectionColor) {
        Firebase colorSelectionChild = mFirebaseRootRef.child("data/" + selectionColor);
        if (colorSelectionChild != null) {
            colorSelectionChild.runTransaction(new Transaction.Handler() {

                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    if (mutableData.getValue() == null) {
                        mutableData.setValue(1);
                    } else {
                        mutableData.setValue(mutableData.getValue(Integer.class) + 1);
                    }
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (mView != null) {
                        if (firebaseError != null) {
                            mView.setFirebaseError(firebaseError);
                        }
                    }
                }
            }, false);
        }
    }

    /**
     *
     */
    @Override
    public void onViewCreated() {
        addMeToConnectedUser();
        startListeningForSchedule();
        startListeningForData();
    }

    /**
     *
     */
    @Override
    public void onViewDestroyed() {
        mView = null;
        removeMeFromConnectedUser();
    }

    /**
     *
     */
    @Override
    public void onViewHidden() {
    }

    /**
     *
     */
    @Override
    public void onViewShown() {
        if(mCurrentTime != 0 && mStartTime != 0 && mEndtime != 0) {
            mView.setDemoDates(currentStatus(), mCurrentTime, mStartTime, mEndtime);
        }
        mView.setConnectedUsers(mAttendantCounter);
    }

    /**
     *
     */
    private void removeMeFromConnectedUser() {
        mFirebaseRootRef.child("status/connectedUsers").runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                final Integer connectedUsers = mutableData.getValue(Integer.class);
                if (connectedUsers != null && connectedUsers.intValue() > 0) {
                    mAttendantCounter = connectedUsers - 1;
                    mutableData.setValue(mAttendantCounter);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                if (mView != null) {
                    if (firebaseError != null) {
                        mView.setFirebaseError(firebaseError);
                        mAttendantCounter = 0;

                    } else {
                        mAttendantCounter = dataSnapshot.getValue(Integer.class);
                    }
                    mView.setConnectedUsers(mAttendantCounter);
                }
            }
        }, false);
    }

    /**
     *
     */
    private void startListeningForData() {
        mFirebaseRootRef.child("data").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mView != null) {
                    mView.setDemoOutput(dataSnapshot.getValue(Map.class));
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if (mView != null) {
                    if (firebaseError != null) {
                        mView.setFirebaseError(firebaseError);
                    } else {
                        mView.setConnectedUsers(0);
                    }
                }
            }
        });
    }

    /**
     *
     */
    private void startListeningForSchedule() {
        mFirebaseRootRef.child("status/schedule").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> data = dataSnapshot.getValue(Map.class);
                mCurrentTime = System.currentTimeMillis();
                mStartTime = (long) data.get("startTime");
                mEndtime = (long) data.get("endTime");
                if(mView != null) {
                    mView.setDemoDates(currentStatus(), mCurrentTime, mStartTime, mEndtime);
                }

                /*if(mEndtime != (long) data.get("endTime")) {
                    mEndtime = (long) data.get("endTime");

                    new Handler().postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentTime = System.currentTimeMillis();
                            mView.setDemoDates(currentStatus(), mCurrentTime, mStartTime, mEndtime);
                        }
                    }, mEndtime + 60000);
                }

                if (mView != null) {
                    mView.setDemoDates(currentStatus(), mCurrentTime, mStartTime, mEndtime);
                }*/
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if(firebaseError != null && mView != null) {
                    mView.setFirebaseError(firebaseError);
                }
            }
        });

        mFirebaseRootRef.child("status/enabled").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEnabled = dataSnapshot.getValue(Boolean.class);
                if(mView != null) {
                    mView.setDemoEnabled(mEnabled);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if(firebaseError != null && mView != null) {
                    mView.setFirebaseError(firebaseError);
                }
            }
        });
    }

    /**
     *
     */
    private void startListeningForConnectedUsers() {
        mFirebaseRootRef.child("status/connectedUsers").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(mView != null) {
                    mView.setConnectedUsers(dataSnapshot.getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if(firebaseError != null && mView != null) {
                    mView.setFirebaseError(firebaseError);
                }
            }
        });
    }
}
