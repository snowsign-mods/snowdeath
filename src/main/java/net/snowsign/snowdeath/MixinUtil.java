package net.snowsign.snowdeath;

import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.snowsign.snowdeath.mixin.PlayerListAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class MixinUtil {
    public static @Nullable Integer getPlayerDeaths(@Nullable MinecraftServer server, @NotNull UUID player) {
        if (server == null) return null;

        Map<UUID, ServerStatsCounter> statisticsMap =
            ((PlayerListAccessor) server.getPlayerList()).getStats();
        ServerStatsCounter playerStatHandler = statisticsMap.get(player);

        if (playerStatHandler == null) return null;
        return playerStatHandler.getValue(Stats.CUSTOM.get(Stats.DEATHS));
    }
}
