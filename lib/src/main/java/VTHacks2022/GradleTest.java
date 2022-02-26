package VTHacks2022;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.HederaPreCheckStatusException;
import com.hedera.hashgraph.sdk.HederaReceiptStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.AccountBalanceQuery;
import com.hedera.hashgraph.sdk.AccountBalance;
import java.util.concurrent.TimeoutException;

public class GradleTest {
    public static void main(String[] args){
        AccountId myAccountId = AccountId.fromString("0.0.30808381");
        PrivateKey myPrivateKey = PrivateKey.fromString("302e020100300506032b657004220420994fdfbad4ef53cb17fddc03b3cf0ebd2d1c7ed2758eea10c05331038c2d531c");
        //Create your Hedera testnet client
        Client client = Client.forTestnet();
        client.setOperator(myAccountId, myPrivateKey);

        PrivateKey newAccountPrivateKey = PrivateKey.generate();
        PublicKey newAccountPublicKey = newAccountPrivateKey.getPublicKey();
        try{
        TransactionResponse newAccount = new AccountCreateTransaction()
        .setKey(newAccountPublicKey)
        .setInitialBalance( Hbar.fromTinybars(1000))
        .execute(client);
        
        // Get the new account ID
        AccountId newAccountId = newAccount.getReceipt(client).accountId;

        //Log the account ID
        System.out.println("The new account ID is: " +newAccountId);

        AccountBalance accountBalance = new AccountBalanceQuery()
        .setAccountId(newAccountId)
        .execute(client);

        System.out.println("The new account balance is: " +accountBalance.hbars);
        } catch (Exception TimeOutException){}
    }
}
