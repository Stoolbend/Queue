package me.Stoolbend.Queue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class QueueCMqueue
  implements CommandExecutor
{
  public static Queue plugin;
  public static Server server;
  public static List<LivingEntity> entities;
  protected static Configuration CONFIG;
  public Connection QDB;

  public QueueCMqueue(Queue instance)
  {
    plugin = instance;
    try
    {
      CONFIG = Queue.config;
      CONFIG.load();
      String sql_host = CONFIG.getString("host", "localhost");
      int sql_port = CONFIG.getInt("port", 3306);
      String sql_db = CONFIG.getString("database", "craftlist");
      String sql_user = CONFIG.getString("username", "root");
      String sql_pass = CONFIG.getString("password", "root");
      this.QDB = DriverManager.getConnection("jdbc:mysql://" + sql_host + ":" + sql_port + "/" + sql_db + "?user=" + sql_user + "&password=" + sql_pass);
    }
    catch (SQLException E) {
      E.printStackTrace();
    }
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if (label.equalsIgnoreCase("queue")) {
      Player player = (Player)sender;
      Statement stmt = null;
      ResultSet rs = null;
      int inQueue = 0;
      try
      {
        stmt = this.QDB.createStatement();
        rs = stmt.executeQuery("SELECT COUNT(*) FROM `queue` WHERE `player` = '" + player.getName() + "' LIMIT 0,1;");
        rs.next();
        inQueue = rs.getInt(1);
      }
      catch (SQLException e) {
        e.printStackTrace();
      }
      if (inQueue == 1)
      {
        int curId = 0;
        try {
          stmt = this.QDB.createStatement();
          rs = stmt.executeQuery("SELECT * FROM `queue` WHERE player = '" + player.getName() + "';");
          rs.next();
          curId = rs.getInt("id");
        }
        catch (SQLException e) {
          e.printStackTrace();
        }
        try {
          stmt = this.QDB.createStatement();
          rs = stmt.executeQuery("SELECT COUNT(*) FROM `queue` WHERE id <= '" + curId + "';");
          rs.next();
          sender.sendMessage(ChatColor.YELLOW + "[Queue] You are currently number " + rs.getInt(1) + " in the queue.");
        }
        catch (SQLException e) {
          e.printStackTrace();
        }
      }
      else
      {
        try {
          stmt = this.QDB.createStatement();
          stmt.executeUpdate("INSERT INTO `queue`.`queue` (`id` ,`player`) VALUES (NULL , '" + player.getName() + "');");
          sender.sendMessage(ChatColor.GREEN + "[Queue] You have been added to the queue! use /queue to check your position");
        }
        catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return false;
  }
}