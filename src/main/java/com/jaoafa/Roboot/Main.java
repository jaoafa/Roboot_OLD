package com.jaoafa.Roboot;

import com.jaoafa.Roboot.Command.Cmd_G;
import com.jaoafa.Roboot.Lib.ClassFinder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.util.Objects;

public class Main extends JavaPlugin {
    private static Main Main = null;
    /**
     * プラグインが起動したときに呼び出し
     *
     * @author mine_book000
     * @since 2019/08/20
     */
    @Override
    public void onEnable() {
        setMain(this);

        Objects.requireNonNull(getCommand("g")).setExecutor(new Cmd_G());

        registEvents();
        if (!isEnabled())
            return;

        for (World world : Bukkit.getWorlds()) {
            world.setThundering(false);
            world.setStorm(false);
        }
    }

    public void registEvents(){
        try {
            ClassFinder classFinder = new ClassFinder(this.getClassLoader());
            for (Class<?> clazz : classFinder.findClasses("com.jaoafa.Roboot.Event")) {
                if (!clazz.getName().startsWith("com.jaoafa.Roboot.Event.")) {
                    continue;
                }
                if (clazz.getEnclosingClass() != null) {
                    continue;
                }
                if (clazz.getName().contains("$")) {
                    continue;
                }

                Constructor<?> construct = clazz.getConstructor();
                Object instance = construct.newInstance();

                if (instance instanceof Listener) {
                    try {
                        Listener listener = (Listener) instance;
                        getServer().getPluginManager().registerEvents(listener, this);
                        getLogger().info(clazz.getSimpleName() + " registered");
                    } catch (ClassCastException e) {
                        // commandexecutor not implemented
                        getLogger().warning(clazz.getSimpleName() + ": Listener not implemented [1]");
                    }
                } else {
                    getLogger().warning(clazz.getSimpleName() + ": Listener not implemented [2]");
                }
            }
        } catch (Exception e) { // ClassFinder.findClassesがそもそもException出すので仕方ないという判断で。
            e.printStackTrace();
        }
    }

    public static JavaPlugin getJavaPlugin() {
        return Main;
    }

    public static Main getMain() {
        return Main;
    }

    public static void setMain(Main main) {
        Main = main;
    }
}
