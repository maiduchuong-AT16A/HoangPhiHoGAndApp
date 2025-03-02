package com.example.managerstaff.supports;

import android.util.Log;

import com.example.managerstaff.models.NotificationData;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient {
    private static final String WS_URL = "ws://192.168.100.195:8000/ws/socket-server/";

    private WebSocket webSocket;

    public void startWebSocket() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        Request request = new Request.Builder()
                .url(WS_URL)
                .build();

        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                // Khi kết nối được mở
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Gson gson = new Gson();
                NotificationData notificationData=gson.fromJson(text, NotificationData.class);
                EventBus.getDefault().post(new MessageEvent(notificationData));
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                // Xử lý thông điệp dạng bytes từ server
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                // Khi kết nối đóng
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                // Xử lý lỗi
            }
        };

        webSocket = client.newWebSocket(request, listener);
    }

    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    public void closeWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, null);
        }
    }
}

