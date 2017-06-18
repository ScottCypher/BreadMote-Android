package com.cypher.breadmote;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a connection with a device
 */
public class Connection {

    /**
     * A component type value that is not used
     */
    public static final byte TYPE_INVALID = -1;

    static final byte TYPE_HEADER = 0;
    static final byte TYPE_CREATE = 1;
    static final byte TYPE_ERROR = 2;
    static final byte TYPE_REMOVE = 3;
    static final byte TYPE_ENABLE = 4;
    static final byte TYPE_UPDATE = 5;

    /**
     * The type of a slider / seek bar component
     */
    public static final byte TYPE_SLIDER = 10;

    /**
     * The type of a switch component
     */
    public static final byte TYPE_SWITCH = 11;

    /**
     * The type of a button component
     */
    public static final byte TYPE_BUTTON = 12;

    /**
     * The type of a checkbox component
     */
    public static final byte TYPE_CHECKBOX = 13;

    /**
     * The type of a text field component
     */
    public static final byte TYPE_TEXTFIELD = 14;

    /**
     * The type of a label component
     */
    public static final byte TYPE_LABEL = 15;

    /**
     * The type of a time picker component
     */
    public static final byte TYPE_TIMEPICKER = 16;

    /**
     * The type of a radio group component
     */
    public static final byte TYPE_RADIO_GROUP = 17;

    private final Context context;

    private final List<Component> components;
    private final List<Error> errors;

    private final List<ComponentListener> componentListeners;
    private final List<ErrorListener> errorListeners;

    private boolean isCreating;
    private MessageWriter messageWriter;
    private MessageReader messageReader;
    private MessageHeader messageHeader;
    private final Manager manager;

    Connection(Context context, Manager manager) {
        this.context = context;
        this.manager = manager;
        isCreating = true;

        components = new ArrayList<>();
        componentListeners = new LinkedList<>();
        errors = new ArrayList<>();
        errorListeners = new LinkedList<>();
    }

    /**
     * Registers a listener to observe component events
     * @param componentListener The listener to be registered
     */
    public void addListener(ComponentListener componentListener) {
        componentListeners.add(componentListener);
        componentListener.setComponents(getUnmodifiableComponents());
        componentListener.onComponentCreation(isCreating);
    }

    /**
     * Unregisters a listener from component events
     *
     * @param componentListener The listener to be unregistered
     * @return {@code true} if the listener was successfully removed, {@code false} otherwise
     */
    public boolean removeListener(ComponentListener componentListener) {
        return componentListeners.remove(componentListener);
    }

    /**
     * Registers a listener for error events
     *
     * @param errorListener The listener to be registered
     */
    public void addListener(ErrorListener errorListener) {
        errorListeners.add(errorListener);
        errorListener.setErrors(getUnmodifiableErrors());
    }

    /**
     * Unregisters a listener from error events
     *
     * @param errorListener The listener to be unregistered
     * @return {@code true} if the listener was successfully removed, {@code false} otherwise     */
    public boolean removeListener(ErrorListener errorListener) {
        return errorListeners.remove(errorListener);
    }

    private void notifyError(Error error) {
        errors.add(error);

        for (ErrorListener errorListener : errorListeners) {
            errorListener.onError(error);
        }
    }

    private void notifyComponentCreation(Creation creation) {
        isCreating = creation.isCreating();
        for (ComponentListener componentListener : componentListeners) {
            componentListener.onComponentCreation(isCreating);
        }

        if (isCreating) {
            clearComponents();
        }
    }

    void onMessageRecieved(int type, byte[] value) {
        MessageAnalyzer messageAnalyzer = new MessageAnalyzer(context, messageHeader, type, value);
        Object object = messageAnalyzer.analyzeMessage();
        onObjectRecieved(object);
    }

    void setReadWrite(MessageReader messageReader, MessageWriter messageWriter) {
        this.messageWriter = messageWriter;
        this.messageReader = messageReader;
        sendComponentChange(new Header());
    }

    private void onObjectRecieved(Object object) {
        if (object instanceof Component) {
            Component component = (Component) object;
            onNewComponent(component);
        } else if (object instanceof Update) {
            Update update = (Update) object;
            onUpdateComponent(update);
        } else if (object instanceof Removal) {
            Removal removal = (Removal) object;
            onRemoveComponent(removal);
        } else if (object instanceof Error) {
            Error error = (Error) object;
            notifyError(error);
        } else if (object instanceof Creation) {
            Creation creation = (Creation) object;
            notifyComponentCreation(creation);
        } else if (object instanceof MessageHeader) {
            MessageHeader messageHeader = (MessageHeader) object;
            setMessageHeader(messageHeader);

            createController();
        } else if (object instanceof Enable) {
            Enable enable = (Enable) object;
            onEnableComponent(enable);
        } else {
            throw new RuntimeException("Unknown message: " + object.getClass());
        }
    }

