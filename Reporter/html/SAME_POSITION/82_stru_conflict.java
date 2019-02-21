<<<<<<< MINE
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
>>>>>>> YOURS

