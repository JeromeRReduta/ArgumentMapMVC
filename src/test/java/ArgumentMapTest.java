import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.TagFilter;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

/**
 * Tests for the {@link ArgumentMap} class.
 *
 * @see ArgumentMap
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Summer 2021
 */
@TestMethodOrder(MethodName.class)
public class ArgumentMapTest {
	/*
	 * Hint: Right-click a nested class to run the tests in that nested class
	 * only. Focus on tests in the order provided in the file. Only focus on one
	 * test at a time. Learn how to read the JUnit output!
	 */

	/**
	 * Tests for the {@link ArgumentMap#isFlag(String)} method.
	 */
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class A_FlagTests {
		/**
		 * Tests values that should be considered valid flags.
		 *
		 * @param flag valid flag value
		 */
		@Order(1)
		@ParameterizedTest(name = "[{index}: \"{0}\"]")
		@ValueSource(strings = { "-a", "-hello", "-hello world", "-trailing  ",
				"-résumé", "-über", "-abc123", "-with-dash", "-with_underscore" })
		public void testValidFlags(String flag) {
			boolean actual = ArgumentMap.isFlag(flag);
			Assertions.assertTrue(actual, flag);
		}

		/**
		 * Tests values that should be considered invalid flags.
		 *
		 * @param flag invalid flag value
		 */
		@Order(2)
		@ParameterizedTest(name = "[{index}: \"{0}\"]")
		@ValueSource(strings = { "a-b-c", "hello", "hello world", "", " ", "\t",
				"\n", "-", " - a", " -a", "\t-a", "--a", "-$a", "-_a", "-\ta", "97",
				"1", "-1" })
		public void testInvalidFlags(String flag) {
			boolean actual = ArgumentMap.isFlag(flag);
			Assertions.assertFalse(actual, flag);
		}

		/**
		 * Tests that null value should be considered an invalid flag.
		 */
		@Order(3)
		@Test
		public void testNullFlag() {
			boolean actual = ArgumentMap.isFlag(null);
			Assertions.assertFalse(actual, "null");
		}

		/**
		 * Tests a randomly generated string (with letters).
		 */
		@Order(4)
		@Test
		public void testRandomStringFlag() {
			Random random = new Random();

			int a = 'a'; // lowercase a codepoint
			int z = 'z'; // lowercase z codepoint

			// https://www.baeldung.com/java-random-string#java8-alphabetic
			String junk = random.ints(5, a, z + 1).collect(StringBuilder::new,
					StringBuilder::appendCodePoint, StringBuilder::append).toString();

			String flag = "-" + junk;

			boolean actual = ArgumentMap.isFlag(flag);
			Assertions.assertTrue(actual, flag);
		}

		/**
		 * Tests a randomly generated string (with digits).
		 */
		@Order(4)
		@Test
		public void testRandomDigitsFlag() {
			Random random = new Random();

			// https://www.baeldung.com/java-random-string#java8-alphabetic
			String junk = random.ints(5, 0, 10).mapToObj(i -> String.valueOf(i))
					.collect(Collectors.joining());

			String flag = "-" + junk;

			boolean actual = ArgumentMap.isFlag(flag);
			Assertions.assertFalse(actual, flag);
		}
	}

