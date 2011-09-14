package me.Stoolbend.Queue;

import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class Queue extends JavaPlugin
{
  Logger log = Logger.getLogger("Minecraft");

  private int taskId = 0;
  public static Server server;
  protected static Configuration config;

  public void onDisable()
  {
    getServer().getScheduler().cancelTask(this.taskId);
    this.log.info("[Q] Queue - DISABLED");
  }

  public void onEnable() {
    this.log.info("[Q] Loading plugin...");
    config = getConfiguration();

    if (config.getInt("system", 0) == 0) {
      this.log.info("[Q] WARN! - config.yml was not found or corrupted!");
      config.setHeader("# Queue 1.0 - Please make sure that you have run the included Setup.sql file on your MySQL Database.");
      config.setProperty("host", "localhost");
      config.setProperty("port", Integer.valueOf(3306));
      config.setProperty("database", "queue");
      config.setProperty("username", "root");
      config.setProperty("password", "root");
      config.setProperty("system", Integer.valueOf(1));
      config.save();
      this.log.info("[Q] WARN! - Generated new config.yml");
    }

    getCommand("queue").setExecutor(new QueueCMqueue(this));
    QueueCMqueue.server = getServer();
    server = getServer();
    this.log.info("[Q] /queue Command active");

    getCommand("next").setExecutor(new QueueCMnext(this));
    QueueCMnext.server = getServer();
    server = getServer();
    this.log.info("[Q] /next Command active");

    this.log.info("[Q] Queue - ENABLED");
  }
}