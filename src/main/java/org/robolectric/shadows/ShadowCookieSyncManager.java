package org.robolectric.shadows;

import android.content.Context;
import android.webkit.CookieSyncManager;

import org.robolectric.Robolectric;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;

/**
 * Shadows the {@code android.webkit.CookieSyncManager} class.
 */
@Implements(CookieSyncManager.class)
public class ShadowCookieSyncManager {

    private static CookieSyncManager sRef;

    private boolean synced = false;

    @Implementation
    public static synchronized CookieSyncManager createInstance(Context ctx) {
        if (sRef == null) {
            sRef = Robolectric.newInstanceOf(CookieSyncManager.class);
        }
        return sRef;
    }

    @Implementation
    public static CookieSyncManager getInstance() {
        if (sRef == null) {
            throw new IllegalStateException("createInstance must be called first");
        }
        return sRef;
    }

    @Implementation
    public void sync() {
        synced = true;
    }

    public boolean synced() {
        return synced;
    }

    public void reset() {
        synced = false;
    }
}