	/**
	 * Tests for the {@link ArgumentMap#numFlags()} method.
	 */
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class B_CountTests {
		/**
		 * Tests count for arguments with a single flag.
		 */
		@Order(1)
		@Test
		public void testOneFlag() {
			String[] args = { "-loquat" };
			int expected = 1;
			int actual = new ArgumentMap(args).numFlags();
			assertEquals(expected, actual);
		}

		/**
		 * Tests count for arguments with a single flag/value pair.
		 */
		@Order(2)
		@Test
		public void testOnePair() {
			String[] args = { "-grape", "raisin" };
			int expected = 1;
			int actual = new ArgumentMap(args).numFlags();
			assertEquals(expected, actual);
		}

		/**
		 * Tests count for arguments with two flags.
		 */
		@Order(3)
		@Test
		public void testTwoFlags() {
			String[] args = { "-tomato", "-potato" };
			int expected = 2;
			int actual = new ArgumentMap(args).numFlags();
			assertEquals(expected, actual);
		}

		/**
		 * Tests count for arguments with a single value.
		 */
		@Order(4)
		@Test
		public void testOnlyValue() {
			String[] args = { "rhubarb" };
			int expected = 0;
			int actual = new ArgumentMap(args).numFlags();
			assertEquals(expected, actual);
		}

		/**
		 * Tests count for arguments with two values.
		 */
		@Order(5)
		@Test
		public void testTwoValues() {
			String[] args = { "constant", "change" };

			int expected = 0;
			int actual = new ArgumentMap(args).numFlags();
			assertEquals(expected, actual);
		}

		/**
		 * Tests count for arguments with a non-pair value then flag.
		 */
		@Order(6)
		@Test
		public void testPineapple() {
			String[] args = { "pine", "-apple" };

			int expected = 1;
			int actual = new ArgumentMap(args).numFlags();
			assertEquals(expected, actual);
		}

		/**
		 * Tests count for arguments with two flag/value pairs.
		 */
		@Order(7)
		@Test
		public void testSquash() {
			String[] args = { "-aubergine", "eggplant", "-courgette", "zucchini" };

			int expected = 2;
			int actual = new ArgumentMap(args).numFlags();
			assertEquals(expected, actual);
		}

		/**
		 * Tests count for arguments with repeated flags.
		 */
		@Order(8)
		@Test
		public void testFruit() {
			String[] args = { "-tangerine", "satsuma", "-tangerine", "clementine",
					"-tangerine", "mandarin" };

			int expected = 1;
			int actual = new ArgumentMap(args).numFlags();
			assertEquals(expected, actual);
		}

		/**
		 * Tests count for arguments with no elements.
		 */
		@Order(9)
		@Test
		public void testEmpty() {
			String[] args = {};

			int expected = 0;
			int actual = new ArgumentMap(args).numFlags();
			assertEquals(expected, actual);
		}

		/**
		 * Tests count for null arguments.
		 */
		@Order(10)
		@Test
		public void testNull() {
			String[] args = null;

			// it is okay to throw a null pointer exception here
			assertThrows(java.lang.NullPointerException.class,
					() -> new ArgumentMap(args).numFlags());
		}
	}

