package org.robolectric.shadows;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;

import org.robolectric.TestRunners;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.text.SpannableString;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

@RunWith(TestRunners.WithDefaults.class)
public class SpannableStringTest {
	
	private SpannableString spanStr;
	
	private static final String TEST_STRING = "Visit us at http://www.foobar.com for more selections";

	@Before
	public void setUp() throws Exception {
		spanStr = new SpannableString(TEST_STRING);
	}

	@Test
	public void testToString() {
		assertThat(spanStr.toString(), sameInstance(TEST_STRING));
	}

	@Test
	public void testSetSpan() {
		URLSpan s1 = new URLSpan("http://www.foobar.com");
		UnderlineSpan s2 = new UnderlineSpan();
		spanStr.setSpan(s1, 12, 33, 0);
		spanStr.setSpan(s2, 1, 10, 0);
		
		assertBothSpans( s1, s2 );
	}

	@Test
	public void testRemoveSpan() {
		URLSpan s1 = new URLSpan("http://www.foobar.com");
		UnderlineSpan s2 = new UnderlineSpan();
		spanStr.setSpan(s1, 12, 33, 0);
		spanStr.setSpan(s2, 1, 10, 0);
		spanStr.removeSpan(s1);
		
		Object[] spans = spanStr.getSpans(0, TEST_STRING.length(), Object.class);
		assertThat(spans, notNullValue());
		assertThat(spans.length, equalTo(1));
		assertThat((UnderlineSpan) spans[0], sameInstance(s2));
	}

	@Test
	public void testGetSpans() {
		URLSpan s1 = new URLSpan("http://www.foobar.com");
		UnderlineSpan s2 = new UnderlineSpan();
		spanStr.setSpan(s1, 1, 10, 0);
		spanStr.setSpan(s2, 20, 30, 0);
		
		Object[] spans = spanStr.getSpans(0, TEST_STRING.length(), Object.class);
		assertThat(spans, notNullValue());
		assertThat(spans.length, equalTo(2));
		assertBothSpans( s1, s2 ); 
		
		spans = spanStr.getSpans(0, TEST_STRING.length(), URLSpan.class);
		assertThat(spans, notNullValue());
		assertThat(spans.length, equalTo(1));
		assertThat((URLSpan) spans[0], sameInstance(s1));
		
		spans = spanStr.getSpans(11, 35, Object.class);
		assertThat(spans, notNullValue());
		assertThat(spans.length, equalTo(1));
		assertThat((UnderlineSpan) spans[0], sameInstance(s2));
		
		spans = spanStr.getSpans(21, 35, Object.class);
		assertThat(spans, notNullValue());
		assertThat(spans.length, equalTo(1));
		assertThat((UnderlineSpan) spans[0], sameInstance(s2));
		
		spans = spanStr.getSpans(5, 15, Object.class);
		assertThat(spans, notNullValue());
		assertThat(spans.length, equalTo(1));
		assertThat((URLSpan) spans[0], sameInstance(s1));		
	}
	
	private void assertBothSpans( URLSpan s1, UnderlineSpan s2 ) {
		Object[] spans = spanStr.getSpans(0, TEST_STRING.length(), Object.class);
		if ( spans[0] instanceof URLSpan ) {
			assertThat((URLSpan) spans[0], sameInstance(s1));
		} else {
			assertThat((UnderlineSpan) spans[0], sameInstance(s2));			
		}
		if ( spans[1] instanceof UnderlineSpan ) {
			assertThat((UnderlineSpan) spans[1], sameInstance(s2));
		} else {
			assertThat((URLSpan) spans[1], sameInstance(s1));		
		}
	}

}
