package com.mayakplay.aclf.cloud.infrastructure;

import com.mayakplay.aclf.cloud.stereotype.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author mayakplay
 * @version 0.0.1
 * @since 20.07.2019.
 */
public final class NettyGatewayServer implements GatewayServer {

    private final List<ClientRegistrationHandler> registrationHandlers = new ArrayList<>();
    private final NettyServerThread nettyServerThread;
    private final List<ClientNuggetReceiveCallback> receiveCallbacks = new ArrayList<>();

    public NettyGatewayServer(int port, Map<String, String> parameters) {
        final ClientRegistrationHandler clientRegistrationHandler = new ClientRegistrationHandler() {
            @Override
            public void onRegister(GatewayClientInfo clientInfo, Map<String, String> parameters) {
                registrationHandlers.forEach(handler -> handler.onRegister(clientInfo, parameters));
            }

            @Override
            public void onUnregister(GatewayClientInfo clientInfo) {
                registrationHandlers.forEach(handler -> handler.onUnregister(clientInfo));
            }
        };

        nettyServerThread = new NettyServerThread(port, this::initReceiveCallbacks, clientRegistrationHandler, parameters);
        nettyServerThread.start();
    }

    //region Implementation
    @Override
    public void addReceiveCallback(ClientNuggetReceiveCallback callback) {
        receiveCallbacks.add(callback);
    }

    @Override
    public void addRegistrationHandler(ClientRegistrationHandler handler) {
        registrationHandlers.add(handler);
    }

    @Override
    public @NotNull Map<String, GatewayClientInfo> getClients() {
        return nettyServerThread.getClients();
    }

    @Override
    public @NotNull Map<String, GatewayClientInfo> getClientsByType(String type) {
        return nettyServerThread.getClientsByType(type);
    }

    @Override
    public @Nullable GatewayClientInfo getClientById(String clientId) {
        return nettyServerThread.getClientById(clientId);
    }

    @Override
    public void sendToClient(@NotNull GatewayClientInfo clientInfo, @NotNull String message, @NotNull Map<String, String> params) {
        nettyServerThread.sendToClient(clientInfo, message, params);
    }

    @Override
    public void sendToAll(@NotNull String message, @NotNull Map<String, String> params) {
        nettyServerThread.sendToAll(message, params);
    }
    //endregio

    private void initReceiveCallbacks(GatewayClientInfo gatewayClientInfo, Nugget nugget) {
        receiveCallbacks.forEach(callback -> callback.nuggetReceived(gatewayClientInfo, nugget));
    }
}