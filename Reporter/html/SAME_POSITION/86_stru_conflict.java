<<<<<<< MINE
ls = handleFindByRange(m, client, ls, conditions, isRowKeyQuery);
=======
if (MetadataUtils.useSecondryIndex(m.getPersistenceUnit())) {
        ls = ((PelopsClient)client).find(this.conditions, m, true, null);
      }
      else {
        ls = onAssociationUsingLucene(m, client, ls);
      }
>>>>>>> YOURS