	/**
	 * Tests how well the {@link ArgumentMap#parse(String[])} method works.
	 */
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class C_ParseTests {
		/**
		 * Checks if number of flags is correct for this test case.
		 */
		@Order(1)
		@Test
		public void testNumFlags() {
			int expected = 5;
			int actual = this.parser.numFlags();

			assertEquals(expected, actual, this.debug);
		}

		/**
		 * Checks if the -d flag was correctly parsed for this test case.
		 */
		@Order(2)
		@Test
		public void testHasFlag() {
			assertTrue(this.parser.hasFlag("-d"), this.debug);
		}

		/**
		 * Checks if the -f flag was correctly parsed for this test case.
		 */
		@Order(3)
		@Test
		public void testHasLastFlag() {
			assertTrue(this.parser.hasFlag("-f"), this.debug);
		}

		/**
		 * Checks if the -g flag does not exist as expected.
		 */
		@Order(4)
		@Test
		public void testHasntFlag() {
			assertFalse(this.parser.hasFlag("-g"), this.debug);
		}

		/**
		 * Checks if the -a value was correctly parsed for this test case.
		 */
		@Order(5)
		@Test
		public void testHasValue() {
			assertTrue(this.parser.hasValue("-a"), this.debug);
		}

		/**
		 * Checks if the -d value was correctly parsed for this test case.
		 */
		@Order(6)
		@Test
		public void testHasFlagNoValue() {
			assertFalse(this.parser.hasValue("-d"), this.debug);
		}

		/**
		 * Checks the value for a non-existent flag.
		 */
		@Order(7)
		@Test
		public void testNoFlagNoValue() {
			assertFalse(this.parser.hasValue("-g"), this.debug);
		}

		/**
		 * Checks if the -b value was correctly parsed for this test case.
		 */
		@Order(8)
		@Test
		public void testGetValueExists() {
			String expected = "bat";
			String actual = this.parser.getString("-b");
			assertEquals(expected, actual, this.debug);
		}

		/**
		 * Checks if the -d value was correctly parsed for this test case.
		 */
		@Order(9)
		@Test
		public void testGetValueNull() {
			String expected = null;
			String actual = this.parser.getString("-d");
			assertEquals(expected, actual, this.debug);
		}

		/**
		 * Checks the value for a non-existent flag.
		 */
		@Order(10)
		@Test
		public void testGetValueNoFlag() {
			String expected = null;
			String actual = this.parser.getString("-g");
			assertEquals(expected, actual, this.debug);
		}

		/**
		 * Checks if the -e flag was correctly parsed for this test case.
		 */
		@Order(11)
		@Test
		public void testGetValueRepeatedFlag() {
			String expected = null;
			String actual = this.parser.getString("-e");
			assertEquals(expected, actual, this.debug);
		}

		/**
		 * Checks if the -b flag was correctly parsed for this test case.
		 */
		@Order(12)
		@Test
		public void testGetDefaultExists() {
			String expected = "bat";
			String actual = this.parser.getString("-b", "bee");
			assertEquals(expected, actual, this.debug);
		}

		/**
		 * Checks if the -d flag was correctly parsed for this test case.
		 */
		@Order(13)
		@Test
		public void testGetDefaultNull() {
			String expected = "dog";
			String actual = this.parser.getString("-d", "dog");
			assertEquals(expected, actual, this.debug);
		}

		/**
		 * Checks if the default value is returned correctly for a non-existent
		 * flag.
		 */
		@Order(14)
		@Test
		public void testGetDefaultMissing() {
			String expected = "goat";
			String actual = this.parser.getString("-g", "goat");
			assertEquals(expected, actual, this.debug);
		}

		/**
		 * Checks that parsing the same arguments twice does not affect count.
		 */
		@Order(15)
		@Test
		public void testDoubleParse() {
			String[] args = { "-a", "42", "-b", "bat", "cat", "-d", "-e", "elk", "-e",
					"-f" };
			this.parser.parse(args);

			int expected = 5;
			int actual = this.parser.numFlags();

			assertEquals(expected, actual, this.debug);
		}
		
		/** ArgumentParser object being tested */
		private ArgumentMap parser;

		/** String used to output debug messages when tests fail. */
		private String debug;

		/**
		 * Sets up the parser object with a single test case.
		 */
		@BeforeEach
		public void setup() {
			String[] args = { "", "-a", "42", "-b", "bat", "cat", "-d", "-e", "elk",
					"-1", "-e", "-f" };

			this.parser = new ArgumentMap();
			this.parser.parse(args);

			this.debug = "\n" + this.parser.toString() + "\n";
		}

		/**
		 * Nullifies the parser object after each test.
		 */
		@AfterEach
		public void teardown() {
			this.parser = null;
		}
	}

