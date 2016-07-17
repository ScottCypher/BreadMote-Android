package com.cypher.breadmote;

import java.util.List;

/**
 * Used to actively listen to component changes while connected to a device
 *
 * @see Connection#addListener(ComponentListener)
 * @see Connection#removeListener(ComponentListener)
 */
public interface ComponentListener {

    /**
     * Called when a new component is added by the connected device
     *
     * @param component The newly added component
     */
    void onComponentAdded(Component component);

    /**
     * Called when a component is updated by the connected device
     *
     * @param index The index of the updated component
     * @see #setComponents(List)
     */
    void onComponentUpdated(int index);

    /**
     * Called when a component is removed by the connected device
     *
     * @param index The index of the removed component
     * @see #setComponents(List)
     */
    void onComponentRemoved(int index);

    /**
     * Called either when the component list is being refreshed or when the listener is first added
     *
     * @param components The current list of components specified by the connected device
     */
    void setComponents(List<Component> components);

    /**
     *
     * @param isCreating {@code true} if the component list is being requested by this device,
     *                               {@code false} otherwise
     * @see Connection#refreshComponents()
     */
    void onComponentCreation(boolean isCreating);
}
