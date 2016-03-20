package com.martellux.droidconit2016.firebasedemo.view.protocol.demo;

import com.martellux.droidconit2016.firebasedemo.view.protocol.IViewLifecycle;

/**
 * Created by alessandromartellucci on 27/02/16.
 */
public interface IDemoViewLifecycle extends IViewLifecycle {

    /**
     *
     */
    public void onOptionActionRefreshSelected();

    /**
     *
     * @param selectionColor
     */
    public void onUserSelectionColor(String selectionColor);
}
