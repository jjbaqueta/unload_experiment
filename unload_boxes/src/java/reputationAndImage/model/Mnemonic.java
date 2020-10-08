package reputationAndImage.model;

/**
 * This class implements a mnemonics enum.
 * It is used to name the agent's beliefs.
 */

public enum Mnemonic 
{
	IMPRESSION("imp"),
	REPUTATION("rep"),
	IMAGE("img"),
	KNOWHOW("ref"),
	TRUST("trust");
	
	private String mnemonic;
	
	private Mnemonic(String mnemonic) 
	{
		this.mnemonic = mnemonic;
	}
	
	public String getMnemonic()
	{
		return this.mnemonic;
	}
}