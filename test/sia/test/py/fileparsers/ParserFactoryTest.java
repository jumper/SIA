package sia.test.py.fileparsers;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.python.core.PyException;

import sia.fileparsers.IParser;
import sia.ui.SIA;
import sia.utils.Dictionaries;
import sia.utils.ParserFactory;

public class ParserFactoryTest {

	@Before
	public void setUp() throws Exception {
		SIA.getInstance().dbInit("test/sia.db");
		Dictionaries.getInstance().init();
	}

	@After
	public void tearDown() throws Exception {
		SIA.getInstance().dbClose();
	}

	@Test
	public void testParserFactoryClassNotFound() {
		try {
			new ParserFactory("NotExistingClass");
		} catch (PyException e) {
			assertEquals("Unexpected error type", "exceptions.ImportError",
					PyException.exceptionClassName(e.type));
			assertEquals("Unexpected IOError argument",
					"No module named NotExistingClass", e.value.toString());
		}
	}

	@Test
	public void testParserFactoryClassExists() {
		new ParserFactory("FmaParser");
	}

	@Test
	public void testCreate() {
		ParserFactory factory = new ParserFactory("FmaParser");
		IParser parser = factory.create();
		assertTrue("Incorrect python proxy type",
			parser.toString().indexOf("org.python.proxies.sia.py.fileparsers.FmaParser$FmaParser$") == 0);
	}

}