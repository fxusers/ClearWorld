package ru.cubelife.clearworld;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class AutoCleaner extends Thread 
{
   /** Выполняется в новом потоке */
   public void run() 
   {
      Thread.sleep(60000); // После выполнения чистки ждем 1 минуту
      cleanAll(); // Вызываем чистку
   }
   
   /** Проверяет все регионы и чистит при необходимости */
   private void cleanAll() 
   {
      System.out.println("--========================== CLEAR WORLD ==========================--");
      WorldGuardPlugin wg = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard"); //Берем WorldGuard
      for (World w : Bukkit.getWorlds()) // Список миров
      {
         // <== Babar TODO Добавить проверку на ID мира
         if (w.getName() != "survival")
            continue;

         RegionManager m = wg.getRegionManager(w); // Список регионов
         for (ProtectedRegion rg : m.getRegions().values()) 
         {
            // <== Babar TODO Исключить __global__

            // <== Список владельцев
            DefaultDomain pOwner = rg.getOwners();  
            int i = 0;
            for(String pName : pOwner.getPlayers()) 
            {
               OfflinePlayer p = Bukkit.getOfflinePlayer(pName);
               if (!p.hasPlayedBefore()) // Пропускаем игрока, которого нет
               {
                  continue;
               }
               
               Player pl = p.getPlayer(); // Ссылка на игрока
               if (pl != null) 
               {
                  if (pl.hasPermission("clearworld.antidel")) 
                  {
                     break; // Выходим из цикла, если игрок имеет право на иммунитет к удалению своих регионов
                  }
               }

               long lastPlayed = p.getLastPlayed();
               long now = System.currentTimeMillis();
               if (now - ClearWorld.time >= lastPlayed) 
               {
                  i++;
               }
            }
            // ==> Список владельцев

            // <== Список участников
            DefaultDomain pMember = rg.getMembers();  
            for(String pName : pMember.getPlayers()) 
            {
               OfflinePlayer p = Bukkit.getOfflinePlayer(pName);
               if (!p.hasPlayedBefore()) // Пропускаем игрока, которого нет
               {
                  continue;
               }
               
               Player pl = p.getPlayer(); // Ссылка на игрока
               if (pl != null) 
               {
                  if (pl.hasPermission("clearworld.antidel")) 
                  {
                     break; // Выходим из цикла, если игрок имеет право на иммунитет к удалению своих регионов
                  }
               }

               long lastPlayed = p.getLastPlayed();
               long now = System.currentTimeMillis();
               if (now - ClearWorld.time >= lastPlayed) 
               {
                  i++;
               }
            }
            // ==> Список владельцев

            if (i >= (pMember.getPlayers().size() + pOwner.getPlayers().size())) 
            {
               System.out.println("Region Name:" + rg.getId() + 
                                  ". Coord: " + rg.getMaximumPoint().getBlockX() + " " + 
                                                rg.getMaximumPoint().getBlockY() + " " 
                                                rg.getMaximumPoint().getBlockZ());
            }
            Thread.sleep(1); // После выполнения чистки ждем 1 час
         }
      }
      System.out.println("--========================== CLEAR WORLD ==========================--");
   }
}
