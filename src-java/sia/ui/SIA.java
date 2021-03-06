package sia.ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.python.core.PyBaseException;
import org.python.core.PyException;
import org.sormula.Database;
import org.sormula.SormulaException;

import sia.models.Configuration;
import sia.models.Contact;
import sia.models.ContactAccount;
import sia.models.Conversation;
import sia.models.Message;
import sia.models.Protocol;
import sia.models.UserAccount;
import sia.utils.Dictionaries;
import sia.utils.ORM;
import sia.utils.ParserFactory;

public class SIA {
	public static SIA instance;

	private Connection connection;
	private ORM orm;
	private Start window;
	private Splash splash;

	private static final Logger logger = Logger.getLogger(SIA.class);

	/**
	 * Initialize database and GUI
	 */
	public void init() {
		PropertyConfigurator.configure("log4j.properties");
		logger.debug("init");
		try {
			splash = new Splash();
			splash.init();
			dbInit("sia.db");
			splash.progressIncrement();
			tmpInit();
			splash.progressIncrement();
			ormInit();
			splash.progressIncrement();
			Dictionaries.getInstance().init();
			splash.progressIncrement();
			ParserFactory.getInstance();
			splash.close();
			guiInit();
		} catch (SQLException e) {
			handleException("Unexpected problem with database connection (SQL).", e);
		} catch (ClassNotFoundException e) {
			handleException("Application module is missing.", e);
		} catch (SormulaException e) {
			handleException("Unexpected problem with database connection (ORM).", e);
		} catch (Exception e) {
			handleException("Unexpected error.", e);
		} finally {
			close(null);
		}
	}

