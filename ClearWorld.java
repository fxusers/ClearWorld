package ru.cubelife.clearworld;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ClearWorld extends JavaPlugin 
{
   /** Конфиг */
   private FileConfiguration cfg;
   /** Файл конфига */
   private File cfgFile;
   /** Время в днях */
   private long t;
   
   /** Время в миллисекундах */
   public static long time;
   
   /** Вызывается при включении */
   public void onEnable() 
   {
      cfgFile = new File(getDataFolder(), "config.yml");
      cfg = YamlConfiguration.loadConfiguration(cfgFile);
      loadCfg(); // Загружаем настройки
      saveCfg(); // Сохраняем настройки
      
      PluginManager pm = getServer().getPluginManager();
      if (pm.getPlugin("WorldGuard") == null) 
         return;

      new AutoCleaner().start(); // Запускает в отдельном потоке AutoCleaner
   }

   /** Загружает конфиг */
   private void loadCfg() 
   {
      t = cfg.getInt("time", 30); // Время в днях
      time = (t * 24 * 3600 * 1000);
   }
   
   /** Сохраняет конфиг */
   private void saveCfg() 
   {
      cfg.set("time", t);
      cfg.save(cfgFile);
   }
}
