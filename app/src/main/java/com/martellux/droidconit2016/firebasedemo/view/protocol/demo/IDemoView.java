package com.martellux.droidconit2016.firebasedemo.view.protocol.demo;

import com.firebase.client.FirebaseError;
import com.martellux.droidconit2016.firebasedemo.view.protocol.IView;
import com.martellux.droidconit2016.firebasedemo.view.protocol.IViewLifecycle;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Created by alessandromartellucci on 27/02/16.
 */
public interface IDemoView extends IView {

    /**
     *
     * @param connectedUsers
     */
    public void setConnectedUsers(int connectedUsers);

    /**
     *
     * @param status
     * @param currentTime
     * @param startDate
     * @param endDate
     */
    public void setDemoDates(int status, long currentTime, long startDate, long endDate);

    /**
     *
     * @param firebaseError
     */
    public void setFirebaseError(FirebaseError firebaseError);

    /**
     *
     * @param output
     */
    public void setDemoOutput(Map<String, Object> output);
}
