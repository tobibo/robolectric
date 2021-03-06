package org.robolectric.shadows;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import android.app.Activity;
import android.widget.Toast;

import org.robolectric.TestRunners;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TestRunners.WithDefaults.class)
public class ToastTest {

    @Test
    public void shouldHaveShortDuration() throws Exception {
        Toast toast = Toast.makeText(new Activity(), "short toast",
                Toast.LENGTH_SHORT);
        assertNotNull(toast);
        assertEquals(Toast.LENGTH_SHORT, toast.getDuration());
    }

    @Test
    public void shouldHaveLongDuration() throws Exception {
        Toast toast = Toast.makeText(new Activity(), "long toast",
                Toast.LENGTH_LONG);
        assertNotNull(toast);
        assertEquals(Toast.LENGTH_LONG, toast.getDuration());
    }
}
