package com.jaoafa.Roboot.Event;

import com.jaoafa.Roboot.Main;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

public class Event_AsyncJoin implements Listener {
    @EventHandler
    public void OnLoginCheck(AsyncPlayerPreLoginEvent event) {
        String name = event.getName();
        UUID uuid = event.getUniqueId();
        InetAddress ia = event.getAddress();
        String ip = ia.getHostAddress();
        String host = ia.getHostName();

        System.out.printf("[Event_AsyncJoin|OnLoginCheck] %s | UUID: %s%n", name, uuid.toString());
        System.out.printf("[Event_AsyncJoin|OnLoginCheck] %s | IP: %s%n", name, ip);
        System.out.printf("[Event_AsyncJoin|OnLoginCheck] %s | Host: %s%n", name, host);

        Country country = null;
        String countryName = null;
        City city;
        String cityName = null;
        if (!(ia.isAnyLocalAddress() || ia.isLoopbackAddress()) && !ip.startsWith("192.168")) {
            CityResponse res = getGeoIP(ia);
            if (res != null) {
                country = res.getCountry();
                countryName = country.getName();
                city = res.getCity();
                cityName = city.getName();

                System.out.printf("[Event_AsyncJoin|OnLoginCheck] %s | Country: %s%n", name, countryName);
                System.out.printf("[Event_AsyncJoin|OnLoginCheck] %s | City: %s%n", name, cityName);
            }
        }

        // 「jaotan」というプレイヤー名は禁止
        if (name.equalsIgnoreCase("jaotan")) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL,
                    ChatColor.RED + "[Login Denied! - Reason: UserName]\n"
                            + ChatColor.RESET + ChatColor.WHITE + "あなたのMinecraftIDは、システムの運用上の問題によりログイン不可能と判断されました。\n"
                            + ChatColor.RESET + ChatColor.AQUA + "ログインするには、MinecraftIDを変更してください。\n"
                            + ChatColor.RESET + ChatColor.WHITE + "もしこの判定が誤判定と思われる場合は、公式Discordへお問い合わせください。");
            System.out.printf("[Event_AsyncJoin|OnLoginCheck] %s -> UserName%n", name);
            return;
        }
        // 日本国外からのアクセスをすべて規制
        if (country != null && !countryName.equalsIgnoreCase("Japan")) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL,
                    ChatColor.RED + "[Login Denied! - Reason: Region restricted]\n"
                            + ChatColor.RESET + ChatColor.WHITE + "海外からのログインと判定されました。\n"
                            + ChatColor.RESET + ChatColor.AQUA + "当サーバでは、日本国外からのログインを禁止しています。\n"
                            + ChatColor.RESET + ChatColor.WHITE + "もしこの判定が誤判定と思われる場合は、公式Discordへお問い合わせください。");
            System.out.printf("[Event_AsyncJoin|OnLoginCheck] %s -> Region restricted: %s %s%n", name, countryName, cityName);
            return;
        }

        System.out.printf("[Event_AsyncJoin|OnLoginCheck] %s -> Login successful%n", name);
    }

    CityResponse getGeoIP(InetAddress ia) {
        JavaPlugin plugin = Main.getJavaPlugin();
        File file = new File(plugin.getDataFolder(), "GeoLite2-City.mmdb");
        if (!file.exists()) {
            plugin.getLogger().warning("GeoLite2-City.mmdb not found. Check Login failed.");
            return null;
        }

        try {
            DatabaseReader dr = new DatabaseReader.Builder(file).build();
            return dr.city(ia);
        } catch (IOException e) {
            plugin.getLogger().warning("IOException caught. getGeoIP failed.");
            e.printStackTrace();
            return null;
        } catch (GeoIp2Exception e) {
            plugin.getLogger().warning("GeoIp2Exception caught. getGeoIP failed.");
            e.printStackTrace();
            return null;
        }
    }
}
