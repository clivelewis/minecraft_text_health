package com.clivelewis.healthtext;

import net.minecraft.server.v1_14_R1.ChatMessageType;
import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Clive on 8/31/2019
 */
public class Main extends JavaPlugin implements Listener {
    private Logger logger;
    private final String LEFT_ALIGN_WORKAROUND = "                             ";
    private boolean enabled;
    private boolean boldText;
    private String prefix;
    private int maxHearths;


    @Override
    public void onEnable() {
        logger = getLogger();
        this.getServer().getPluginManager().registerEvents(this, this);
        loadConfiguration();
    }

    private void loadConfiguration() {
        this.saveDefaultConfig();
        try {
            enabled = this.getConfig().getBoolean("enabled", true);
            boldText = this.getConfig().getBoolean("boldText", false);
            prefix = this.getConfig().getString("prefix", "HP: ");
            maxHearths = this.getConfig().getInt("maxHearths", 10);
        }catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent e){
        if(!enabled) return;

        Player p = e.getPlayer();
        p.setHealthScale(maxHearths * 2);

        this.getServer().getScheduler()
                .runTaskTimerAsynchronously(this, () -> showHealthBar(p), 0L, 1L);
    }

    private void showHealthBar(Player player){
        try{
            String healthDisplay = (boldText ? "" + ChatColor.BOLD : "")
                    + ChatColor.RED + prefix + (int) player.getHealth() + "/"
                    + (int) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + LEFT_ALIGN_WORKAROUND;

            IChatBaseComponent comp = IChatBaseComponent.ChatSerializer
                    .a("{\"text\":\"" + healthDisplay + "\"}");
            PacketPlayOutChat packet = new PacketPlayOutChat(comp, ChatMessageType.GAME_INFO);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);

        }catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
        }

    }
}
