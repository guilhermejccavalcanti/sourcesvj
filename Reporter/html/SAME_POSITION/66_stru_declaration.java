  private void updatePanelOrdering() {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\processing\revisions\rev_44abe2f_1c1d660\rev_left_44abe2f\app\src\processing\app\contrib\ContributionListPanel.java
dtm.getDataVector().removeAllElements();
=======
if (contributionTab.contributionType != null) {
      int row = 0;
      for (Entry<Contribution, ContributionPanel> entry : panelByContribution.entrySet()) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = row++;
        c.anchor = GridBagConstraints.NORTH;
        add(entry.getValue(), c);
      }
    }
    else {
      int row = 0;
      for (Entry<Contribution, ContributionPanel> entry : panelByContribution.entrySet()) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = row++;
        c.anchor = GridBagConstraints.NORTH;
        add(entry.getValue(), c);
      }
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1;
      c.weighty = 1;
      c.gridx = 0;
      c.gridy = row++;
      c.anchor = GridBagConstraints.NORTH;
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\processing\revisions\rev_44abe2f_1c1d660\rev_right_1c1d660\app\src\processing\app\contrib\ContributionListPanel.java

    dtm.fireTableDataChanged();
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\processing\revisions\rev_44abe2f_1c1d660\rev_left_44abe2f\app\src\processing\app\contrib\ContributionListPanel.java
for (Entry<Contribution, ContributionPanel> entry : panelByContribution.entrySet()) {
      ((DefaultTableModel)table.getModel()).addRow(new Object[]{ entry.getKey(), entry.getKey(), entry.getKey() } );
    }
=======
if (contributionTab.contributionType != null) {
      int row = 0;
      for (Entry<Contribution, ContributionPanel> entry : panelByContribution.entrySet()) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = row++;
        c.anchor = GridBagConstraints.NORTH;
        add(entry.getValue(), c);
      }
    }
    else {
      int row = 0;
      for (Entry<Contribution, ContributionPanel> entry : panelByContribution.entrySet()) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = row++;
        c.anchor = GridBagConstraints.NORTH;
        add(entry.getValue(), c);
      }
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1;
      c.weighty = 1;
      c.gridx = 0;
      c.gridy = row++;
      c.anchor = GridBagConstraints.NORTH;
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\processing\revisions\rev_44abe2f_1c1d660\rev_right_1c1d660\app\src\processing\app\contrib\ContributionListPanel.java

  }


