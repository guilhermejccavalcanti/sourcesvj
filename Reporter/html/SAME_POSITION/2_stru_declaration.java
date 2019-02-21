  private void showReport() {
    final String report = currentModel.getReport(statisticsUI);
    if (report != null) {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\gephi\revisions\rev_90690bb_7fe6cf7\rev_left_90690bb\DesktopStatistics\src\org\gephi\desktop\statistics\StatisticsFrontEnd.java
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
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\gephi\revisions\rev_90690bb_7fe6cf7\rev_right_7fe6cf7\DesktopStatistics\src\org\gephi\desktop\statistics\StatisticsFrontEnd.java

      if (report != null) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              SimpleHTMLReport dialog = new SimpleHTMLReport(WindowManager.getDefault().getMainWindow(), report);
            }
        });
      }
    }
  }


