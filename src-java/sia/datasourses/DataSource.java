package sia.datasourses;

import java.util.List;
import sia.models.Contact;
import sia.models.UserAccount;
/**
 * 
 * @author Agnieszka Glabala
 *
 */
public abstract class DataSource {
	String[] extensions;
	String[][]  descriptions;
	List<UserAccount> userAccounts;
	String[] passwordDescriptions;
	String[] passwords;
	
	/**
	 * Return accepted by parser file extensions 
	 * @return array of extensions
	 */
	public String[] getFileExtensions() {
		return extensions;
	}
	
	/**
	 * Return description for required files for every file [short description ie. "Database file" , hint how to find this file ie. "usually in home/.kadu/"]
	 * @return array of descriptions
	 */
	public String[][] getFileDescriptions() {
		return descriptions;
	}
	
	/**
	 * Return user accounts found in archive files or null if null if user should to set this manually
	 * @return list of user accounts
	 */
	public abstract List<UserAccount> getUserAccouts();
	
	/**
	 * Set from which accounts user want to import data
	 * @param selectedAccounts array in the same order as userAccounts 
	 */
	public void setUserAccounts(boolean[] selectedAccounts) {
		//TODO
	}
	
	/**
	 * Set from which accounts user want to import data (given by user)
	 * @param selectedAccounts array in the same order as userAccounts 
	 */
	public void setUserAccounts(List<UserAccount> uas) {
		//TODO
	}
	
	/**
	 * Return all contacts with conversations (but not necessarily messages)
	 * @return list of contacts
	 */
	public abstract List<Contact> getContacts();
	
	/**
	 * Load and validate files
	 */
	public abstract void loadFiles(String[] files);
	
	/**
	 * Returns descriptions of passwords, ie. ["your pasword to archive 1", "your password to archive 2"]
	 * @return array of descriptions of passwords
	 */
	public String[] getRequiredPassword() {
		return passwordDescriptions;
	} 
	
	/**
	 * Set passwords
	 * @param passwords
	 */
	public void setPasswords(String[] passwords) {
		this.passwords = passwords;
	}
}
