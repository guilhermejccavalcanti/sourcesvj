<<<<<<< MINE
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
>>>>>>> YOURS

