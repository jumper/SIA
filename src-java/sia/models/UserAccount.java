package sia.models;

import org.sormula.annotation.Column;
import org.sormula.annotation.cascade.OneToOneCascade;
import org.sormula.annotation.cascade.SelectCascade;

/**
 * User account
 * 
 * @author jumper
 */
public class UserAccount {
	@Column(identity=true, primaryKey=true)
	private int id;
	private int protocolId;
	@OneToOneCascade(selects = { @SelectCascade(sourceParameterFieldNames = {"protocolId"}) }, inserts = {}, updates = {}, deletes = {} )
	private Protocol protocol;
	private String uid;
	
	/**
	 * Default constructor 
	 */
	public UserAccount() { }
	
	/**
	 * Constructor
	 * @param id
	 * @param uid
	 */
	public UserAccount(int id, Protocol protocol, String uid) { 
		this.id = id;
		setProtocol(protocol);
		this.uid = uid;
	}
	
	/**
	 * Returns ID
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set ID
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns protocol ID
	 * @return protocolId
	 */
	public int getProtocolId() {
		return protocolId;
	}

	/**
	 * Set protocol ID
	 * @param protocolId
	 */
	public void setProtocolId(int protocolID) {
		this.protocolId = protocolID;
	}

	/**
	 * Returns protocol
	 * @return protocol
	 */
	public Protocol getProtocol() {
		return protocol;
	}

	/**
	 * Set protocol
	 * @param protocol
	 */
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
		this.protocolId = protocol != null ? protocol.getId() : 0;
	}

	/**
	 * Returns UID
	 * @return uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * Set UID
	 * @param uid
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((protocol == null) ? 0 : protocol.hashCode());
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserAccount other = (UserAccount) obj;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "UserAccount [protocol=" + (protocol != null ? protocol.getName() : "null") + ", uid=" + uid + "]";
	}
}
