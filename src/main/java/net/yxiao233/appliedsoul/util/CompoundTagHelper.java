package net.yxiao233.appliedsoul.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;

public class CompoundTagHelper {
    public static void writePosListToTag(ArrayList<BlockPos> posList, CompoundTag tag, String key){
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("pos_list_size",posList.size());
        posList.forEach(pos -> {
            CompoundTag xyz = new CompoundTag();
            xyz.putInt("x",pos.getX());
            xyz.putInt("y",pos.getY());
            xyz.putInt("z",pos.getZ());
            compoundTag.put("pos" + posList.indexOf(pos),xyz);
        });
        tag.put(key,compoundTag);
    }

    public static ArrayList<BlockPos> readPosListFromTag(CompoundTag tag, String key){
        ArrayList<BlockPos> list = new ArrayList<>();
        if(!tag.contains(key)){
            return list;
        }
        CompoundTag compoundTag = tag.getCompound(key);
        if(compoundTag.contains("pos_list_size", 99)){
            int posListSize = compoundTag.getInt("pos_list_size");
            for (int i = 0; i < posListSize; i++) {
                CompoundTag xzy = compoundTag.getCompound("pos" + i);
                list.add(new BlockPos(xzy.getInt("x"),xzy.getInt("y"),xzy.getInt("z")));
            }
        }
        return list;
    }
}
