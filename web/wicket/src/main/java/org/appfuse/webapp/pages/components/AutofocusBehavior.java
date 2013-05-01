package org.appfuse.webapp.pages.components;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;

/**
 * A behavior that add a autofocus attribute support for input components.
 */
public class AutofocusBehavior extends Behavior {

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        super.onComponentTag(component, tag);
        tag.put("autofocus", "autofocus");
    }
}
