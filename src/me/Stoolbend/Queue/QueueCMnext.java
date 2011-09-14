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
import org.bukkit.util.config.Configuration;

public class QueueCMnext
  implements CommandExecutor
{
  public static Queue plugin;
  public static Server server;
  public static List<LivingEntity> entities;
  protected static Configuration CONFIG;
  public Connection QDB;

  public QueueCMnext(Queue instance)
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
    if (label.equalsIgnoreCase("next")) {
      if (sender.hasPermission("queue.see"))
      {
        Statement stmt = null;
        ResultSet rs = null;
        int queue = 0;

        if ((args.length == 1) && (args[0].equalsIgnoreCase("accept")))
        {
          try {
            stmt = this.QDB.createStatement();
            rs = stmt.executeQuery("SELECT COUNT(*) FROM `queue`;");
            rs.next();
            queue = rs.getInt(1);
          }
          catch (SQLException e) {
            e.printStackTrace();
          }

          if (queue > 0)
          {
            try
            {
              stmt = this.QDB.createStatement();
              rs = stmt.executeQuery("SELECT * FROM `queue` ORDER BY `id` ASC LIMIT 0 , 1;");
              rs.next();
              stmt.executeUpdate("DELETE FROM `queue` WHERE `player` = '" + rs.getString("player") + "';");
              sender.sendMessage(ChatColor.GREEN + "[Queue] Done! The next person is now available on /next");
            }
            catch (SQLException e) {
              e.printStackTrace();
            }
          }
          else
          {
            sender.sendMessage(ChatColor.DARK_RED + "[Queue] There is nobody in the queue...");
          }
        }
        try
        {
          stmt = this.QDB.createStatement();
          rs = stmt.executeQuery("SELECT COUNT(*) FROM `queue`;");
          rs.next();
          sender.sendMessage(ChatColor.YELLOW + "[Queue] There are currently " + rs.getInt(1) + " players awaiting your help.");
          stmt = this.QDB.createStatement();
          rs = stmt.executeQuery("SELECT * FROM `queue` ORDER BY `id` ASC LIMIT 0 , 1;");
          rs.next();
          sender.sendMessage(ChatColor.YELLOW + "[Queue] The next player in the queue is " + ChatColor.AQUA + rs.getString("player") + ChatColor.YELLOW + ".");
          sender.sendMessage(ChatColor.YELLOW + "[Queue] Type /next accept to help them " + ChatColor.RED + "(or remove them from the queue)" + ChatColor.YELLOW + ".");
        }
        catch (SQLException e) {
          e.printStackTrace();
        }
      }
      else {
        sender.sendMessage(ChatColor.DARK_RED + "[Queue] You dont have permission to do that...");
      }
    }
    return false;
  }
}