package sia.test.py.fileparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.python.core.PyException;

import sia.fileparsers.Parser;
import sia.models.Contact;
import sia.models.UserAccount;
import sia.ui.SIA;
import sia.utils.Dictionaries;
import sia.utils.ParserFactory;

/**
 * Fma Parser Test
 * @author jumper
 */
public class FmaParserTest {
	private Parser parser;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SIA.getInstance().dbInit(ParserFactoryTest.class.getClassLoader().getResource("sia/test/py/fileparsers/sia.db").getPath());
		SIA.getInstance().tmpInit();
		SIA.getInstance().ormInit();
		Dictionaries.getInstance().init();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		parser = ParserFactory.getInstance().create("FmaParser");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		SIA.getInstance().close(null);
	}

	@Test
	public void testLoadFilesEmptyArray() throws Exception {
		try {
			parser.loadFiles(new String[] {});
			fail("Shouldn't get so far.");
		} catch (PyException e) {
			assertEquals("Unexpected error type", "exceptions.IndexError", PyException.exceptionClassName(e.type));
			assertTrue("Unexpected IOError argument", e.value.toString().indexOf("index out of range: 0") == 12);
		}
	}

	@Test
	public void testLoadFilesNotFound() throws Exception {
		try {
			parser.loadFiles(new String[] {"/dasds/dummy-name"});
			fail("Shouldn't get so far.");
		} catch (NullPointerException e) {
		} catch (PyException e) {
			assertEquals("Unexpected error type", "exceptions.IOError", PyException.exceptionClassName(e.type));
			assertTrue("Unexpected IOError argument", e.value.toString().indexOf("No such file or directory") == 12);
		}
	}
	
	@Test
	public void testLoadFilesSample() throws Exception {
		parser.loadFiles(new String[] { ParserFactoryTest.class.getClassLoader().getResource("sia/test/py/fileparsers/FmaParserTest-empty.xml").getPath() } );
	}
	
	@Test
	public void testGetUserAccountsNoFileLoaded() throws Exception {
		assertEquals("User accounts not empty!", null, parser.getUserAccounts());
	}
	
	@Test
	public void testGetUserAccountsFileOk() throws Exception {
		parser.loadFiles(new String[] { ParserFactoryTest.class.getClassLoader().getResource("sia/test/py/fileparsers/FmaParserTest-sample.xml").getPath() } );
		assertEquals("User accounts array size <> 1", 1, parser.getUserAccounts().size());
		assertEquals("User account not the same", new UserAccount(-1, Dictionaries.getInstance().getProtocol("SMS"), ""), parser.getUserAccounts().get(0));
	}
	
	@Test
	public void testGetContactsEmptyXml() throws Exception {
		parser.loadFiles(new String[] { ParserFactoryTest.class.getClassLoader().getResource("sia/test/py/fileparsers/FmaParserTest-empty.xml").getPath() } );
		List<Contact> contacts = parser.getContacts(parser.getUserAccounts());
		assertNotNull("Contact list is null", contacts);
		assertEquals("Contact list not empty", 0, contacts.size());
	}
	
	@Test
	public void testGetContactsOneEmptyMessage() throws Exception {
		parser.loadFiles(new String[] { ParserFactoryTest.class.getClassLoader().getResource("sia/test/py/fileparsers/FmaParserTest-one-empty-message.xml").getPath() } );
		List<UserAccount> userAccount = parser.getUserAccounts();
		List<Contact> contacts = parser.getContacts(userAccount);
		assertNotNull("Contact list is null", contacts);
		assertEquals("Contact list size != 1", 1, contacts.size());
		assertEquals("Id should be -1", 0, contacts.get(0).getId());
		assertEquals("First name not empty", "", contacts.get(0).getFirstname());
		assertEquals("Last name not empty", "", contacts.get(0).getLastname());
		assertEquals("Name not empty", "", contacts.get(0).getLastname());
		assertNotNull("Contact accounts is null", contacts.get(0).getContactAccounts());
		assertEquals("Contact accounts size != 1", 1, contacts.get(0).getContactAccounts().size());
		assertEquals("Contact account contact reference fail", contacts.get(0), contacts.get(0).getContactAccounts().get(0).getContact());
		assertEquals("Contact account contact ID fail", contacts.get(0).getId(), contacts.get(0).getContactAccounts().get(0).getContactId());
		assertEquals("Contact account ID != -1", 0, contacts.get(0).getContactAccounts().get(0).getId());
		assertEquals("Contact account name not empty", "", contacts.get(0).getContactAccounts().get(0).getName());
		assertEquals("Contact account other info not empty", "", contacts.get(0).getContactAccounts().get(0).getOtherinfo());
		assertEquals("Contact account uid not empty", "", contacts.get(0).getContactAccounts().get(0).getUid());
		assertEquals("Contact account protocol", Dictionaries.getInstance().getProtocol("SMS"), contacts.get(0).getContactAccounts().get(0).getProtocol());
		assertNotSame("Contact account protocol ID = -1", 0, contacts.get(0).getContactAccounts().get(0).getProtocolId());
		assertEquals("Contact account avatar exists", false, contacts.get(0).getContactAccounts().get(0).isAvatar());
		assertNotNull("Conversations list is null", contacts.get(0).getContactAccounts().get(0).getConversations());
		assertEquals("Conversations list size != 1", 1, contacts.get(0).getContactAccounts().get(0).getConversations().size());
		assertEquals("Conversation contact account reference fail", contacts.get(0).getContactAccounts().get(0), contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getContactAccount());
		assertEquals("Conversation contact account ID fail", contacts.get(0).getContactAccounts().get(0).getId(), contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getContactAccountId());
		assertEquals("Conversation ID != -1", 0, contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getId());
		assertEquals("Conversation length != 1", 1, contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getLength());
		Calendar cal = Calendar.getInstance();
		cal.set(2001, Calendar.JANUARY, 1, 1, 1, 1);
		assertEquals("Conversation time fail", cal.getTime().toString(), contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getTime().toString());
		assertEquals("Conversation title != 111", "111", contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getTitle());
		assertEquals("Conversation user account fail", userAccount.get(0), contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getUserAccount());
		assertEquals("Conversation user account ID fail", userAccount.get(0).getId(), contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getUserAccountId());
		assertNotNull("Messages list is null", contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getMessages());
		assertEquals("Messages list size != 1", 1, contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getMessages().size());
		assertEquals("Message ID != -1", 0, contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getMessages().get(0).getId());
		assertEquals("Message conversation fail", contacts.get(0).getContactAccounts().get(0).getConversations().get(0), contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getMessages().get(0).getConversation());
		assertEquals("Message conversation ID fail", contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getId(), contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getMessages().get(0).getConversationId());
		assertEquals("Message not received (sent)", true, contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getMessages().get(0).isReceived());
		assertEquals("First message != conversation title begin", contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getTitle(), contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getMessages().get(0).getMessage());
		assertEquals("First message time != conversation begin time", contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getTime(), contacts.get(0).getContactAccounts().get(0).getConversations().get(0).getMessages().get(0).getTime());
	}
	
	@Test
	public void testGetContact() throws Exception {
		parser.loadFiles(new String[] { ParserFactoryTest.class.getClassLoader().getResource("sia/test/py/fileparsers/FmaParserTest-sample.xml").getPath() } );
		parser.getContacts(parser.getUserAccounts());
		//TODO: [Marek] test conversations etc, maybe in other test case?
	}

}
