package com.endercrest.colorcube.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Thomas Cordua-von Specht on 12/23/2016.
 *
 * This is a utility that is here to help send packets to players as required.
 */
public class NMSUtil {

    public enum SupportVersion{
        v1_9_R1,
        v1_10_R1,
        v1_11_R1,
        UNSUPPORTED;

        private static SupportVersion serverVersion;

        public static SupportVersion getServerVersion(){
            if(serverVersion == null) {
                try {
                    serverVersion = SupportVersion.valueOf(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);
                } catch (IllegalArgumentException e) {
                    serverVersion = UNSUPPORTED;
                }
            }
            return serverVersion;
        }
    }

    public static void sendPacket(Player p, Object packet) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, ClassNotFoundException {
        Object nmsPlayer = p.getClass().getMethod("getHandle").invoke(p);
        Object plrConnection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
        plrConnection.getClass().getMethod("sendPacket", getNmsClass("Packet")).invoke(plrConnection, packet);
    }

    public static Class<?> getNmsClass(String nmsClassName) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + nmsClassName);
    }
}
