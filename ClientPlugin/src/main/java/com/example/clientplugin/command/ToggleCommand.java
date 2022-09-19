package com.example.clientplugin.command;

import com.example.clientplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import ovh.cubecast.cubeapi.api.ChatUtil;
import ovh.cubecast.cubeapi.api.annotation.Permission;
import ovh.cubecast.cubeapi.api.command.AbstractCommand;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Permission(permissionString = "clientplugin.use", message = "&cYou dont have perms !!!")
public class ToggleCommand extends AbstractCommand<Player> {

    private final Main instance;

    private BukkitTask task = null;

    private boolean toggled = false;

    public ToggleCommand(Main main) {
        super("togglecommand", "ccommand", "clcom");
        this.instance = main;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] strings) {
        toggled = !toggled;

        if (toggled) {
            ChatUtil.INSTANCE.sendMessage(player, "&aMessage receiving from server enabled");

            String ip = instance.getConfig().getString("IP");
            int port = instance.getConfig().getInt("PORT");

            task = new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        Socket socket = new Socket(ip, port);

                        InputStream inputStream = socket.getInputStream();
                        DataInputStream dataInputStream = new DataInputStream(inputStream);

                        String message = dataInputStream.readUTF();

                        Bukkit.getOnlinePlayers().forEach(p ->
                                ChatUtil.INSTANCE.sendTitle(p, "", ChatUtil.INSTANCE.format("&a" + message), 10, 20, 10));
                    } catch (IOException ex) {
                        instance.getLogger().severe("Error when receiving message");

                        toggled = false;
                        task = null;

                        Bukkit.getOnlinePlayers().forEach(p ->
                                ChatUtil.INSTANCE.sendTitle(p, "", ChatUtil.INSTANCE.format("&cServer timeout"), 10, 20, 10));

                        cancel();
                    }
                }
            }.runTaskTimerAsynchronously(instance, 1L, 1L);
        } else {
            if (task != null)
                task.cancel();

            ChatUtil.INSTANCE.sendMessage(player, "&cMessage receiving from server disabled");
        }
    }

    @NotNull
    @Override
    public List<String> tab(@NotNull Player player, @NotNull String[] strings) {
        return new ArrayList<>();
    }

}
