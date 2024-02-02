package moe.gensoukyo.thirst.utils;

import net.minecraft.client.MouseHandler;
import net.minecraft.world.level.Level;

public class ClientHelper {
    public static boolean clientMouseGrabbed(MouseHandler handler, Level level) {
        if (level.isClientSide) {
            return handler.isMouseGrabbed();
        }
        return false;
    }
}
