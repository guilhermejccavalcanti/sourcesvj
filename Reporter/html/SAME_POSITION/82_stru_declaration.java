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
  }


