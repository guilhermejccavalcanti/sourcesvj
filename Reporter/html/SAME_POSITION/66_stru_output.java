package processing.app.contrib;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import processing.app.Base;
import processing.app.ui.Toolkit;

public class ContributionListPanel extends JPanel implements Scrollable, ContributionChangeListener {
  ContributionTab contributionTab;
  StatusPanel statusPanel;
  TreeMap<Contribution, ContributionPanel> panelByContribution;
  static HyperlinkListener nullHyperlinkListener = new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent e) {
      }
  };
  private ContributionPanel selectedPanel;
  private ContributionFilter filter;
  private ContributionListing contribListing = ContributionListing.getInstance();
  private JTable table;
  DefaultTableModel dtm;
  public ContributionListPanel(ContributionTab contributionTab, ContributionFilter filter, StatusPanel statusPanel) {
    super();
    this.contributionTab = contributionTab;
    this.statusPanel = statusPanel;
    this.filter = filter;
    setLayout(new GridBagLayout());
    setOpaque(true);
    if (Base.isLinux()) {
      setBackground(Color.white);
    }
    else {
      setBackground(UIManager.getColor("List.background"));
    }
    panelByContribution = new TreeMap<Contribution, ContributionPanel>(contribListing.getComparator());
    dtm = new MyTableModel();
    table = new JTable(dtm) {
        @Override public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
          Component c = super.prepareRenderer(renderer, row, column);
          if (isRowSelected(row)) {
            c.setBackground(Color.blue);
          }
          else {
            c.setBackground(Color.white);
          }
          return c;
        }
    };
    String[] colName = { "Status", "Name", "Author" } ;
    dtm.setColumnIdentifiers(colName);
    JScrollPane scrollPane = new JScrollPane(table);
    table.setFillsViewportHeight(true);
    table.setDefaultRenderer(Contribution.class, new StatusRendere());
    table.setRowHeight(30);
    table.setRowMargin(6);
    table.getColumnModel().setColumnMargin(-1);
    table.getColumnModel().getColumn(0).setMaxWidth(60);
    table.setShowGrid(false);
    table.setColumnSelectionAllowed(false);
    table.setCellSelectionEnabled(false);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    GroupLayout layout = new GroupLayout(this);
    layout.setHorizontalGroup(layout.createParallelGroup().addComponent(scrollPane));
    layout.setVerticalGroup(layout.createSequentialGroup().addComponent(scrollPane));
    this.setLayout(layout);
    table.setVisible(true);
  }
  
  class StatusRendere extends DefaultTableCellRenderer {
    @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Contribution contribution = (Contribution)value;
      JLabel label = new JLabel();
      if (column == 0) {
        Icon icon = null;
        label.setBorder(BorderFactory.createEmptyBorder(0, 17, 0, 0));
        if (contribution.isInstalled()) {
          icon = UIManager.getIcon("OptionPane.warningIcon");
          if (contribListing.hasUpdates(contribution)) {
            icon = Toolkit.getLibIcon("icons/pde-16.png");
          }
          if (!contribution.isCompatible(Base.getRevision())) {
            icon = Toolkit.getLibIcon("icons/pde-16.png");
          }
        }
        label.setIcon(icon);
        if (isSelected) {
          label.setBackground(Color.BLUE);
        }
        label.setOpaque(true);
      }
      else 
        if (column == 1) {
          JTextPane name = new JTextPane();
          name.setContentType("text/html");
          name.setEditable(false);
          name.setText("<html><body><b>" + contribution.getName() + "</b> - " + contribution.getSentence() + "</body></html>");
          GroupLayout layout = new GroupLayout(label);
          layout.setAutoCreateGaps(true);
          layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(name));
          layout.setVerticalGroup(layout.createParallelGroup().addComponent(name));
          if (table.isRowSelected(row)) {
            name.setBackground(Color.BLUE);
            name.setOpaque(true);
          }
          label.setLayout(layout);
        }
        else {
          JLabel icon = new JLabel(contribution.isSpecial() ? Toolkit.getLibIcon("icons/pde-16.png") : null);
          JTextPane author = new JTextPane();
          StringBuilder name = new StringBuilder("");
          String authorList = contribution.getAuthorList();
          if (authorList != null) {
            for (int i = 0; i < authorList.length(); i++) {
              if (authorList.charAt(i) == '[' || authorList.charAt(i) == ']') {
                continue ;
              }
              if (authorList.charAt(i) == '(') {
                i++;
                while (authorList.charAt(i) != ')'){
                  i++;
                }
              }
              else {
                name.append(authorList.charAt(i));
              }
            }
          }
          author.setText(name.toString());
          author.setEditable(false);
          author.setOpaque(false);
          if (table.isRowSelected(row)) {
            label.setBackground(Color.BLUE);
          }
          GroupLayout layout = new GroupLayout(label);
          layout.setHorizontalGroup(layout.createSequentialGroup().addContainerGap().addComponent(icon).addComponent(author));
          layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(author).addComponent(icon));
          label.setLayout(layout);
          label.setOpaque(true);
        }
      return label;
    }
  }
  
  class MyTableModel extends DefaultTableModel {
    MyTableModel() {
      super(0, 0);
    }
    @Override public boolean isCellEditable(int row, int column) {
      return false;
    }
    @Override public Class<?> getColumnClass(int columnIndex) {
      return Contribution.class;
    }
  }
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
  public void contributionAdded(final Contribution contribution) {
    if (filter.matches(contribution)) {
      EventQueue.invokeLater(new Runnable() {
          public void run() {
            if (!panelByContribution.containsKey(contribution)) {
              ContributionPanel newPanel = new ContributionPanel(ContributionListPanel.this);
              synchronized(panelByContribution) {
                panelByContribution.put(contribution, newPanel);
              }
              if (newPanel != null) {
                newPanel.setContribution(contribution);
                add(newPanel);
                updatePanelOrdering();
                updateColors();
              }
            }
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                  scrollRectToVisible(new Rectangle(0, 0, 1, 1));
                }
            });
          }
      });
    }
  }
  public void contributionRemoved(final Contribution contribution) {
    EventQueue.invokeLater(new Runnable() {
        public void run() {
          synchronized(panelByContribution) {
            ContributionPanel panel = panelByContribution.get(contribution);
            if (panel != null) {
              remove(panel);
              panelByContribution.remove(contribution);
            }
          }
          updatePanelOrdering();
          updateColors();
          updateUI();
        }
    });
  }
  public void contributionChanged(final Contribution oldContrib, final Contribution newContrib) {
    EventQueue.invokeLater(new Runnable() {
        public void run() {
          synchronized(panelByContribution) {
            ContributionPanel panel = panelByContribution.get(oldContrib);
            if (panel == null) {
              contributionAdded(newContrib);
            }
            else {
              panelByContribution.remove(oldContrib);
              panel.setContribution(newContrib);
              panelByContribution.put(newContrib, panel);
              updatePanelOrdering();
            }
          }
        }
    });
  }
  public void filterLibraries(List<Contribution> filteredContributions) {
    synchronized(panelByContribution) {
      Set<Contribution> hiddenPanels = new TreeSet<Contribution>(contribListing.getComparator());
      hiddenPanels.addAll(panelByContribution.keySet());
      for (Contribution info : filteredContributions) {
        ContributionPanel panel = panelByContribution.get(info);
        if (panel != null) {
          panel.setVisible(true);
          hiddenPanels.remove(info);
        }
      }
      for (Contribution info : hiddenPanels) {
        ContributionPanel panel = panelByContribution.get(info);
        if (panel != null) {
          panel.setVisible(false);
        }
      }
    }
  }
  protected void setSelectedPanel(ContributionPanel contributionPanel) {
    contributionTab.contributionManagerDialog.updateStatusPanel(contributionPanel);
    if (selectedPanel == contributionPanel) {
      selectedPanel.setSelected(true);
    }
    else {
      ContributionPanel lastSelected = selectedPanel;
      selectedPanel = contributionPanel;
      if (lastSelected != null) {
        lastSelected.setSelected(false);
      }
      contributionPanel.setSelected(true);
      updateColors();
      requestFocusInWindow();
      requestFocusInWindow();
    }
  }
  protected ContributionPanel getSelectedPanel() {
    return selectedPanel;
  }
  protected void updateColors() {
    int count = 0;
    synchronized(panelByContribution) {
      for (Entry<Contribution, ContributionPanel> entry : panelByContribution.entrySet()) {
        ContributionPanel panel = entry.getValue();
        if (panel.isVisible() && panel.isSelected()) {
          panel.setBackground(UIManager.getColor("List.selectionBackground"));
          panel.setForeground(UIManager.getColor("List.selectionForeground"));
          panel.setBorder(UIManager.getBorder("List.focusCellHighlightBorder"));
          count++;
        }
        else {
          Border border = null;
          if (panel.isVisible()) {
            if (Base.isMacOS()) {
              if (count % 2 == 1) {
                border = UIManager.getBorder("List.oddRowBackgroundPainter");
              }
              else {
                border = UIManager.getBorder("List.evenRowBackgroundPainter");
              }
            }
            else {
              if (count % 2 == 1) {
                panel.setBackground(new Color(219, 224, 229));
              }
              else {
                panel.setBackground(new Color(241, 241, 241));
              }
            }
            count++;
          }
          if (border == null) {
            border = BorderFactory.createEmptyBorder(1, 1, 1, 1);
          }
          panel.setBorder(border);
          panel.setForeground(UIManager.getColor("List.foreground"));
        }
      }
    }
  }
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }
  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    if (orientation == SwingConstants.VERTICAL) {
      int blockAmount = visibleRect.height;
      if (direction > 0) {
        visibleRect.y += blockAmount;
      }
      else {
        visibleRect.y -= blockAmount;
      }
      blockAmount += getScrollableUnitIncrement(visibleRect, orientation, direction);
      return blockAmount;
    }
    return 0;
  }
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    if (orientation == SwingConstants.VERTICAL) {
      int lastHeight = 0, height = 0;
      int bottomOfScrollArea = visibleRect.y + visibleRect.height;
      for (Component c : getComponents()) {
        if (c.isVisible()) {
          if (c instanceof ContributionPanel) {
            Dimension d = c.getPreferredSize();
            int nextHeight = height + d.height;
            if (direction > 0) {
              if (nextHeight > bottomOfScrollArea) {
                return nextHeight - bottomOfScrollArea;
              }
            }
            else {
              if (nextHeight > visibleRect.y) {
                if (visibleRect.y != height) {
                  return visibleRect.y - height;
                }
                else {
                  return visibleRect.y - lastHeight;
                }
              }
            }
            lastHeight = height;
            height = nextHeight;
          }
        }
      }
    }
    return 0;
  }
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }
  public boolean getScrollableTracksViewportWidth() {
    return true;
  }
}