	/**
	 * Tests for the {@link ArgumentMap#getPath(String)} and the
	 * {@link ArgumentMap#getInteger(String, int)} methods.
	 */
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class D_ValueTests {
		/**
		 * Checks that {@link ArgumentMap#getPath(String)} properly returns path.
		 */
		@Order(1)
		@Test
		public void testGetValidPath() {
			String[] args = { "-p", "." };
			ArgumentMap map = new ArgumentMap(args);

			Path expected = Path.of(".");
			Path actual = map.getPath("-p");
			assertEquals(expected, actual);
		}

		/**
		 * Checks that {@link ArgumentMap#getPath(String)} returns null as expected.
		 */
		@Order(2)
		@Test
		public void testGetInvalidPath() {
			String[] args = { "-p" };
			ArgumentMap map = new ArgumentMap(args);

			Path expected = null;
			Path actual = map.getPath("-p");
			assertEquals(expected, actual);
		}

		/**
		 * Checks that {@link ArgumentMap#getPath(String, Path)} returns value
		 * properly when flag/value pair exists.
		 */
		@Order(3)
		@Test
		public void testGetValidDefaultPath() {
			String[] args = { "-p", "." };
			ArgumentMap map = new ArgumentMap(args);

			Path expected = Path.of(".");
			Path actual = map.getPath("-p", Path.of(".."));
			assertEquals(expected, actual);
		}

		/**
		 * Checks that {@link ArgumentMap#getPath(String, Path)} returns value
		 * properly when flag/value pair does not exist.
		 */
		@Order(4)
		@Test
		public void testGetInvalidDefaultPath() {
			String[] args = { "-p" };
			ArgumentMap map = new ArgumentMap(args);

			Path expected = Path.of(".");
			Path actual = map.getPath("-p", Path.of("."));
			assertEquals(expected, actual);
		}

		/**
		 * Checks that {@link ArgumentMap#getInteger(String, int)} returns the
		 * expected value.
		 */
		@Order(5)
		@Test
		public void testGetIntegerPositive() {
			String[] args = { "-num", "42" };
			ArgumentMap map = new ArgumentMap(args);

			int expected = 42;
			int actual = map.getInteger("-num", 0);
			assertEquals(expected, actual);
		}

		/**
		 * Checks that {@link ArgumentMap#getInteger(String, int)} returns the
		 * expected value.
		 */
		@Order(6)
		@Test
		public void testGetIntegerNoValue() {
			String[] args = { "-num" };
			ArgumentMap map = new ArgumentMap(args);

			int expected = 0;
			int actual = map.getInteger("-num", 0);
			assertEquals(expected, actual);
		}

		/**
		 * Checks that {@link ArgumentMap#getInteger(String, int)} returns the
		 * expected value.
		 */
		@Order(7)
		@Test
		public void testGetIntegerLetterValue() {
			String[] args = { "-num", "hello" };
			ArgumentMap map = new ArgumentMap(args);

			int expected = 0;
			int actual = map.getInteger("-num", 0);
			assertEquals(expected, actual);
		}

		/**
		 * Checks that {@link ArgumentMap#getInteger(String, int)} returns the
		 * expected value.
		 */
		@Order(8)
		@Test
		public void testGetIntegerNegativeValue() {
			String[] args = { "-num", "-13" };
			ArgumentMap map = new ArgumentMap(args);

			int expected = -13;
			int actual = map.getInteger("-num", 0);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Imperfect tests to try and determine if the approach may have issues.
	 */
	@Tag("approach")
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class E_ApproachTests {
		/**
		 * Tests that the java.io.File class does not appear in the implementation
		 * code.
		 *
		 * @throws IOException if IO issue occurs
		 */
		@Test
		@Order(1)
		public void testFileClass() throws IOException {
			Assertions.assertFalse(source.contains("import java.io.File"),
					"Do not use the java.io.File class!");
		}

		/**
		 * Tests that the java.io.Paths class does not appear in the implementation
		 * code.
		 *
		 * @throws IOException if IO issue occurs
		 */
		@Test
		@Order(2)
		public void testPathsClass() throws IOException {
			Assertions.assertFalse(source.contains("import java.nio.file.Paths"),
					"Do not use the java.io.file.Paths class!");
		}

		/**
		 * Attempts to determine if looping is used improperly. There should only be
		 * one loop necessary for the parse method.
		 *
		 * @throws IOException if IO issue occurs
		 */
		@Test
		@Order(3)
		public void testLoopCount() throws IOException {
			String regex = "(?i)\\b(for|while)\\s*\\(";
			long count = Pattern.compile(regex).matcher(source).results().count();

			assertTrue(count <= 1, "Found " + count
					+ " loops in source code. Only 1 should be necessary.");
		}
		
		/**
		 * Causes this group of tests to fail if the other non-approach tests are
		 * not yet passing.
		 */
		@Test
		@Order(4)
		public void testOthersPassing() {
			var request = LauncherDiscoveryRequestBuilder.request()
					.selectors(DiscoverySelectors.selectClass(ArgumentMapTest.class))
					.filters(TagFilter.excludeTags("approach"))
					.build();

			var launcher = LauncherFactory.create();
			var listener = new SummaryGeneratingListener();

			Logger logger = Logger.getLogger("org.junit.platform.launcher");
			logger.setLevel(Level.SEVERE);

			launcher.registerTestExecutionListeners(listener);
			launcher.execute(request);

			Assertions.assertEquals(0, listener.getSummary().getTotalFailureCount(),
					"Must pass other tests to earn credit for approach group!");
		}
		
		/** Source code loaded as a String object. */
		private String source;

		/**
		 * Sets up the parser object with a single test case.
		 *
		 * @throws IOException if IO issue occurs
		 */
		@BeforeEach
		public void setup() throws IOException {
			String name = ArgumentMap.class.getSimpleName() + ".java";
			Path path = Path.of("src", "main", "java", name);

			assertTrue(Files.isReadable(path), "Unable to access source code.");
			this.source = Files.readString(path, StandardCharsets.UTF_8);
		}
	}
}
