package br.ufpe.cin.mergers;

import java.io.File;

import br.ufpe.cin.exceptions.ExceptionUtils;
import br.ufpe.cin.exceptions.StructuredMergeException;
import br.ufpe.cin.mergers.util.MergeContext;
import de.fosd.jdime.Main;

/**
 * Represents structured merge. Internally calls JDime to perform structured
 * merge.
 * 
 * @author Guilherme
 *
 */
public class StructuredMerge {

	public static String merge(File left, File base, File right, MergeContext context) throws StructuredMergeException {
		String structuredMergeResult = null;
		try {
			structuredMergeResult = Main.run(left, base, right, context.differences);
		} catch (Exception | Error e) {
			throw new StructuredMergeException(ExceptionUtils.getCauseMessage(e), context);
		}
		return structuredMergeResult;
	}
	
	//FPFN
	public static String merge(File left, File base, File right, MergeContext context, boolean isOrdered) throws StructuredMergeException {
		String structuredMergeResult = null;
		try {
			structuredMergeResult = SemistructuredMerge.merge(left, base, right, context, isOrdered);
		} catch (Exception | Error e) {
			throw new StructuredMergeException(ExceptionUtils.getCauseMessage(e), context);
		}
		return structuredMergeResult;
	}

	//	public static String merge(File left, File base, File right, MergeContext context) throws StructuredMergeException {
	//		String structuredMergeResult = null;
	//		try {
	//			// String[] args = {"-mode", "structured", left.getPath(),
	//			// base.getPath(), right.getPath()};
	//			// structuredMergeResult = Main.run(left,base,right);
	//
	//			// FPFN
	//			// structuredMergeResult = Main.run(left,base,right,
	//			// context.differences);
	//			// structuredMergeResult = (new Main()).run(left,base,right,
	//			// context.differences);
	//
	//			final AtomicReference<Throwable> texception = new AtomicReference<Throwable>();
	//			final AtomicReference<String> output = new AtomicReference<String>();
	//			ExecutorService executor = Executors.newSingleThreadExecutor();
	//			@SuppressWarnings({ "unchecked", "rawtypes" })
	//			Future<String> future = executor.submit(new Callable() {
	//				public Object call() throws Exception {
	//					try {
	//
	//						String s = Main.run(left, base, right,
	//								context.differences);
	//						output.set(s);
	//
	//					} catch (Exception | Error e) {
	//						texception.set(e);
	//					}
	//					return null;
	//				}
	//			});
	//			try {
	//				future.get(30, TimeUnit.SECONDS); // timeout after 30 seconds
	//			} catch (Exception e) {
	//				future.cancel(true);
	//				executor.shutdownNow();
	//				throw e;
	//			}
	//			executor.shutdownNow();
	//			if (texception.get() != null) {
	//				throw new Exception();
	//			}
	//			structuredMergeResult = output.get();
	//		} catch (Exception | Error e) {
	//			throw new StructuredMergeException(ExceptionUtils.getCauseMessage(e), context);
	//		}
	//		return structuredMergeResult;
	//	}
}
