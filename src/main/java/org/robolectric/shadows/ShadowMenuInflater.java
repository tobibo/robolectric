package org.robolectric.shadows;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.SubMenu;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;
import org.robolectric.res.MenuNode;
import org.robolectric.res.ResourceLoader;
import org.robolectric.tester.android.util.TestAttributeSet;
import org.robolectric.util.I18nException;

import static org.robolectric.Robolectric.shadowOf;

/**
 * Shadow of {@code MenuInflater} that actually inflates menus into {@code View}s that are functional enough to
 * support testing.
 */

@Implements(MenuInflater.class)
public class ShadowMenuInflater {
    private Context context;
    private ResourceLoader resourceLoader;
    private boolean strictI18n;

    public void __constructor__(Context context) {
        this.context = context;
        resourceLoader = shadowOf(context).getResourceLoader();
        strictI18n = shadowOf(context).isStrictI18n();
    }

    @Implementation
    public void inflate(int resource, Menu root) {
        String qualifiers = shadowOf(context.getResources().getConfiguration()).getQualifiers();
        MenuNode menuNode = resourceLoader.getMenuNode(resourceLoader.getResourceExtractor().getResName(resource), qualifiers);

        try {
            addChildrenInGroup(menuNode, 0, root);
        } catch (I18nException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("error inflating " + shadowOf(context).getResName(resource), e);
        }
    }

    private void addChildrenInGroup(MenuNode source, int groupId, Menu root) {
        for (MenuNode child : source.getChildren()) {
            String name = child.getName();
            TestAttributeSet attributes = shadowOf(context).createAttributeSet(child.getAttributes(), null);
            if (strictI18n) {
                attributes.validateStrictI18n();
            }
            if (name.equals("item")) {
                if (child.isSubMenuItem()) {
                    SubMenu sub = root.addSubMenu(groupId,
                            attributes.getAttributeResourceValue("android", "id", 0),
                            0, attributes.getAttributeValue("android", "title"));
                    MenuNode subMenuNode = child.getChildren().get(0);
                    addChildrenInGroup(subMenuNode, groupId, sub);
                } else {
                    root.add(groupId,
                            attributes.getAttributeResourceValue("android", "id", 0),
                            0, attributes.getAttributeValue("android", "title"));
                }
            } else if (name.equals("group")) {
                int newGroupId = attributes.getAttributeResourceValue("android", "id", 0);
                addChildrenInGroup(child, newGroupId, root);
            }
        }
    }
}
