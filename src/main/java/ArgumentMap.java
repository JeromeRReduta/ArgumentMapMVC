import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses and stores command-line arguments into simple flag/value pairs.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Summer 2021
 */
public class ArgumentMap {
	/**
	 * Stores command-line arguments in flag/value pairs.
	 */
	private final Map<String, String> map;

	/**
	 * Initializes this argument map.
	 */
	public ArgumentMap() {
		this.map = new HashMap<>();
	}

	/**
	 * Initializes this argument map and then parsers the arguments into
	 * flag/value pairs where possible. Some flags may not have associated values.
	 * If a flag is repeated, its value is overwritten.
	 *
	 * @param args the command line arguments to parse
	 */
	public ArgumentMap(String[] args) {
		this();
		parse(args);
	}

	/**
	 * Parses the arguments into flag/value pairs where possible. Some flags may
	 * not have associated values. If a flag is repeated, its value will be
	 * overwritten.
	 *
	 * @param args the command line arguments to parse
	 */
	public void parse(String[] args) {
		// Sanity checks - args isn't null or empty
		if (args == null) {
			throw new NullPointerException("args was null!");
		}
		if (args.length == 0) {
			return;
		}
		for (int i = 0; i < args.length - 1; i++) {
			if (isValue(args[i])) {
				continue;
			}
			String key = args[i];
			String value = isValue(args[i + 1])
					? args[i + 1]
					: null;
			map.put(key, value);
		}
		// Tail case - last one is flag (if last str is value, we do nothing)
		String last = args[args.length - 1];
		if (isFlag(last)) {
			map.put(last, null);
		}
		System.out.println(map);
	}

	/**
	 * Determines whether the argument is a flag. The argument is considered a
	 * flag if it is a dash "-" character followed by any letter character.
	 *
	 * @param arg the argument to test if its a flag
	 * @return {@code true} if the argument is a flag
	 *
	 * @see String#startsWith(String)
	 * @see String#length()
	 * @see String#codePointAt(int)
	 * @see Character#isLetter(int)
	 */
	public static boolean isFlag(String arg) {
		if (arg == null) {
			return false;
		}
		if (arg.length() < 2) {
			return false;
		}
		if (!arg.startsWith("-")) {
			return false;
		}
		return Character.isLetter(
				arg.charAt(1));

	}

	/**
	 * Determines whether the argument is a value. Anything that is not a flag is
	 * considered a value.
	 *
	 * @param arg the argument to test if its a value
	 * @return {@code true} if the argument is a value
	 */
	public static boolean isValue(String arg) {
		return !isFlag(arg);
	}

	/**
	 * Returns the number of unique flags.
	 *
	 * @return number of unique flags
	 */
	public int numFlags() {
		return map.size();
	}

	/**
	 * Determines whether the specified flag exists.
	 *
	 * @param flag the flag check
	 * @return {@code true} if the flag exists
	 */
	public boolean hasFlag(String flag) {
		return map.containsKey(flag);
	}

	/**
	 * Determines whether the specified flag is mapped to a non-null value.
	 *
	 * @param flag the flag to find
	 * @return {@code true} if the flag is mapped to a non-null value
	 */
	public boolean hasValue(String flag) {
		return map.get(flag) != null;
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link String}
	 * or null if there is no mapping.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped or {@code null} if
	 *         there is no mapping
	 */
	public String getString(String flag) {
		return map.get(flag);
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link String}
	 * or the default value if there is no mapping.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @param defaultValue the default value to return if there is no mapping
	 * @return the value to which the specified flag is mapped, or the default
	 *         value if there is no mapping
	 */
	public String getString(String flag, String defaultValue) {
		String value = map.get(flag);
		return value != null
				? value
				: defaultValue;
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link Path},
	 * or {@code null} if unable to retrieve this mapping (including being unable
	 * to convert the value to a {@link Path} or no value exists).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         unable to retrieve this mapping
	 *
	 * @see Path#of(String, String...)
	 */
	public Path getPath(String flag) {
		return getPath(flag, null);
	}

	/**
	 * Returns the value the specified flag is mapped as a {@link Path}, or the
	 * default value if unable to retrieve this mapping (including being unable to
	 * convert the value to a {@link Path} or if no value exists).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag the flag whose associated value will be returned
	 * @param defaultValue the default value to return if there is no valid
	 *        mapping
	 * @return the value the specified flag is mapped as a {@link Path}, or the
	 *         default value if there is no valid mapping
	 */
	public Path getPath(String flag, Path defaultValue) {
		String pathStr = getString(flag);
		try {
			return Path.of(pathStr);
		}
		catch (InvalidPathException | NullPointerException e) { // Path cannot resolve or pathStr = null
			return defaultValue;
		}
		catch (Exception e) { // Somehow a different exception occurs
			e.printStackTrace();
			return defaultValue;
		}
	}

	/**
	 * Returns the value the specified flag is mapped as an int value, or the
	 * default value if unable to retrieve this mapping (including being unable to
	 * convert the value to an int or if no value exists).
	 *
	 * @param flag the flag whose associated value will be returned
	 * @param defaultValue the default value to return if there is no valid
	 *        mapping
	 * @return the value the specified flag is mapped as a int, or the default
	 *         value if there is no valid mapping
	 */
	public int getInteger(String flag, int defaultValue) {
		String result = map.get(flag);
		try {
			return Integer.parseInt(result);
		}
		catch (NumberFormatException | NullPointerException e) {
			return defaultValue;
		}
		catch (Exception e) { // Unexpected errors
			e.printStackTrace();
			return defaultValue;
		}
	}

	@Override
	public String toString() {
		return this.map.toString();
	}
}
