package com.vi.vimarvel.dispatcher;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class Dispatcher {
    private static Dispatcher mInstance;
    private Handler actionsHandler;
    private HashMap<ActionType, Set<WeakReference<IActionHandler>>> actionListeners;
    private Handler eventsHandler;
    private HashMap<EventType, Set<WeakReference<IEventHandler>>> eventListeners;

    public static synchronized Dispatcher getInstance() {
        if (mInstance == null) {
            mInstance = new Dispatcher();
        }

        return mInstance;
    }

    private Dispatcher() {
        HandlerThread bgThreadAction = new HandlerThread("ActionHandlerThread");
        bgThreadAction.start();
        Looper actionLooper = bgThreadAction.getLooper();
        actionsHandler = new Handler(actionLooper);
        actionListeners = new HashMap<>();

        HandlerThread bgThreadEvent = new HandlerThread("EventHandlerThread");
        bgThreadEvent.start();
        Looper eventLooper = bgThreadEvent.getLooper();
        eventsHandler = new Handler(eventLooper);
        eventListeners = new HashMap<>();
    }

    @SuppressWarnings("ConstantConditions")
    public void addActionListener(ActionType actionType, IActionHandler actionHandler) {
        if (actionListeners.get(actionType) == null) {
            actionListeners.put(actionType, new LinkedHashSet<>());
        }

        WeakReference<IActionHandler> weakActionHandler = new WeakReference<>(actionHandler);
        actionListeners.get(actionType).add(weakActionHandler);
    }

    public void dispatchAction(ActionType actionType) {
        dispatchAction(actionType, null);
    }

    public void dispatchAction(ActionType actionType, Object arguments) {
        this.actionsHandler.post(() -> {
            Set<WeakReference<IActionHandler>> actionHandlers = actionListeners.get(actionType);
            if (actionHandlers != null) {
                Iterator<WeakReference<IActionHandler>> iterator = actionHandlers.iterator();
                for (; iterator.hasNext(); ) {
                    WeakReference<IActionHandler> handler = iterator.next();
                    if (handler.get() != null) {
                        handler.get().onAction(actionType, arguments);
                    }
                }
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    public void addEventListener(EventType eventType, IEventHandler eventHandler) {
        if (eventListeners.get(eventType) == null) {
            eventListeners.put(eventType, new LinkedHashSet<>());
        }

        WeakReference<IEventHandler> weakEventHandler = new WeakReference<>(eventHandler);
        eventListeners.get(eventType).add(weakEventHandler);
    }

    public void dispatchEvent(EventType eventType, Object arguments) {
        this.eventsHandler.post(() -> {
            Set<WeakReference<IEventHandler>> eventHandlers = eventListeners.get(eventType);
            if (eventHandlers != null) {
                Iterator<WeakReference<IEventHandler>> iterator = eventHandlers.iterator();
                for (; iterator.hasNext(); ) {
                    WeakReference<IEventHandler> handler = iterator.next();
                    if (handler.get() != null) {
                        handler.get().onEvent(eventType, arguments);
                    }
                }
            }
        });
    }

    public void dispatchEventFailure(EventType eventType, EventErrorType eventError, Object arguments) {
        this.eventsHandler.post(() -> {
            Set<WeakReference<IEventHandler>> eventHandlers = eventListeners.get(eventType);
            if (eventHandlers != null) {
                Iterator<WeakReference<IEventHandler>> iterator = eventHandlers.iterator();
                for (; iterator.hasNext(); ) {
                    WeakReference<IEventHandler> handler = iterator.next();
                    if (handler.get() != null) {
                        handler.get().onEventFailure(eventType, eventError, arguments);
                    }
                }
            }
        });
    }
}