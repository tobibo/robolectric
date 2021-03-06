package org.robolectric.shadows;

import android.telephony.PhoneNumberUtils;
import org.robolectric.TestRunners;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(TestRunners.WithDefaults.class)
public class PhoneNumberUtilTest {
    @Test
    public void testFormatNumber() {
        assertThat(PhoneNumberUtils.formatNumber("12345678901"), equalTo("12345678901-formatted"));
    }

    @Test
    public void testStripSeparators() {
        assertThat(PhoneNumberUtils.stripSeparators("12345678901"), equalTo("12345678901-stripped"));
    }
        
}
