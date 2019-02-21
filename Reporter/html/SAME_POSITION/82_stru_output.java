package com.sk89q.worldedit.commands;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.snapshots.InvalidSnapshotException;
import com.sk89q.worldedit.snapshots.Snapshot;
import com.sk89q.worldedit.snapshots.SnapshotRepository;
import com.sk89q.worldedit.*;

public class SnapshotCommands {
  private static Logger logger = Logger.getLogger("Minecraft.WorldEdit");
  private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
  @Command(aliases = {"list", }, usage = "[num]", desc = "List snapshots", min = 0, max = 1) @CommandPermissions(value = {"worldedit.snapshots.list", }) public static void list(CommandContext args, WorldEdit we, LocalSession session, LocalPlayer player, EditSession editSession) throws WorldEditException {
    LocalConfiguration config = we.getConfiguration();
    int num = args.argsLength() > 0 ? Math.min(40, Math.max(5, args.getInteger(0))) : 5;
    String worldName = player.getWorld().getName();
    SnapshotRepository repo = config.snapshotRepositories.get(worldName);
    if (repo == null) {
      player.printError("Snapshot/backup restore is not configured for this world.");
      return ;
    }
    List<Snapshot> snapshots = repo.getSnapshots(true);
    if (snapshots.size() > 0) {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\worldedit\revisions\rev_9343b3d_73a8646\rev_left_9343b3d\src\main\java\com\sk89q\worldedit\commands\SnapshotCommands.java
if (snapshots.size() > 0) {
        for (byte i = 0; i < Math.min(num, snapshots.size()); ++i) {
          player.print((i + 1) + ". " + snapshots.get(i).getName());
        }
        player.print("Use /snap use [snapshot] or /snap use latest.");
      }
      else {
        player.printError("No snapshots are available. See console for details.");
        File dir = config.snapshotRepo.getDirectory();
        try {
          logger.info("WorldEdit found no snapshots: looked in: " + dir.getCanonicalPath());
        }
        catch (IOException e) {
          logger.info("WorldEdit found no snapshots: looked in " + "(NON-RESOLVABLE PATH - does it exist?): " + dir.getPath());
        }
      }
=======
for (byte i = 0; i < Math.min(num, snapshots.size()); i++) {
        player.print((i + 1) + ". " + snapshots.get(i).getName());
      }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\worldedit\revisions\rev_9343b3d_73a8646\rev_right_73a8646\src\main\java\com\sk89q\worldedit\commands\SnapshotCommands.java

      if (snapshots.size() > 0) {
        for (byte i = 0; i < Math.min(num, snapshots.size()); ++i) {
          player.print((i + 1) + ". " + snapshots.get(i).getName());
        }
        player.print("Use /snap use [snapshot] or /snap use latest.");
      }
      else {
        player.printError("No snapshots are available. See console for details.");
        File dir = config.snapshotRepo.getDirectory();
        try {
          logger.info("WorldEdit found no snapshots: looked in: " + dir.getCanonicalPath());
        }
        catch (IOException e) {
          logger.info("WorldEdit found no snapshots: looked in " + "(NON-RESOLVABLE PATH - does it exist?): " + dir.getPath());
        }
      }
    }
    else {
      player.printError("No snapshots are available. See console for details.");
      File dir = repo.getDirectory();
      try {
        logger.info("WorldEdit found no snapshots: looked in: " + dir.getCanonicalPath());
      }
      catch (IOException e) {
        logger.info("WorldEdit found no snapshots: looked in " + "(NON-RESOLVABLE PATH - does it exist?): " + dir.getPath());
      }
    }
  }
  @Command(aliases = {"use", }, usage = "<snapshot>", desc = "Choose a snapshot to use", min = 1, max = 1) @CommandPermissions(value = {"worldedit.snapshots.restore", }) public static void use(CommandContext args, WorldEdit we, LocalSession session, LocalPlayer player, EditSession editSession) throws WorldEditException {
    LocalConfiguration config = we.getConfiguration();
    String worldName = player.getWorld().getName();
    SnapshotRepository repo = config.snapshotRepositories.get(worldName);
    if (repo == null) {
      player.printError("Snapshot/backup restore is not configured for this world.");
      return ;
    }
    String name = args.getString(0);
    if (name.equalsIgnoreCase("latest")) {
      Snapshot snapshot = repo.getDefaultSnapshot();
      if (snapshot != null) {
        session.setSnapshot(player.getWorld(), null);
        player.print("Now using newest snapshot.");
      }
      else {
        player.printError("No snapshots were found.");
      }
    }
    else {
      try {
        session.setSnapshot(player.getWorld(), repo.getSnapshot(name));
        player.print("Snapshot set to: " + name);
      }
      catch (InvalidSnapshotException e) {
        player.printError("That snapshot does not exist or is not available.");
      }
    }
  }
  @Command(aliases = {"before", }, usage = "<date>", desc = "Choose the nearest snapshot before a date", min = 1, max = -1) @CommandPermissions(value = {"worldedit.snapshots.restore", }) public static void before(CommandContext args, WorldEdit we, LocalSession session, LocalPlayer player, EditSession editSession) throws WorldEditException {
    LocalConfiguration config = we.getConfiguration();
    String worldName = player.getWorld().getName();
    SnapshotRepository repo = config.snapshotRepositories.get(worldName);
    if (repo == null) {
      player.printError("Snapshot/backup restore is not configured for this world.");
      return ;
    }
    Calendar date = session.detectDate(args.getJoinedStrings(0));
    if (date == null) {
      player.printError("Could not detect the date inputted.");
    }
    else {
      dateFormat.setTimeZone(session.getTimeZone());
      Snapshot snapshot = repo.getSnapshotBefore(date);
      if (snapshot == null) {
        player.printError("Couldn\'t find a snapshot before " + dateFormat.format(date.getTime()) + ".");
      }
      else {
        session.setSnapshot(player.getWorld(), snapshot);
        player.print("Snapshot set to: " + snapshot.getName());
      }
    }
  }
  @Command(aliases = {"after", }, usage = "<date>", desc = "Choose the nearest snapshot after a date", min = 1, max = -1) @CommandPermissions(value = {"worldedit.snapshots.restore", }) public static void after(CommandContext args, WorldEdit we, LocalSession session, LocalPlayer player, EditSession editSession) throws WorldEditException {
    LocalConfiguration config = we.getConfiguration();
    String worldName = player.getWorld().getName();
    SnapshotRepository repo = config.snapshotRepositories.get(worldName);
    if (repo == null) {
      player.printError("Snapshot/backup restore is not configured for this world.");
      return ;
    }
    Calendar date = session.detectDate(args.getJoinedStrings(0));
    if (date == null) {
      player.printError("Could not detect the date inputted.");
    }
    else {
      dateFormat.setTimeZone(session.getTimeZone());
      Snapshot snapshot = repo.getSnapshotAfter(date);
      if (snapshot == null) {
        player.printError("Couldn\'t find a snapshot after " + dateFormat.format(date.getTime()) + ".");
      }
      else {
        session.setSnapshot(player.getWorld(), snapshot);
        player.print("Snapshot set to: " + snapshot.getName());
      }
    }
  }
}

