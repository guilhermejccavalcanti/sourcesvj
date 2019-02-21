<<<<<<< MINE
if (report != null) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              SimpleHTMLReport dialog = new SimpleHTMLReport(WindowManager.getDefault().getMainWindow(), report);
            }
        });
      }
=======
SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            StatisticsReportPanel dialog = new StatisticsReportPanel(WindowManager.getDefault().getMainWindow(), report);
          }
      });
>>>>>>> YOURS

