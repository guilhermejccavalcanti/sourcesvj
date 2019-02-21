      @Override public Iterator<TitanRelation> execute(final VertexCentricQuery query, final SliceQuery sq, final Object exeInfo) {
        assert exeInfo == null;
        if (query.getVertex().isNew()) 
          return Iterators.emptyIterator();
        final InternalVertex v = query.getVertex();
        Iterable<Entry> iter = v.loadRelations(sq, new Retriever<SliceQuery, List<Entry>>() {
            @Override public List<Entry> get(SliceQuery query) {
              return graph.edgeQuery(v.getID(), query, txHandle);
            }
        });
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\titan\revisions\rev_4935a97_c136530\rev_left_4935a97\titan-core\src\main\java\com\thinkaurelius\titan\graphdb\transaction\StandardTitanTx.java
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
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\titan\revisions\rev_4935a97_c136530\rev_right_c136530\titan-core\src\main\java\com\thinkaurelius\titan\graphdb\transaction\StandardTitanTx.java

        return Iterables.transform(iter, new Function<Entry, TitanRelation>() {
            @Override public TitanRelation apply(@Nullable Entry entry) {
              return edgeSerializer.readRelation(v, entry);
            }
        }).iterator();
      }


