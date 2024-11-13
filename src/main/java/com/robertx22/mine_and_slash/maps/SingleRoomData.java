package com.robertx22.mine_and_slash.maps;

public class SingleRoomData {
    public HasDoneData chests = new HasDoneData();
    public HasDoneData mobs = new HasDoneData();

    public int getPercentDone() {
        int mobs = this.mobs.getPercentDone();
        int chests = this.chests.getPercentDone();
        return (mobs + chests) / 2;
    }
}
