package c5.context;

/**
 * ContextHolder for holding context at thread local.
 * 
 * @author li
 * @since 2014
 */
public class ContextHolder {

	private static ThreadLocal<Context> context = new ThreadLocal<Context>();

	public static void init() {
		context.set(new Context());
	}

	public static void clean() {
		context.remove();
	}

	public static Context get() {
		return context.get();
	}
}
