package com.jaoafa.Roboot.Event;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class Event_MCBansReputationCheck implements Listener {
    @EventHandler
    public void OnLoginCheck(AsyncPlayerPreLoginEvent event) {
        String name = event.getName();
        UUID uuid = event.getUniqueId();

        try {
            String url = String.format("https://api.jaoafa.com/users/mcbans/%s", uuid.toString());
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().build();
            try (Response response = client.newCall(request).execute()) {
                if(response.code() == 404){
                    Bukkit.getOperators().stream()
                            .filter(p -> p.isOnline() && p.getPlayer() != null)
                            .forEach(p -> p.getPlayer().sendMessage(String.format("[%s|Reputation] Not Found (10?)", name)));
                    System.out.printf("[MCBansReputationCheck] %s -> Not Found (10?)%n", name);
                    ResponseBody body = response.body();
                    if(body != null){
                        System.out.printf("[MCBansReputationCheck] %s%n", body.string());
                    }
                    return;
                }
                if (response.code() != 200) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "[Login Failed] " + ChatColor.WHITE + "ログイン処理に失敗しました。\n" +
                            "少しおいてから再度ログインをお試しください。再試行しても状態が変わらない場合はお問い合わせください。");
                    Bukkit.getOperators().stream()
                            .filter(p -> p.isOnline() && p.getPlayer() != null)
                            .forEach(p -> p.getPlayer().sendMessage(String.format("[%s|Reputation] Error: %d", name, response.code())));
                    System.out.printf("[MCBansReputationCheck] %s -> Error: %d%n", name, response.code());
                    ResponseBody body = response.body();
                    if(body != null){
                        System.out.printf("[MCBansReputationCheck] %s%n", body.string());
                    }
                    return;
                }
                ResponseBody body = response.body();
                if(body == null){
                    return;
                }
                JSONObject json = new JSONObject(body.string());
                double reputation = json.getJSONObject("data").optDouble("reputation", -1);
                if(reputation < 3){
                    // 3未満
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "[Login Failed] " + ChatColor.WHITE + "ログイン処理に失敗しました。\n" +
                            "MCBansでのReputationが3以上必要です。");
                    Bukkit.getOperators().stream()
                            .filter(p -> p.isOnline() && p.getPlayer() != null)
                            .forEach(p -> p.getPlayer().sendMessage(String.format("[%s|Reputation] %f / 10 -> Deny", name, reputation)));
                    System.out.printf("[MCBansReputationCheck] %s -> %f / 10 -> Deny%n", name, reputation);
                    return;
                }
                Bukkit.getOperators().stream()
                        .filter(p -> p.isOnline() && p.getPlayer() != null)
                        .forEach(p -> p.getPlayer().sendMessage(String.format("[%s|Reputation] %f / 10", name, reputation)));
                System.out.printf("[MCBansReputationCheck] %s -> %f / 10%n", name, reputation);
            }
        }catch (IOException e){
            e.printStackTrace();

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "[Login Failed] " + ChatColor.WHITE + "ログイン処理に失敗しました。\n" +
                    "少しおいてから再度ログインをお試しください。再試行しても状態が変わらない場合はお問い合わせください。");
            Bukkit.getOperators().stream()
                    .filter(p -> p.isOnline() && p.getPlayer() != null)
                    .forEach(p -> p.getPlayer().sendMessage(String.format("[%s|Reputation] Error: %s", name, e.getMessage())));
            System.out.printf("[MCBansReputationCheck] %s -> Error: %s%n", name, e.getMessage());
        }
    }
}
