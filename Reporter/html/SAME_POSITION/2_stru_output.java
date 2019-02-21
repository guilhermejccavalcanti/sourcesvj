package org.gephi.desktop.statistics;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.gephi.statistics.api.StatisticsController;
import org.gephi.statistics.api.StatisticsModel;
import org.gephi.statistics.spi.StatisticsUI;
import org.gephi.ui.components.SimpleHTMLReport;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.longtask.api.LongTaskListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

public class StatisticsFrontEnd extends javax.swing.JPanel {
  private StatisticsUI statisticsUI;
  private final String RUN;
  private final String CANCEL;
  private Statistics currentStatistics;
  private StatisticsModel currentModel;
  public StatisticsFrontEnd(StatisticsUI ui) {
    initComponents();
    RUN = NbBundle.getMessage(StatisticsFrontEnd.class, "StatisticsFrontEnd.runStatus.run");
    CANCEL = NbBundle.getMessage(StatisticsFrontEnd.class, "StatisticsFrontEnd.runStatus.cancel");
    initUI(ui);
    runButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (runButton.getText().equals(RUN)) {
            run();
          }
          else {
            cancel();
          }
        }
    });
    reportButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          showReport();
        }
    });
  }
  private void initUI(StatisticsUI ui) {
    this.statisticsUI = ui;
    displayLabel.setText(ui.getDisplayName());
    busyLabel.setVisible(false);
    runButton.setEnabled(false);
    runButton.setText(RUN);
    reportButton.setEnabled(false);
  }
  public void refreshModel(StatisticsModel model) {
    currentModel = model;
    if (model == null) {
      runButton.setText(RUN);
      runButton.setEnabled(false);
      busyLabel.setBusy(false);
      busyLabel.setVisible(false);
      reportButton.setEnabled(false);
      resultLabel.setText("");
      currentStatistics = null;
      return ;
    }
    runButton.setEnabled(true);
    if (model.isRunning(statisticsUI)) {
      runButton.setText(CANCEL);
      busyLabel.setVisible(true);
      busyLabel.setBusy(true);
      reportButton.setEnabled(false);
      resultLabel.setText("");
      if (currentStatistics == null) {
        currentStatistics = currentModel.getRunning(statisticsUI);
      }
    }
    else {
      runButton.setText(RUN);
      busyLabel.setBusy(false);
      busyLabel.setVisible(false);
      currentStatistics = null;
      refreshResult(model);
    }
  }
  private void refreshResult(StatisticsModel model) {
    String result = model.getResult(statisticsUI);
    if (result != null) {
      resultLabel.setText(result);
      reportButton.setEnabled(true);
      reportButton.setEnabled(true);
    }
    else {
      resultLabel.setText("");
      reportButton.setEnabled(false);
    }
  }
  private void run() {
    StatisticsController controller = Lookup.getDefault().lookup(StatisticsController.class);
    StatisticsBuilder builder = controller.getBuilder(statisticsUI.getStatisticsClass());
    currentStatistics = builder.getStatistics();
    if (currentStatistics != null) {
      LongTaskListener listener = new LongTaskListener() {
          public void taskFinished(LongTask task) {
            showReport();
          }
      };
      JPanel settingsPanel = statisticsUI.getSettingsPanel();
      if (settingsPanel != null) {
        statisticsUI.setup(currentStatistics);
        DialogDescriptor dd = new DialogDescriptor(settingsPanel, NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsFrontEnd.settingsPanel.title", builder.getName()));
        if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
          statisticsUI.unsetup();
          controller.execute(currentStatistics, listener);
        }
      }
      else {
        statisticsUI.setup(currentStatistics);
        controller.execute(currentStatistics, listener);
      }
    }
    else {
      statisticsUI.setup(currentStatistics);
      controller.execute(currentStatistics, listener);
    }
    LongTaskListener listener = new LongTaskListener() {
        public void taskFinished(LongTask task) {
          showReport();
        }
    };
    JPanel settingsPanel = statisticsUI.getSettingsPanel();
    if (settingsPanel != null) {
      LongTaskListener listener = new LongTaskListener() {
          public void taskFinished(LongTask task) {
            showReport();
          }
      };
      JPanel settingsPanel = statisticsUI.getSettingsPanel();
      if (settingsPanel != null) {
        statisticsUI.setup(currentStatistics);
        DialogDescriptor dd = new DialogDescriptor(settingsPanel, NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsFrontEnd.settingsPanel.title", builder.getName()));
        if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
          statisticsUI.unsetup();
          controller.execute(currentStatistics, listener);
        }
      }
      else {
        statisticsUI.setup(currentStatistics);
        controller.execute(currentStatistics, listener);
      }
    }
    else {
      statisticsUI.setup(currentStatistics);
      controller.execute(currentStatistics, listener);
    }
  }
  private void cancel() {
    if (currentStatistics != null && currentStatistics instanceof LongTask) {
      LongTask longTask = (LongTask)currentStatistics;
      longTask.cancel();
    }
  }
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
  @SuppressWarnings(value = {"unchecked", }) private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;
    busyLabel = new org.jdesktop.swingx.JXBusyLabel(new Dimension(16, 16));
    displayLabel = new javax.swing.JLabel();
    resultLabel = new javax.swing.JLabel();
    toolbar = new javax.swing.JToolBar();
    runButton = new javax.swing.JButton();
    reportButton = new javax.swing.JButton();
    setOpaque(false);
    setLayout(new java.awt.GridBagLayout());
    busyLabel.setText(org.openide.util.NbBundle.getMessage(StatisticsFrontEnd.class, "StatisticsFrontEnd.busyLabel.text"));
    busyLabel.setMinimumSize(new java.awt.Dimension(16, 16));
    busyLabel.setPreferredSize(new java.awt.Dimension(16, 16));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    add(busyLabel, gridBagConstraints);
    displayLabel.setText(org.openide.util.NbBundle.getMessage(StatisticsFrontEnd.class, "StatisticsFrontEnd.displayLabel.text"));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
    add(displayLabel, gridBagConstraints);
    resultLabel.setText(org.openide.util.NbBundle.getMessage(StatisticsFrontEnd.class, "StatisticsFrontEnd.resultLabel.text"));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 3);
    add(resultLabel, gridBagConstraints);
    toolbar.setFloatable(false);
    toolbar.setRollover(true);
    toolbar.setOpaque(false);
    runButton.setText(org.openide.util.NbBundle.getMessage(StatisticsFrontEnd.class, "StatisticsFrontEnd.runButton.text"));
    runButton.setFocusable(false);
    runButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    runButton.setOpaque(false);
    runButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolbar.add(runButton);
    reportButton.setText(org.openide.util.NbBundle.getMessage(StatisticsFrontEnd.class, "StatisticsFrontEnd.reportButton.text"));
    reportButton.setFocusable(false);
    reportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    reportButton.setOpaque(false);
    reportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolbar.add(reportButton);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    add(toolbar, gridBagConstraints);
  }
  private org.jdesktop.swingx.JXBusyLabel busyLabel;
  private javax.swing.JLabel displayLabel;
  private javax.swing.JButton reportButton;
  private javax.swing.JLabel resultLabel;
  private javax.swing.JButton runButton;
  private javax.swing.JToolBar toolbar;
}

