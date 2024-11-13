package com.robertx22.mine_and_slash.maps;

import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;

public class MapRoomsData {

    public HasDoneData rooms = new HasDoneData();
    public HashMap<String, SingleRoomData> map = new HashMap<>();

    public void addRoom(ChunkPos cp) {
        String key = key(cp);
        if (!map.containsKey(key)) {
            map.put(key, new SingleRoomData());
        }
    }

    public SingleRoomData get(ChunkPos cp) {
        String key = key(cp);
        if (!map.containsKey(key)) {
            map.put(key, new SingleRoomData());
        }
        return map.get(key);
    }

    String key(ChunkPos cp) {
        String key = cp.x + "_" + cp.z;
        return key;
    }

    public boolean isDoneGenerating() {
        if (rooms.total <= 0) {
            return false;
        }
        return rooms.done >= rooms.total;
    }

    public int getMapCompletePercent() {
        int totalpercents = 0;

        for (SingleRoomData data : map.values()) {
            totalpercents += data.getPercentDone();
        }
        int perc = totalpercents / rooms.total;

        if (perc > 100) {
            perc = 100;
            ExileLog.get().log("Map progress percent is somehow more than 100!");
        }

        return perc;
    }
}
