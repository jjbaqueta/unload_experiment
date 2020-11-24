package trustModel.repAndImg.enums;

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
	TRUST("trust"),
	REASONING("Reasoning"),
	ABILITY("Ability"),
	AVAILABILITY("Availability"),
	TRUSTFULNESS("Trustfulness"),
	IMG_EFFECT("img_effect"),
	REP_EFFECT("rep_effect"),
	REASONING_EFFECT("reasoning_effect"),
	KNOWHOW_EFFECT("knowhow_effect"),
	ABILITY_EFFECT("ability_effect"),
	AVAILABILITY_EFFECT("availability_effect"),
	URGENCY("urgency"),
	OWN_IMPS("own_impressions"),
	OTHER_IMPS("other_impressions"),
	SELFCONFIDENT("self_confident"),
	REFERENCES("references");
	
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