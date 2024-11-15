package com.robertx22.mine_and_slash.maps;

import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.library_of_exile.utils.RandomUtils;
import com.robertx22.library_of_exile.utils.TeleportUtils;
import com.robertx22.mine_and_slash.config.forge.ServerContainer;
import com.robertx22.mine_and_slash.database.data.league.LeagueMechanics;
import com.robertx22.mine_and_slash.database.data.league.LeagueStructure;
import com.robertx22.mine_and_slash.database.data.profession.ExplainedResult;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.maps.spawned_map_mobs.SpawnedMobList;
import com.robertx22.mine_and_slash.uncommon.MathHelper;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.interfaces.data_items.IRarity;
import com.robertx22.mine_and_slash.uncommon.localization.Chats;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.WorldUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;

import java.util.HashMap;
import java.util.stream.Collectors;

public class MapData {


    public MapItemData map = new MapItemData();

    public MapRoomsData rooms = new MapRoomsData();

    public MapLeaguesData leagues = new MapLeaguesData();

    private HashMap<String, Integer> lives = new HashMap<>();

    public String dungeonid = "";

    public String mobs = "";

    public String completion_rarity = IRarity.COMMON_ID;

    public boolean gave_boss_tp = false;

    public int despawnedMobs = 0;

    public SpawnedMobList getMobSpawns() {
        return ExileDB.MapMobs().get(mobs);
    }

    public int getLives(Player p) {
        int cur = lives.getOrDefault(p.getStringUUID(), map.getRarity().map_lives);
        return cur;
    }

    public void reduceLives(Player p) {
        int cur = getLives(p) - 1;
        lives.put(p.getStringUUID(), cur);
    }

    public String playerUuid = "";

    public int chunkX = 0;
    public int chunkZ = 0;


    public static MapData newMap(Player p, MapItemData map, MapsData maps) {


        Load.player(p).prophecy.affixesTaken.clear();

        maps.deleteOldMap(p);

        MapData data = new MapData();
        data.playerUuid = p.getStringUUID();
        data.map = map;

        ChunkPos cp = data.randomFree(p.level(), maps);

        data.chunkX = cp.x;
        data.chunkZ = cp.z;

        data.leagues.setupOnMapStart(map, p);


        return data;

    }

    public void spawnRandomLeagueMechanic(Level level, BlockPos pos) {
        var league = LeagueStructure.getMechanicFromPosition((ServerLevel) level, pos);

        if (league == LeagueMechanics.NONE) {
            var list = leagues.getLeagueMechanics().stream().filter(x -> leagues.get(x).remainingSpawns > 0).collect(Collectors.toList());

            if (list.size() > 0) {
                var randomLeague = RandomUtils.randomFromList(list);
                randomLeague.spawnMechanicInMap((ServerLevel) level, pos);
                leagues.get(randomLeague).remainingSpawns--;
            }
        }
    }


    public void teleportToMap(Player p) {
        if (p.level().isClientSide) {
            return;
        }


        Load.player(p).map.sendMapTpMsg = true;

        Load.player(p).map.tpbackdim = p.level().dimension().location().toString();
        Load.player(p).map.tp_back_pos = p.blockPosition().asLong();


        BlockPos pos = getDungeonStartTeleportPos(new ChunkPos(this.chunkX, this.chunkZ));

        Level world = p.level().getServer().getLevel(ResourceKey.create(Registries.DIMENSION, WorldUtils.DUNGEON_DIM_ID));


        world.setBlock(new BlockPos(pos.getX(), 54, pos.getZ()), Blocks.BEDROCK.defaultBlockState(), 2);

        TeleportUtils.teleport((ServerPlayer) p, pos, WorldUtils.DUNGEON_DIM_ID);


    }


    public static BlockPos getDungeonStartTeleportPos(ChunkPos pos) {
        BlockPos p = getStartChunk(pos).getBlockAt(0, 0, 0);
        p = new BlockPos(p.getX() + 8, 57, p.getZ() + 8);
        return p;
    }


    private ChunkPos randomFree(Level level, MapsData maps) {

        ChunkPos pos = null;

        int tries = 0;

        // seems this is how you get the level
        WorldBorder border = level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, WorldUtils.DUNGEON_DIM_ID)).getWorldBorder();

        int max = (int) (border.getMaxX() / 16 / 2);

        max = MathHelper.clamp(max, 0, 299999 / 2); // don't be higher than normal mc border

        while (pos == null || maps.getMap(pos).isPresent() || !border.isWithinBounds(pos)) {
            if (tries++ > 200) {
                ExileLog.get().warn("Tried too many times to find random dungeon pos and failed, please delete the map dimension folder");
                return null;
            }
            int x = RandomUtils.RandomRange(50, max);
            int z = RandomUtils.RandomRange(50, max);

            pos = new ChunkPos(x, z);
            pos = getStartChunk(pos.getMiddleBlockPosition(50));

        }

        if (tries > 1000) {
            ExileLog.get().warn("It took more than 1000 tries to find random free dungeon, either you are insanely unlucky, or the world is close to filled! Dungeon worlds are cleared on next server boot if they reach too close to capacity.");
        }

        return pos;

    }

    public ExplainedResult canTeleportToArena(Player p) {
        int perc = rooms.getMapCompletePercent();

        int needed = ServerContainer.get().MAP_PERCENT_COMPLETE_NEEDED_FOR_BOSS_ARENA.get().intValue();

        if (perc > needed) {
            return ExplainedResult.success(Chats.BOSS_ARENA_UNLOCKED.locName().withStyle(ChatFormatting.GREEN));
        } else {
            return ExplainedResult.failure(Chats.BOSS_LOCKED.locName(needed + "%").withStyle(ChatFormatting.RED));
        }
    }

    public static BlockPos getDungeonStartTeleportPos(BlockPos pos) {
        BlockPos p = getStartChunk(pos).getMiddleBlockPosition(55);
        p = new BlockPos(p.getX(), 55, p.getZ());
        return p;
    }


    public boolean isMapHere(BlockPos pos) {
        var cp = getStartChunk(pos);
        return cp.x == chunkX && cp.z == chunkZ;
    }

    public static int DUNGEON_LENGTH = 30;

    public static ChunkPos getStartChunk(BlockPos pos) {
        return getStartChunk(new ChunkPos(pos));
    }

    public static ChunkPos getStartChunk(ChunkPos cp) {
        int chunkX = cp.x;
        int chunkZ = cp.z;
        int distToEntranceX = 11 - (chunkX % DUNGEON_LENGTH);
        int distToEntranceZ = 11 - (chunkZ % DUNGEON_LENGTH);
        chunkX += distToEntranceX;
        chunkZ += distToEntranceZ;
        return new ChunkPos(chunkX, chunkZ);
    }

}
