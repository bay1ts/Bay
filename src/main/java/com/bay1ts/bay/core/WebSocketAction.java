package com.bay1ts.bay.core;

/**
 * Created by chenu on 2016/11/17.
 */
public interface WebSocketAction {
    void onMessage(WebSocketContext context);
    void onClose(WebSocketContext context);

    void onError(WebSocketContext webSocketContext);

    void onConnect(WebSocketContext webSocketContext);
}