	/**
	 * General exception handler
	 * 
	 * @param ex
	 */
	public void handleException(final String message, final Exception ex) {
		final String exceptionMessage;
		 if (ex instanceof PyException && ((PyException) ex).value instanceof PyBaseException) {
             exceptionMessage = ((PyBaseException) ((PyException) ex).value).message.toString();
		} else {
			exceptionMessage = ex.getLocalizedMessage() == null ? "" : ex.getLocalizedMessage();
		}
		logger.error(message, ex);
		window.getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openError(window.getShell(), "Error", (message != null ? message : "") + "\n"
						+ exceptionMessage + "\nDetails stored in error.log.");
			}
		});
	}

	/**
	 * Database init
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws SormulaException
	 */
	public void dbInit(String dbPath) throws ClassNotFoundException, SQLException, SormulaException {
		System.setProperty("sqlite.purejava", "true");
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
		connection.setAutoCommit(true);
		Statement stmt = connection.createStatement();
		stmt.execute("PRAGMA locking_mode = EXCLUSIVE");
		try {
			stmt.executeUpdate("UPDATE configuration SET value = '' WHERE key = 'random'");
		} catch (SQLException e) {
			if (e.getMessage().indexOf("locked") != -1) {
				close("Another instance of this application is already running. Only one instance can be launched at once.");
			}
		}
		stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT value FROM configuration WHERE key = 'running_application'");
		if (rs.next() && rs.getString(1) != null && rs.getString(1).equals("true")) {
			cleanup();
			updateConversations(null);
		}
		rs.close();
		stmt = connection.createStatement();
		stmt.executeUpdate("INSERT OR REPLACE INTO configuration (key, value) VALUES ('running_application', 'true')");
		stmt.executeUpdate("PRAGMA foreign_keys = ON");
		stmt.execute("PRAGMA journal_mode = OFF");
		stmt.executeUpdate("PRAGMA temp_store = 1");
		stmt.executeUpdate("PRAGMA synchronous = OFF");
		stmt = connection.createStatement();
		stmt.execute("ATTACH DATABASE ':memory:' AS aux1");
		stmt.execute("PRAGMA aux1.foreign_keys = OFF");
	}

	/**
	 * ORM init
	 * 
	 * @throws SormulaException
	 */
	public void ormInit() throws SormulaException {
		Database database = new Database(connection, "main");
		Database databaseTemp = new Database(connection, "aux1");
		orm = new ORM(database, databaseTemp);
		orm.createTable(Configuration.class);
		orm.createTable(Contact.class);
		orm.createTable(ContactAccount.class);
		orm.createTable(Conversation.class);
		orm.createTable(Message.class);
		orm.createTable(Protocol.class);
		orm.createTable(UserAccount.class);
		orm.createTempTable(Contact.class);
		orm.createTempTable(ContactAccount.class);
		orm.createTempTable(Conversation.class);
		orm.createTempTable(Message.class);
		orm.createTempTable(UserAccount.class);
	}

	/**
	 * Temporary database init
	 * 
	 * @throws SQLException
	 */
	public void tmpInit() throws SQLException {
		Statement stmt = connection.createStatement();
		Statement select = connection.createStatement();
		ResultSet result = select.executeQuery("SELECT sql, name FROM main.sqlite_master WHERE type = 'table'");
		String name;
		// stmt.executeUpdate("DELETE FROM sqlite_sequence");
		while (result.next()) {
			name = result.getString(2);
			if (name.indexOf("sqlite_") != 0 && name.indexOf("configuration") != 0) {
				stmt.executeUpdate(result.getString(1).replace("CREATE TABLE", "CREATE TABLE IF NOT EXISTS aux1."));
				stmt.executeUpdate("DELETE FROM aux1." + name);
				// stmt.executeUpdate("INSERT INTO aux1."+name+" SELECT * FROM main."+name+" WHERE id = (SELECT MAX(id) FROM main."+name+")");
				stmt.executeUpdate("INSERT OR REPLACE INTO aux1.sqlite_sequence SELECT name, seq FROM main.sqlite_sequence WHERE name = '"
						+ name + "'");
			}
		}
	}

	/**
	 * Save changes from temporary database to file database
	 * 
	 * @throws SQLException
	 */
	public void tmpSave() throws SQLException {
		Statement stmt = connection.createStatement();
		String[] tables = new String[] { "useraccount", "contact", "contactaccount", "conversation", "message" };
		int affected[] = new int[tables.length];
		for (String table : tables) {
			stmt.addBatch("INSERT OR REPLACE INTO main." + table + " SELECT * FROM aux1." + table
					+ " WHERE aux1." + table + ".id > IFNULL((SELECT MAX(id) FROM main." + table + "), 0)");
		}
		affected = stmt.executeBatch();
		for (int i=0; i<tables.length; i++) {
			logger.debug("temp -> file " + tables[i] + ", affected: " + affected[i]);
		}
		stmt.close();
	}
	
	/**
	 * Clean-up database
	 * 
	 * @throws SQLException
	 */
	public void cleanup() throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.addBatch("DELETE FROM contact WHERE id IN (SELECT DISTINCT c.id FROM contact c LEFT JOIN contactaccount ca ON ca.contactId = c.id WHERE ca.id IS NULL)");
		stmt.addBatch("DELETE FROM conversation WHERE id IN (SELECT DISTINCT c.id FROM conversation c LEFT JOIN message m ON m.conversationId = c.id WHERE m.id IS NULL)");
		stmt.executeBatch();
		stmt.close();
	}

	/**
	 * Update conversations
	 * 
	 * @throws SQLException
	 */
	public void updateConversations(List<UserAccount> userAccounts) throws SQLException {
		String where = "";
		if (userAccounts != null) {
			where = "WHERE userAccountId IN (";
			for (int i = 0; i < userAccounts.size(); i++) {
				where += (i == 0 ? "" : ",") + userAccounts.get(i).getId();
			}
			where += ")";
		}
		String sql = "UPDATE main.conversation "
				+ "SET time = (SELECT MIN(time) FROM main.message WHERE conversationId = main.conversation.id), "
				+ "endTime = (SELECT MAX(time) FROM main.message WHERE conversationId = main.conversation.id), "
				+ "length = (SELECT COUNT(1) FROM main.message WHERE conversationId = main.conversation.id) "
				+ where;
		Statement stmt = SIA.getInstance().getConnection().createStatement();
		stmt.executeUpdate(sql);
	}

	/**
	 * Close database connection
	 * 
	 * @throws SQLException
	 */
	public void close(String message) {
		if (splash != null)
			splash.close();
		if (window != null) {
			window.close();
		}
		if (message != null) {
			MessageDialog.openInformation(new Shell(), "Unexpected shutdown", message);
		}
		if (connection != null) {
			try {
				Statement stmt = connection.createStatement();
				stmt.executeUpdate("UPDATE configuration SET value = 'false' WHERE key = 'running_application'");
			} catch (SQLException e) {
				logger.error(e);
			}
			try {
				connection.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
		if (message != null) {
			System.exit(-1);
		}
	}

	/**
	 * GUI init
	 */
	public void guiInit() {
		while (Display.getCurrent() != null && !Display.getCurrent().isDisposed())
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Logger.getLogger(Start.class).error("Interrupt while waiting for display disposal", e);
		}
		window = new Start();
		window.run();
	}

	/**
	 * Returns SIA instance
	 * 
	 * @return SIA
	 */
	public static SIA getInstance() {
		if (instance == null)
			instance = new SIA();
		return instance;
	}

	/**
	 * Returns database connection
	 * 
	 * @return database connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Returns ORM
	 * 
	 * @return ORM
	 */
	public ORM getORM() {
		return orm;
	}

	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SIA.getInstance().init();
	}
	
	/**
	 * Splash screen with progress loader
	 * 
	 * @author jumper
	 */
	private class Splash {
		private Display display = null;
		private Image image = null;
		private Shell splash = null;
		private ProgressBar splashBar;
		
		public void init() {
			display = new Display();
			image = new Image(display, 300, 50);
			splash = new Shell(SWT.ON_TOP);
			splashBar = new ProgressBar(splash, SWT.NONE);
			GC gc = new GC(image);
			gc.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			gc.fillRectangle(image.getBounds());
			gc.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
			gc.drawText("SIA loading...", 10, 10);
			gc.dispose();
			splashBar.setMinimum(0);
			splashBar.setMaximum(5);
			Label label = new Label(splash, SWT.NONE);
			label.setImage(image);
			FormLayout layout = new FormLayout();
			splash.setLayout(layout);
			FormData labelData = new FormData ();
			labelData.right = new FormAttachment (100, 0);
			labelData.bottom = new FormAttachment (100, 0);
			label.setLayoutData(labelData);
			FormData progressData = new FormData ();
			progressData.left = new FormAttachment (0, 5);
			progressData.right = new FormAttachment (100, -5);
			progressData.bottom = new FormAttachment (100, -5);
			splashBar.setLayoutData(progressData);
			splash.pack();
			Rectangle splashRect = splash.getBounds();
			Rectangle displayRect = display.getBounds();
			int x = (displayRect.width - splashRect.width) / 2;
			int y = (displayRect.height - splashRect.height) / 2;
			splash.setLocation(x, y);
			splash.open();
		}
		
		public void progressIncrement() {
			splashBar.setSelection(splashBar.getSelection() + 1);
		}
		
		public void close() {
			if (splash != null && !splash.isDisposed())
				splash.close();
			if (image != null && !image.isDisposed())
				image.dispose();
			if (Display.getCurrent() != null && !Display.getCurrent().isDisposed())
				Display.getCurrent().dispose();
		}
	}
}
