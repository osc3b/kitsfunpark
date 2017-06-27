package com.minecraftfunpark.kits.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.minecraftfunpark.kits.Kits;
import com.minecraftfunpark.kits.api.Kit;
import com.minecraftfunpark.kits.permissions.Permissions;
import com.minecraftfunpark.kits.util.CommandDescription;
import com.minecraftfunpark.kits.util.Message;
import com.minecraftfunpark.kits.util.Time;
import com.minecraftfunpark.kits.util.Utils;
import com.minecraftfunpark.kits.util.Message.MessageType;

public class KitsCommandExecutor implements CommandExecutor {
    private final Kits plugin;

    public KitsCommandExecutor(Kits instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {

        if (args.length < 1) {
            handleBaseCommand(sender);
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            handleReload(sender);
        }

        return false;
    }

    private void handleBaseCommand(CommandSender sender) {
        if (sender instanceof Player && !Permissions.checkPermission((Player) sender, Permissions.KITS_LIST)) {
            return;
        }

        if (plugin.getCollectionManager().getKits().size() < 1) {
            sender.sendMessage(Message.show("There are no available kits.", MessageType.WARNING));
            return;
        }

        if (!(sender instanceof Player)) {
            String message = "Kits disponibles: ";

            List<String> kitNames = new ArrayList<>();
            for (Kit kit : plugin.getCollectionManager().getKits()) {
                kitNames.add(ChatColor.DARK_AQUA + kit.getName());
            }

            sender.sendMessage(message + StringUtils.join(kitNames, ", "));

            return;
        }

        Player player = (Player) sender;

        List<CommandDescription> commands = new ArrayList<>();

        for (Kit kit : plugin.getCollectionManager().getKits()) {
            if (!Permissions.hasPermission(player, Permissions.KITS_SPAWN, kit.getName())) continue;

            boolean delayed = plugin.getCollectionManager().getDelayedPlayer(player).playerDelayed(kit);

            List<String> lines = new ArrayList<>();
            List<String> items = new ArrayList<>();

            for (ItemStack item : kit.getItems()) {
                if (item == null) continue;
                items.add(item.getAmount() + " x " + (item.hasItemMeta() ? item.getItemMeta().getDisplayName() : Utils.capitalize(item.getType().name())));
            }

            lines.add(ChatColor.DARK_AQUA + "Number of items: " + ChatColor.GRAY + items.size());
            lines.add(ChatColor.DARK_AQUA + "Delay: " + (kit.getDelay() <= 0 ? ChatColor.GRAY + "no delay" : ChatColor.GRAY + Time.fromMilliseconds(kit.getDelay()).toReadableFormat(false)));
            lines.add(ChatColor.DARK_AQUA + "Clear: " + ChatColor.GRAY + kit.getClear());
            lines.add(ChatColor.DARK_AQUA + "Overwrite: " + ChatColor.GRAY + kit.getOverwrite());
            lines.add(ChatColor.DARK_AQUA + "Announce: " + ChatColor.GRAY + kit.getAnnounce());

            if (delayed) {
                lines.add(" ");
                lines.add(ChatColor.DARK_AQUA + "Remaining time: " + ChatColor.RED + plugin.getCollectionManager().getDelayedPlayer(player).getRemainingTime(kit));
            }

            commands.add(new CommandDescription((delayed ? ChatColor.RED + "" + ChatColor.STRIKETHROUGH : ChatColor.DARK_AQUA + "") + kit.getName(), delayed ? "" : "/kit " + kit.getName(), lines.toArray(new String[lines.size()])));
        }

        player.sendMessage(ChatColor.DARK_GRAY + "Kits disponibles para ti:");
        Message.showCommand(player, " ", commands.toArray(new CommandDescription[commands.size()]));
    }

    private void handleReload(CommandSender sender) {
        if (sender instanceof Player && !Permissions.checkPermission((Player) sender, Permissions.KITS_ADMIN)) {
            return;
        }

        plugin.reload();
        sender.sendMessage(Message.show("Reloaded configurations.", MessageType.INFO));
    }
}
