public TextAreaPopup() {
<<<<<<< MINE
      cutItem = new JMenuItem("Cut");
=======
      JMenuItem item;

      cutItem = new JMenuItem(Language.text("menu.edit.cut"));
>>>>>>> YOURS
      cutItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            handleCut();
          }
      });
      this.add(cutItem);

      copyItem = new JMenuItem(Language.text("menu.edit.copy"));
      copyItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            handleCopy();
          }
        });
      this.add(copyItem);

      discourseItem = new JMenuItem(Language.text("menu.edit.copy_as_html"));
      discourseItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            handleCopyAsHTML();
          }
        });
      this.add(discourseItem);

<<<<<<< MINE
      pasteItem = new JMenuItem("Paste");
      pasteItem.addActionListener(new ActionListener() {
=======
      item = new JMenuItem(Language.text("menu.edit.paste"));
      item.addActionListener(new ActionListener() {
>>>>>>> YOURS
          public void actionPerformed(ActionEvent e) {
            handlePaste();
          }
        });
      this.add(pasteItem);

<<<<<<< MINE
      selectAllItem = new JMenuItem("Select All");
      selectAllItem.addActionListener(new ActionListener() {
=======
      item = new JMenuItem(Language.text("menu.edit.select_all"));
      item.addActionListener(new ActionListener() {
>>>>>>> YOURS
        public void actionPerformed(ActionEvent e) {
          handleSelectAll();
        }
      });
      this.add(selectAllItem);

      this.addSeparator();

<<<<<<< MINE
      commUncommItem = new JMenuItem("Comment/Uncomment");
      commUncommItem.addActionListener(new ActionListener() {
=======
      item = new JMenuItem(Language.text("menu.edit.comment_uncomment"));
      item.addActionListener(new ActionListener() {
>>>>>>> YOURS
          public void actionPerformed(ActionEvent e) {
            handleCommentUncomment();
          }
      });
      this.add(commUncommItem);

<<<<<<< MINE
      incIndItem = new JMenuItem("Increase Indent");
      incIndItem.addActionListener(new ActionListener() {
=======
      item = new JMenuItem("\u2192 "+Language.text("menu.edit.increase_indent"));
      item.addActionListener(new ActionListener() {
>>>>>>> YOURS
          public void actionPerformed(ActionEvent e) {
            handleIndentOutdent(true);
          }
      });
      this.add(incIndItem);

<<<<<<< MINE
      decIndItem = new JMenuItem("Decrease Indent");
      decIndItem.addActionListener(new ActionListener() {
=======
      item = new JMenuItem("\u2190 "+Language.text("menu.edit.decrease_indent"));
      item.addActionListener(new ActionListener() {
>>>>>>> YOURS
          public void actionPerformed(ActionEvent e) {
            handleIndentOutdent(false);
          }
      });
      this.add(decIndItem);

      this.addSeparator();

      referenceItem = new JMenuItem(Language.text("find_in_reference"));
      referenceItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            handleFindReference();
          }
        });
      this.add(referenceItem);

      Toolkit.setMenuMnemonics(cutItem, copyItem, discourseItem,
        pasteItem, selectAllItem, commUncommItem, incIndItem, 
	decIndItem, referenceItem);
    }

