package net.yxiao233.appliedsoul.common.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class SoulCapabilities {
    public static final Capability<ISoulHandler> BLOCK = CapabilityManager.get(new CapabilityToken<ISoulHandler>() {
    });
}
