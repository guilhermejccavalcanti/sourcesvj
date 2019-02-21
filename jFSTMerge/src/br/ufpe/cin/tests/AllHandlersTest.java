package br.ufpe.cin.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	NewElementReferencingEditedOneHandlerTest.class,
	TypeAmbiguityErrorHandlerTest.class,
	RenamingConflictsHandlerTest.class,
	InitializationBlocksHandlerTest.class,
	DuplicatedDeclarationErrorsHandlerTest.class,
	DeletionsHandlerTest.class
})
public class AllHandlersTest {}
