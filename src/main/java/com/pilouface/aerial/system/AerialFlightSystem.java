// java
package com.pilouface.aerial.system;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.builtin.mounts.MountedComponent;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockMovementSettings;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.modules.entity.component.CollisionResultComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PositionDataComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerProcessMovementSystem;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.Nonnull;


public class AerialFlightSystem extends EntityTickingSystem<EntityStore> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private final ComponentType<EntityStore, Player> playerComponentType;
    private final ComponentType<EntityStore, PlayerRef> playerRefComponentType;
    private final ComponentType<EntityStore, TransformComponent> transformComponentType;
    private final ComponentType<EntityStore, Velocity> velocityComponentType;
    private final ComponentType<EntityStore, MovementStatesComponent> movementStatesComponentType;
    private final ComponentType<EntityStore, CollisionResultComponent> collisionResultComponentType;
    private final ComponentType<EntityStore, PositionDataComponent> positionDataComponentType;
    private final Query<EntityStore> query;
    private final Map<UUID, PlayerState> states = new HashMap<>();

    public AerialFlightSystem() {
        this.playerComponentType = Player.getComponentType();
        this.playerRefComponentType = PlayerRef.getComponentType();
        this.transformComponentType = TransformComponent.getComponentType();
        this.velocityComponentType = Velocity.getComponentType();
        this.movementStatesComponentType = MovementStatesComponent.getComponentType();
        this.collisionResultComponentType = CollisionResultComponent.getComponentType();
        this.positionDataComponentType = PositionDataComponent.getComponentType();
        this.query = Query.and(this.playerComponentType, this.playerRefComponentType, this.transformComponentType, this.velocityComponentType, this.movementStatesComponentType, this.collisionResultComponentType, this.positionDataComponentType);

        // Packet analysis for behavior debugging
        /*
        PacketAdapters.registerInbound((PacketHandler handler, Packet packet) -> {
            String handlerName = handler.getClass().getSimpleName();
            String packetName = packet.getClass().getSimpleName();
            if (!"EntityUpdates".equals(packetName) && !"CachedPacket".equals(packetName) && packet.getId() == 108) {
                LOGGER.at(Level.INFO).log("[" + handlerName + "] Sent packet id=" + packet.getId() + ": " + packetName);
            }
        });
        */
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.query;
    }

    public boolean isParallel(int archetypeChunkSize, int taskCount) {
        return false;
    }

    public void handlePlayerReady(@Nonnull UUID playerUuid) {
        PlayerState state = (PlayerState)this.states.computeIfAbsent(playerUuid, (k) -> new PlayerState());
    }

    public void handlePlayerDisconnect(@Nonnull UUID playerUuid) {
        this.states.remove(playerUuid);
    }

    @Override
    public void tick(float deltaSeconds, int startIndex, ArchetypeChunk<EntityStore> chunk, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        Player player = (Player)chunk.getComponent(startIndex, this.playerComponentType);
        if (player == null) return;
        if(this.hasWings(player).equals(0)) {
            MovementStatesComponent movementStates = (MovementStatesComponent)chunk.getComponent(startIndex, this.movementStatesComponentType);
            Velocity velocity = (Velocity)chunk.getComponent(startIndex, this.velocityComponentType);
            PositionDataComponent positionData = (PositionDataComponent)chunk.getComponent(startIndex, this.positionDataComponentType);
            TransformComponent transform = (TransformComponent)chunk.getComponent(startIndex, this.transformComponentType);

            if (movementStates.getMovementStates().jumping && player.getGameMode().equals(GameMode.Adventure)) {
                    // flight logic
            }
        }
    }

    public Boolean hasWings(@Nonnull Player player) {
        Inventory inventory = player.getInventory();
        ItemContainer armor = inventory.getArmor();
        ItemStack chest = armor.getItemStack((short) 1);

        if (chest == null) {
            return false;
        }
        return chest.getItemId().equals("Aerial_Wings_T1") || chest.getItemId().equals("Aerial_Wings_T2") || chest.getItemId().equals("Aerial_Wings_T3");
    }

    private static final class PlayerState {
        private PlayerState() {}
    }
}
