/*
 This interface has the observed ASDU types in captures (2017 summer)
 New observed type ID can be added
 */
public interface ASDUType {
    final int M_SP_NA_1 = 1;    // single-point information
    final int M_DP_NA_1 = 3;    // double-point information
    final int M_ST_NA_1 = 5;    // step position information; e.g. "104asdu.vti.v": "14"
    final int M_BO_NA_1	= 7;    // Bitstring of 32 bits
    final int M_ME_NA_1	= 9;    // measured value, normalized value,	"104asdu.normval": "0.0013732
    final int M_ME_NC_1	= 13;   // measured value, short floating point number
    final int M_SP_TB_1	= 30;   // single-point information with time tag CP56Time2a
    final int M_DP_TB_1	= 31;   // double-point information with time tag CP56Time2a
    final int M_ME_TF_1 = 36;	// measured value, short floating point number with time tag CP56Time2a
    final int C_SE_NC_1	= 50;   // set point command, short floating point number

    final int M_EI_NA_1	= 70;   // end of initialization
    final int C_IC_NA_1	= 100;  // interrogation command
    final int C_CS_NA_1	= 103;  // Clock synchronization command
}
