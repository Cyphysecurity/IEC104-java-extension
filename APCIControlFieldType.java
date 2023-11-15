
public interface APCIControlFieldType {

	/*
	 * This interface acts a global bucket for some commonly use fields
	 * Three types of control field formats and six flags for uType are used: 
	 * Numbered information transfer (I format, likely from client to server or slave to master)
	 * Numbered supervisory functions (S format,likely from server to client and acts as an ACK message since it N(S) pair are zero's)
	 * Unnumbered control functions (U format, only first octet has meaningful data, rest of the other 3 have zero's)
	 */
	final int I_FORMAT=0;
	final int S_FORMAT=1;
	final int U_FORMAT=3;
	final int TESTFR_ACT = 10;
	final int TESTFR_CON = 20;
	final int STOPDT_CON = 8;
	final int STOPDT_ACT = 4;
	final int STARTDT_CON = 2;
	final int STARTDT_ACT = 1;
}