    private void setMessageHeader(MessageHeader messageHeader) {
        this.messageHeader = messageHeader;
        messageReader.setMessageHeader(messageHeader);
        messageWriter.setMessageHeader(messageHeader);
    }

    private void onRemoveComponent(Removal removal) {
        int index = 0;
        boolean found = false;
        for (Iterator<Component> iterator = components.iterator(); iterator.hasNext(); ) {
            Component component = iterator.next();
            if (removal.isFor(component)) {
                iterator.remove();
                found = true;
                break;
            }
            index++;
        }

        if (found) {
            for (ComponentListener componentListener : componentListeners) {
                componentListener.onComponentRemoved(index);
            }
        } else {
            Error error = removal.generateError(context);
            notifyError(error);
        }
    }

    private void onEnableComponent(Enable enable) {
        int index = 0;
        boolean found = false;
        for (Component component : components) {
            if (enable.isFor(component)) {
                boolean isEnabled = enable.isEnabled();
                if (isEnabled == component.isEnabled()) {
                    return;
                } else {
                    component.setEnabled(isEnabled);
                    found = true;
                    break;
                }
            }
            index++;
        }

        if (found) {
            notifyComponentsUpdated(index);
        } else {
            Error error = enable.generateError(context);
            notifyError(error);
        }
    }

    private void notifyComponentsUpdated(int index) {
        for (ComponentListener componentListener : componentListeners) {
            componentListener.onComponentUpdated(index);
        }
    }

    private void onUpdateComponent(Update update) {
        boolean found = false;
        int index = 0;
        while (index < components.size()) {
            Component component = components.get(index);
            if (update.getName().equals(component.getName())) {
                if (update.canApplyTo(messageHeader, component)) {
                    if (component.isAnUpdate(messageHeader, update)) {
                        component.update(messageHeader, update);
                        found = true;
                    } else {
                        //same value, no update
                        return;
                    }
                }
                break;
            }
            index++;
        }

        if (found) {
            notifyComponentsUpdated(index);
        } else {
            Error error = update.generateError(context);
            notifyError(error);
        }
    }

    private void onNewComponent(Component component) {
        boolean found = false;
        for (int index = 0; index < components.size(); index++) {
            Component existingComponent = components.get(index);
            if (existingComponent.getName().equals(component.getName())) {
                found = true;
                break;
            }
        }

        if (found) {
            Error error = component.generateError(context);
            notifyError(error);
        } else {
            components.add(component);

            for (ComponentListener componentListener : componentListeners) {
                componentListener.onComponentAdded(component);
            }
        }
    }

    /**
     * Removes an error from the error log
     * @param index The index of the error to be removed
     */
    public void removeError(int index) {
        errors.remove(index);
        for (ErrorListener errorListener : errorListeners) {
            errorListener.onErrorRemoved(index);
        }
    }

    /**
     * Clears the error log
     */
    public void clearErrors() {
        errors.clear();
        for (ErrorListener errorListener : errorListeners) {
            errorListener.setErrors(getUnmodifiableErrors());
        }
    }

    /**
     * Clears all components and errors and disconnects from the currently connected device. The
     * connection is no longer valid.
     */
    public void disconnect() {
        clearComponents();
        componentListeners.clear();
        clearErrors();
        errorListeners.clear();

        manager.disconnect();
    }

    private List<Error> getUnmodifiableErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Requests the connected device resend the list of components. The current list of components will
     * be cleared.
     */
    public void refreshComponents() {
        clearComponents();
        createController();
    }

    private void clearComponents() {
        components.clear();
        for (ComponentListener componentListener : componentListeners) {
            componentListener.setComponents(getUnmodifiableComponents());
        }
    }

    private void createController() {
        Creation creation = new Creation(true);
        notifyComponentCreation(creation);
        sendComponentChange(creation);
    }

    /**
     * Used to notify the connected device a component has been interacted with.
     * @param component The modified component
     */
    public void sendComponentChange(Component component) {
        sendComponentChange((TLVCompatible)component);
    }

    void sendComponentChange(TLVCompatible tlvCompatible) {
        if (messageWriter != null) {
            try {
                messageWriter.putComponent(tlvCompatible);
            } catch (InterruptedException e) {
                //TODO handle failure
                e.printStackTrace();
            }
        }
    }

    private List<Component> getUnmodifiableComponents() {
        return Collections.unmodifiableList(components);
    }
}
