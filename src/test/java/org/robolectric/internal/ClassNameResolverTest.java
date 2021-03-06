package org.robolectric.internal;

import android.app.Application;
import org.robolectric.TestApplication;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClassNameResolverTest {
    @Test
    public void shouldResolveClassesBySimpleName() throws Exception {
        assertEquals(TestApplication.class, new ClassNameResolver<Application>("org.robolectric", "TestApplication").resolve());
    }

    @Test
    public void shouldResolveClassesByDottedSimpleName() throws Exception {
        assertEquals(TestApplication.class, new ClassNameResolver<Application>("org.robolectric", ".TestApplication").resolve());
    }

    @Test
    public void shouldResolveClassesByFullyQualifiedName() throws Exception {
        assertEquals(TestApplication.class, new ClassNameResolver<Application>("org.robolectric", "org.robolectric.TestApplication").resolve());
    }

    @Test
    public void shouldResolveClassesByPartiallyQualifiedName() throws Exception {
        assertEquals(TestApplication.class, new ClassNameResolver<Application>("org", ".robolectric.TestApplication").resolve());
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotResolveClassesByUndottedPartiallyQualifiedNameBecauseAndroidDoesnt() throws Exception {
        new ClassNameResolver<Application>("org", "robolectric.TestApplication").resolve();
    }
}
