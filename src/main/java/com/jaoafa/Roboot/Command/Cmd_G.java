package com.jaoafa.Roboot.Command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Cmd_G implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.GREEN + "[G] " + "このコマンドはゲーム内から実行してください。");
                return true;
            }
            Player player = (Player) sender;
            GameMode beforeGameMode = player.getGameMode();

            if (player.getGameMode() == GameMode.SPECTATOR) {
                // スペクテイターならクリエイティブにする
                player.setGameMode(GameMode.CREATIVE);
                if (player.getGameMode() != GameMode.CREATIVE) {
                    sender.sendMessage(ChatColor.GREEN + "[G] " + "ゲームモードの変更ができませんでした。");
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + "[G] " + beforeGameMode.name() + " -> " + GameMode.CREATIVE.name());
                return true;
            } else if (player.getGameMode() == GameMode.CREATIVE) {
                // クリエイティブならスペクテイターにする
                player.setGameMode(GameMode.SPECTATOR);
                if (player.getGameMode() != GameMode.SPECTATOR) {
                    sender.sendMessage(ChatColor.GREEN + "[G] " + "ゲームモードの変更ができませんでした。");
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + "[G] " + beforeGameMode.name() + " -> " + GameMode.SPECTATOR.name());
                return true;
            } else {
                // それ以外(サバイバル・アドベンチャー)ならクリエイティブにする
                player.setGameMode(GameMode.CREATIVE);
                if (player.getGameMode() != GameMode.CREATIVE) {
                    sender.sendMessage(ChatColor.GREEN + "[G] " + "ゲームモードの変更ができませんでした。");
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + "[G] " + beforeGameMode.name() + " -> " + GameMode.CREATIVE.name());
                return true;
            }
        } else if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.GREEN + "[G] " + "このコマンドはゲーム内から実行してください。");
                return true;
            }
            Player player = (Player) sender;
            GameMode beforeGameMode = player.getGameMode();

            int i;
            try {
                i = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.GREEN + "[G] " + "引数には数値を指定してください。");
                return true;
            }

            @SuppressWarnings("deprecation")
            GameMode gm = GameMode.getByValue(i);
            if (gm == null) {
                sender.sendMessage(ChatColor.GREEN + "[G] " + "指定された引数からゲームモードが取得できませんでした。");
                return true;
            }

            player.setGameMode(gm);
            if (player.getGameMode() != gm) {
                sender.sendMessage(ChatColor.GREEN + "[G] " + "ゲームモードの変更ができませんでした。");
                return true;
            }
            sender.sendMessage(ChatColor.GREEN + "[G] " + beforeGameMode.name() + " -> " + gm.name());
            return true;
        } else if (args.length == 2) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.isOp()) {
                    sender.sendMessage(ChatColor.GREEN + "[G] " +
                            "あなたの権限では他のユーザーのゲームモードを変更することはできません。自身のゲームモードを変更する場合はプレイヤー名を入れずに入力してください。");
                    return true;
                }
            }

            int i;
            try {
                i = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.GREEN + "[G] " + "引数には数値を指定してください。");
                return true;
            }

            @SuppressWarnings("deprecation")
            GameMode gm = GameMode.getByValue(i);
            if (gm == null) {
                sender.sendMessage(ChatColor.GREEN + "[G] " + "指定された引数からゲームモードが取得できませんでした。");
                return true;
            }

            String playername = args[1];
            Player player = Bukkit.getPlayerExact(playername);
            if (player == null) {
                sender.sendMessage(ChatColor.GREEN + "[G] " + "指定されたプレイヤー「" + playername + "」は見つかりませんでした。");

                Player any_chance_player = Bukkit.getPlayer(playername);
                if (any_chance_player != null) {
                    sender.sendMessage(ChatColor.GREEN + "[G] " + "もしかして: " + any_chance_player.getName());
                }
                return true;
            }

            GameMode beforeGameMode = player.getGameMode();

            player.setGameMode(gm);
            if (player.getGameMode() != gm) {
                sender.sendMessage(ChatColor.GREEN + "[G] " + "ゲームモードの変更ができませんでした。");
                return true;
            }
            sender.sendMessage(ChatColor.GREEN + "[G] " + player.getName() + ": " + beforeGameMode.name() + " -> " + gm.name());
            return true;
        }
        sender.sendMessage(ChatColor.GREEN + "[G] " + "/g [0-3]");
        return true;
    }

}
