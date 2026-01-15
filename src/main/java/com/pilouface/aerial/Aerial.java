package com.pilouface.aerial;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.pilouface.aerial.system.AerialFlightSystem;

public class Aerial extends JavaPlugin {
    private AerialFlightSystem movementSystem;

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public Aerial(JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from %s version %s", this.getName(), this.getManifest().getVersion().toString());
    }

    protected void setup() {

        this.movementSystem = new AerialFlightSystem();
        this.getEntityStoreRegistry().registerSystem(this.movementSystem);
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, (event) -> this.movementSystem.handlePlayerReady(event.getPlayer().getUuid()));
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, (event) -> this.movementSystem.handlePlayerDisconnect(event.getPlayerRef().getUuid()));
    }



}
