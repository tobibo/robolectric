package org.robolectric.res;

import android.R;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowStateListDrawable;
import org.robolectric.tester.android.util.ResName;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.HashMap;
import java.util.Map;

import static org.robolectric.Robolectric.shadowOf;

public class DrawableBuilder {
    // Put all the states for a StateListDrawable in the into a Map for looking up
    // http://developer.android.com/guide/topics/resources/drawable-resource.html#StateList
    static final Map<String, Integer> STATE_MAP = new HashMap<String, Integer>();
    static {
        STATE_MAP.put("android:state_selected", R.attr.state_selected);
        STATE_MAP.put("android:state_pressed", R.attr.state_pressed);
        STATE_MAP.put("android:state_focused", R.attr.state_focused);
        STATE_MAP.put("android:state_checkable", R.attr.state_checkable);
        STATE_MAP.put("android:state_checked", R.attr.state_checked);
        STATE_MAP.put("android:state_enabled", R.attr.state_enabled);
        STATE_MAP.put("android:state_window_focused", R.attr.state_window_focused);
    }

    private final ResourceExtractor resourceExtractor;

    public DrawableBuilder(ResourceExtractor resourceExtractor) {
        this.resourceExtractor = resourceExtractor;
    }

    private Drawable getXmlDrawable(Resources resources, DrawableNode.Xml drawableNode, ResName resName) {
        Document xmlDoc = drawableNode.document;
        NodeList nodes = xmlDoc.getElementsByTagName("selector");
        if (nodes != null && nodes.getLength() > 0) {
            return buildStateListDrawable(drawableNode);
        }

        nodes = xmlDoc.getElementsByTagName("layer-list");
        if (nodes != null && nodes.getLength() > 0) {
            NodeList itemNodes = findNodes("/layer-list/item", xmlDoc);
            Drawable[] layers = new Drawable[itemNodes.getLength()];
            for (int i = 0; i < itemNodes.getLength(); i++) {
                Node node = itemNodes.item(i);
                layers[i] = getDrawableForNode(node, resName, resources);
            }
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            shadowOf(layerDrawable).setLoadedFromResourceId(resourceExtractor.getResourceId(resName));
            return layerDrawable;
        }

        nodes = xmlDoc.getElementsByTagName("animation-list");
        if (nodes != null && nodes.getLength() > 0) {
            AnimationDrawable animationDrawable = new AnimationDrawable();

            NodeList itemNodes = findNodes("/animation-list/item", xmlDoc);
            for (int i = 0; i < itemNodes.getLength(); i++) {
                Node node = itemNodes.item(i);
                Drawable frameDrawable = getDrawableForNode(node, resName, resources);
                String duration = node.getAttributes().getNamedItemNS(ResourceLoader.ANDROID_NS, "duration").getNodeValue();
                animationDrawable.addFrame(frameDrawable, Integer.parseInt(duration));
            }
            return animationDrawable;
        }

        return null;
    }

    private Drawable getDrawableForNode(Node node, ResName resName, Resources resources) {
        String drawableName = node.getAttributes().getNamedItemNS(ResourceLoader.ANDROID_NS, "drawable").getNodeValue();
        ResName otherDrawableResName = resName.qualify(drawableName);
        return resources.getDrawable(resourceExtractor.getResourceId(otherDrawableResName));
    }

    private NodeList findNodes(String xpathExpression, Document xmlDoc) {
        try {
            return (NodeList) XPathFactory.newInstance().newXPath().compile(xpathExpression).evaluate(xmlDoc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    private StateListDrawable buildStateListDrawable(DrawableNode.Xml drawableNode) {
        StateListDrawable drawable = new StateListDrawable();
        ShadowStateListDrawable shDrawable = Robolectric.shadowOf(drawable);
        NodeList items = drawableNode.document.getElementsByTagName("item");
        for (int i = 0; i < items.getLength(); i++) {
            Node node = items.item(i);
            Node drawableName = node.getAttributes().getNamedItemNS(ResourceLoader.ANDROID_NS, "drawable");
            if (drawableName != null) {
                int resId = resourceExtractor.getResourceId(drawableName.getNodeValue(), drawableNode.xmlContext.packageName);
                int stateId = getStateId(node);
                shDrawable.addState(stateId, resId);
            }
        }
        return drawable;
    }

    private int getStateId(Node node) {
        NamedNodeMap attrs = node.getAttributes();
        for (String state : STATE_MAP.keySet()) {
            Node attr = attrs.getNamedItem(state);
            if (attr != null) {
                return STATE_MAP.get(state);
            }
        }

        // if a state wasn't specified, return the default state
        return R.attr.state_active;
    }

    public Drawable getDrawable(@NotNull ResName resName, Resources resources, DrawableNode drawableNode) {
        if (drawableNode instanceof DrawableNode.Xml) {
            Drawable xmlDrawable = getXmlDrawable(resources, (DrawableNode.Xml) drawableNode, resName);
            if (xmlDrawable != null) {
                return xmlDrawable;
            }
        }


        if ("anim".equals(resName.type)) {
            return new AnimationDrawable();
        }

        if ("color".equals(resName.type)) {
            return new ColorDrawable();
        }

        if (isNinePatchDrawable(drawableNode)) {
            return new NinePatchDrawable(resources, null);
        }

        return new BitmapDrawable(BitmapFactory.decodeResource(resources, resourceExtractor.getResourceId(resName)));
    }

    public boolean isNinePatchDrawable(DrawableNode drawableNode) {
        return drawableNode instanceof DrawableNode.ImageFile && ((DrawableNode.ImageFile) drawableNode).isNinePatch;
    }
}
