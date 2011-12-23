package sia.ui;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;

import sia.ui.importui.ImportWizard;

/**
 * 
 * @author Agnieszka Glabala
 *
 */
public class Start extends ApplicationWindow {
	private Table conversationsTable;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Start window = new Start();
			window.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Start() {
		super(null);
	}

	/**
	 * Configure shell
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setSize(882, 557);
		shell.setText("SIA - SMS and IM Archiver");
	}

	/**
	 * Open the window.
	 */
	public void run() {
		setBlockOnOpen(true);
		open();
		Display.getCurrent().dispose();
	}

	/**
	 * Create contents of the window.
	 */
	protected Control createContents(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		// TOOLBAR
		ToolBar toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL,
				GridData.VERTICAL_ALIGN_BEGINNING, true, false);
		toolBar.setLayoutData(gridData);

		ToolItem importButton = new ToolItem(toolBar, SWT.NONE);
		importButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ImportWizard importWizard = new ImportWizard();
				// Instantiates the wizard container with the wizard and opens
				// it
				WizardDialog dialog = new WizardDialog(composite.getShell(),
						importWizard);
				dialog.create();
				dialog.open();
			}
		});
		importButton.setImage(sia.ui.org.eclipse.wb.swt.SWTResourceManager
				.getImage(Start.class, "/sia/ui/resources/import.png"));
		importButton.setText("Import");

		ToolItem exportButton = new ToolItem(toolBar, SWT.NONE);
		exportButton.setImage(sia.ui.org.eclipse.wb.swt.SWTResourceManager
				.getImage(Start.class, "/sia/ui/resources/export.png"));
		exportButton.setText("Export");

		ToolItem preferencesButton = new ToolItem(toolBar, SWT.NONE);
		preferencesButton.setImage(sia.ui.org.eclipse.wb.swt.SWTResourceManager
				.getImage(Start.class, "/sia/ui/resources/properties.png"));
		preferencesButton.setText("Preferences");

		ToolItem synchronizeButton = new ToolItem(toolBar, SWT.NONE);
		synchronizeButton.setImage(sia.ui.org.eclipse.wb.swt.SWTResourceManager
				.getImage(Start.class, "/sia/ui/resources/sync.png"));
		synchronizeButton.setText("Synchronize");
		// END TOOLBAR

		gridData = new GridData(GridData.FILL, GridData.FILL, false, true);

		// MIDDLE COMPOSITE - SASHFORM
		SashForm sashForm = new SashForm(composite, SWT.NONE);
		sashForm.setLayoutData(gridData);
		// END MIDDLE COMPOSITE

		// LEFT COMPOSITE
		Composite compositeLeft = new Composite(sashForm, SWT.NONE);
		compositeLeft.setLayout(new GridLayout(2, false));

		Text contactsKeyword = new Text(compositeLeft, SWT.SINGLE | SWT.BORDER);
		GridData gd_contactsKeyword = new GridData(GridData.FILL_HORIZONTAL);
		gd_contactsKeyword.verticalAlignment = SWT.FILL;
		contactsKeyword.setLayoutData(gd_contactsKeyword);
		Button contactsSearch = new Button(compositeLeft, SWT.PUSH);
		contactsSearch.setImage(sia.ui.org.eclipse.wb.swt.SWTResourceManager
				.getImage(Start.class, "/sia/ui/resources/find.png"));
		contactsSearch.setText("Search");

		ScrolledComposite contactsScrolledComposite = new ScrolledComposite(
				compositeLeft, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		contactsScrolledComposite.setExpandHorizontal(true);
		contactsScrolledComposite.setExpandVertical(true);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		contactsScrolledComposite.setLayoutData(gridData);

		Tree contactsTree = new Tree(contactsScrolledComposite, SWT.BORDER);
		contactsScrolledComposite.setContent(contactsTree);
		contactsScrolledComposite.setMinSize(contactsTree.computeSize(
				SWT.DEFAULT, SWT.DEFAULT));
		// END LEFT COMPOSITE

		// RIGHT COMPOSITE
		Composite compositeRight = new Composite(sashForm, SWT.NONE);
		compositeRight.setLayout(new GridLayout(2, false));
		Text messagesKeyword = new Text(compositeRight, SWT.SINGLE | SWT.BORDER);
		GridData gd_messagesKeyword = new GridData(GridData.FILL_HORIZONTAL);
		gd_messagesKeyword.verticalAlignment = SWT.FILL;
		messagesKeyword.setLayoutData(gd_messagesKeyword);
		Button messagesSearch = new Button(compositeRight, SWT.PUSH);
		messagesSearch.setImage(sia.ui.org.eclipse.wb.swt.SWTResourceManager
				.getImage(Start.class, "/sia/ui/resources/find.png"));
		messagesSearch.setText("Search");

		SashForm sashForm_1 = new SashForm(compositeRight, SWT.VERTICAL);
		GridData gridData1 = new GridData(GridData.FILL_BOTH);
		gridData1.horizontalSpan = 2;
		sashForm_1.setLayoutData(gridData1);
		ScrolledComposite messagesScrolledComposite = new ScrolledComposite(
				sashForm_1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		messagesScrolledComposite.setExpandHorizontal(true);
		messagesScrolledComposite.setExpandVertical(true);

		conversationsTable = new Table(messagesScrolledComposite, SWT.BORDER
				| SWT.FULL_SELECTION);
		conversationsTable.setHeaderVisible(true);
		conversationsTable.setLinesVisible(true);

		TableColumn tblclmnContact = new TableColumn(conversationsTable,
				SWT.NONE);
		tblclmnContact.setWidth(100);
		tblclmnContact.setText("Contact");

		TableColumn tblclmnTitle = new TableColumn(conversationsTable, SWT.NONE);
		tblclmnTitle.setWidth(233);
		tblclmnTitle.setText("Title");

		TableColumn tblclmnTime = new TableColumn(conversationsTable, SWT.NONE);
		tblclmnTime.setResizable(false);
		tblclmnTime.setWidth(119);
		tblclmnTime.setText("Time");

		TableColumn tblclmnLength = new TableColumn(conversationsTable,
				SWT.NONE);
		tblclmnLength.setResizable(false);
		tblclmnLength.setWidth(83);
		tblclmnLength.setText("Length");
		messagesScrolledComposite.setContent(conversationsTable);
		messagesScrolledComposite.setMinSize(conversationsTable.computeSize(
				SWT.DEFAULT, SWT.DEFAULT));

		ScrolledComposite scrolledComposite = new ScrolledComposite(sashForm_1,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Browser conversationBrowser = new Browser(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(conversationBrowser);
		scrolledComposite.setMinSize(conversationBrowser.computeSize(
				SWT.DEFAULT, SWT.DEFAULT));
		sashForm_1.setWeights(new int[] { 1, 1 });
		sashForm.setWeights(new int[] { 264, 613 });
		// RIGHT COMPOSITE

		// STATUS
		Label status = new Label(composite, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL,
				GridData.VERTICAL_ALIGN_END, false, false);
		status.setLayoutData(gridData);
		status.setText("");
		// END STATUS
		return composite;
	}
}