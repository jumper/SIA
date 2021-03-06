package sia.ui.importui;

import java.util.Arrays;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import sia.utils.Dictionaries;

/**
 * 
 * @author Agnieszka Glabala
 *
 */
public class ImportChooseIM extends WizardPage {
	private String[] imNames;
	private Button[] radioButtons;
	/**
	 * Create the wizard.
	 */
	public ImportChooseIM(String[] names) {
		super("chooseIM");
		setTitle("Choose IM");
		setDescription("Choose IM from which you would like to import data:");
		Arrays.sort(names);
		this.imNames = names;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {		
		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		setControl(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		Composite composite = new Composite(scrolledComposite, SWT.NONE);
		scrolledComposite.setSize(500, 500);
		composite.setLayout(new GridLayout(1, false));
		radioButtons = new Button[imNames.length];
		for (int i = 0; i < imNames.length; i++) {
			radioButtons[i] = new Button(composite, SWT.RADIO);
			radioButtons[i].setImage(sia.ui.org.eclipse.wb.swt.SWTResourceManager.getImage(ImportChooseIM.class, 
					"/sia/ui/resources/ims/"+Dictionaries.getInstance().getDataSources().get(imNames[i])+".png"));
			radioButtons[i].setText(imNames[i]);
			radioButtons[i].addSelectionListener((ImportWizard)getWizard());
		}
		
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
	}
	
	public int getSelected(){
		for (int i = 0; i < radioButtons.length; i++) {
			if(radioButtons[i].getSelection()) 
				return i;
		}
		return -1;
	}
	
}
