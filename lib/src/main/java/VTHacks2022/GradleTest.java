package VTHacks2022;

import com.hedera.hashgraph.sdk.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class GradleTest{
    public static void main(String[] args) throws TimeoutException, PrecheckStatusException, ReceiptStatusException{
        AccountId myAccountId = AccountId.fromString("0.0.30808381");
        PrivateKey myPrivateKey = PrivateKey.fromString("302e020100300506032b657004220420994fdfbad4ef53cb17fddc03b3cf0ebd2d1c7ed2758eea10c05331038c2d531c");
        //Create your Hedera testnet client
        Client client = Client.forTestnet();
        client.setOperator(myAccountId, myPrivateKey);

          PrivateKey treasuryKey = PrivateKey.generate();
          PublicKey treasuryPublicKey = treasuryKey.getPublicKey();
          TransactionResponse treasuryAccount = new AccountCreateTransaction()
                  .setKey(treasuryPublicKey)
                  .setInitialBalance(new Hbar(10))
                  .execute(client);
  
          AccountId treasuryId = treasuryAccount.getReceipt(client).accountId;
          PrivateKey userKey = PrivateKey.generate();
          PublicKey userPublicKey = userKey.getPublicKey();
          TransactionResponse userAccount = new AccountCreateTransaction()
                  .setKey(userPublicKey)
                  .setInitialBalance(new Hbar(10))
                  .execute(client);
  
          AccountId userAccountId = userAccount.getReceipt(client).accountId;
  
          //Alice Key
          PrivateKey supplyKey = PrivateKey.generate();
          PublicKey supplyPublicKey = supplyKey.getPublicKey();
  
          //Create the NFT
          TokenCreateTransaction nftCreate = new TokenCreateTransaction()
                  .setTokenName("diploma")
                  .setTokenSymbol("GRAD")
                  .setTokenType(TokenType.NON_FUNGIBLE_UNIQUE)
                  .setDecimals(0)
                  .setInitialSupply(0)
                  .setTreasuryAccountId(treasuryId)
                  .setSupplyType(TokenSupplyType.FINITE)
                  .setMaxSupply(250)
                  .setSupplyKey(supplyKey)
                  .freezeWith(client);
  
  
          //Sign the transaction with the treasury key
          TokenCreateTransaction nftCreateTxSign = nftCreate.sign(treasuryKey);
  
          //Submit the transaction to a Hedera network
          TransactionResponse nftCreateSubmit = nftCreateTxSign.execute(client);
  
          //Get the transaction receipt
          TransactionReceipt nftCreateRx = nftCreateSubmit.getReceipt(client);
  
          //Get the token ID
          TokenId tokenId = nftCreateRx.tokenId;
  
          //Log the token ID
          System.out.println("Created NFT with token ID " +tokenId);
  
          // IPFS CONTENT IDENTIFIERS FOR WHICH WE WILL CREATE NFT
          String CID = ("QmTzWcVfk88JRqjTpVwHzBeULRTNzHY7mnBSG42CpwHmPa") ;
  
          // MINT NEW NFT
          TokenMintTransaction mintTx = new TokenMintTransaction()
                  .setTokenId(tokenId)
                  .addMetadata(CID.getBytes())
              .freezeWith(client);
  
          //Sign with the supply key
          TokenMintTransaction mintTxSign = mintTx.sign(supplyKey);
  
          //Submit the transaction to a Hedera network
          TransactionResponse mintTxSubmit = mintTxSign.execute(client);
  
          //Get the transaction receipt
          TransactionReceipt mintRx = mintTxSubmit.getReceipt(client);
  
          //Log the serial number
          System.out.println("Created NFT " +tokenId + "with serial: " +mintRx.serials);
  
      //Create the associate transaction and sign with Alice's key 
          TokenAssociateTransaction associateAliceTx = new TokenAssociateTransaction()
                  .setAccountId(userAccountId)
                  .setTokenIds(Collections.singletonList(tokenId))
              .freezeWith(client)
                  .sign(userKey);
  
          //Submit the transaction to a Hedera network
          TransactionResponse associateAliceTxSubmit = associateAliceTx.execute(client);
  
          //Get the transaction receipt
          TransactionReceipt associateAliceRx = associateAliceTxSubmit.getReceipt(client);
  
          //Confirm the transaction was successful
          System.out.println("NFT association with Alice's account: " +associateAliceRx.status);
  
          // Check the balance before the NFT transfer for the treasury account
          AccountBalance balanceCheckTreasury = new AccountBalanceQuery().setAccountId(treasuryId).execute(client);
          System.out.println("Treasury balance: " +balanceCheckTreasury.tokens + "NFTs of ID " +tokenId);
  
          // Check the balance before the NFT transfer for Alice's account
          AccountBalance balanceCheckAlice = new AccountBalanceQuery().setAccountId(userAccountId).execute(client);
          System.out.println("Alice's balance: " +balanceCheckAlice.tokens + "NFTs of ID " +tokenId);
  
          // Transfer NFT from treasury to Alice
          // Sign with the treasury key to authorize the transfer
          TransferTransaction tokenTransferTx = new TransferTransaction()
                  .addNftTransfer( new NftId(tokenId, 1), treasuryId, userAccountId)
                  .freezeWith(client)
                  .sign(treasuryKey);
  
          TransactionResponse tokenTransferSubmit = tokenTransferTx.execute(client);
          TransactionReceipt tokenTransferRx = tokenTransferSubmit.getReceipt(client);
  
          System.out.println("NFT transfer from Treasury to Alice: " +tokenTransferRx.status);
  
          // Check the balance for the treasury account after the transfer
          AccountBalance balanceCheckTreasury2 = new AccountBalanceQuery().setAccountId(treasuryId).execute(client);
          System.out.println("Treasury balance: " +balanceCheckTreasury2.tokens + "NFTs of ID " + tokenId);
  
          // Check the balance for Alice's account after the transfer
          AccountBalance balanceCheckAlice2 = new AccountBalanceQuery().setAccountId(userAccountId).execute(client);
          System.out.println("Alice's balance: " +balanceCheckAlice2.tokens +  "NFTs of ID " +tokenId);
              
        
    }
}
