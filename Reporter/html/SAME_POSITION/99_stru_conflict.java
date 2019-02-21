<<<<<<< MINE
if (filterDirection) {
          assert query.getDirection() != Direction.BOTH;
          iter = Iterables.filter(iter, new Predicate<Entry>() {
              @Override public boolean apply(@Nullable Entry entry) {
                return edgeSerializer.parseDirection(entry) == query.getDirection();
              }
          });
        }
=======
return Iterables.transform(iter, new Function<Entry, TitanRelation>() {
            @Override public TitanRelation apply(@Nullable Entry entry) {
              return edgeSerializer.readRelation(v, entry);
            }
        }).iterator();
>>>>>>> YOURS

