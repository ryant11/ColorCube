package com.endercrest.colorcube.menu;

import com.endercrest.colorcube.MessageManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CloseItem extends PageItem {

    public CloseItem(Page page, int itemIndex) {
        super(page, itemIndex);
    }

    @Override
    public void onClick(Player player) {
        player.closeInventory();
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(MessageManager.getInstance().getFValue("menu.item.close.title"));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
