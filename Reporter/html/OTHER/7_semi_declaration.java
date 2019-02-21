public FixedEnsembleProvider(String connectionString)
    {
<<<<<<< MINE
        this(connectionString, true);
=======
        Preconditions.checkArgument(!Strings.isNullOrEmpty(connectionString),
            "connectionString cannot be null or empty");
        this.connectionString = connectionString;
>>>>>>> YOURS
    }

