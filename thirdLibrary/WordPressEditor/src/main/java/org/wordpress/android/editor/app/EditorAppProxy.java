package org.wordpress.android.editor.app;

import android.app.Application;

/**
 * Created by sym on 17/7/22.
 */

public class EditorAppProxy {

    private Application mApp;

    private static final EditorAppProxy ourInstance = new EditorAppProxy();

    public static EditorAppProxy getInstance() {
        return ourInstance;
    }

    private EditorAppProxy() {
    }

    public void init(Application application) {
        this.mApp = application;
    }

    public Application getApplication() {
        return mApp;
    }

}
