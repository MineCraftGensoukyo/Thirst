package moe.gensoukyo.thirst.net.pkg;

import moe.gensoukyo.thirst.gui.hud.WaterLevelHud;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CisternUseResPack {
    private final int resultLevel;

    public CisternUseResPack(int level) {
        this.resultLevel = level;
    }

    public CisternUseResPack(FriendlyByteBuf buf) {
        resultLevel = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(resultLevel);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            WaterLevelHud.startHighlight(resultLevel);
        }
        return true;
    }
}
