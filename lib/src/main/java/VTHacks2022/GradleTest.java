package VTHacks2022;

import com.hedera.hashgraph.sdk.*;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import java.io.File;  
import java.io.FileNotFoundException;  
import java.util.Scanner; 

public class GradleTest{
    public static void main(String[] args) throws TimeoutException, PrecheckStatusException, ReceiptStatusException{
        AccountId myAccountId = AccountId.fromString(""); //developer id here
        PrivateKey myPrivateKey = PrivateKey.fromString(""); //developer private key here
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
          PrivateKey supplyKey = PrivateKey.generate();
          //PublicKey supplyPublicKey = supplyKey.getPublicKey();
  
          //creates the empty NFT
          TokenCreateTransaction nftCreate = new TokenCreateTransaction()
                  .setTokenName("Space Pictures")
                  .setTokenSymbol("SPC")
                  .setTokenType(TokenType.NON_FUNGIBLE_UNIQUE)
                  .setDecimals(0)
                  .setInitialSupply(0)
                  .setTreasuryAccountId(treasuryId)
                  .setSupplyType(TokenSupplyType.FINITE)
                  .setMaxSupply(250)
                  .setSupplyKey(supplyKey)
                  .freezeWith(client);
  
  
          TokenCreateTransaction nftCreateTxSign = nftCreate.sign(treasuryKey);
          TransactionResponse nftCreateSubmit = nftCreateTxSign.execute(client);
          TransactionReceipt nftCreateRx = nftCreateSubmit.getReceipt(client);
          TokenId tokenId = nftCreateRx.tokenId;
  
          System.out.println("Created NFT with token ID " +tokenId);
  
          // IPFS CONTENT IDENTIFIERS FOR WHICH WE WILL CREATE NFT
          String CID = "";
          try {
                File myObj = new File("C:/Users/mattb/Documents/VTHacks2022/VTHacks2022/test.txt");
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                  CID = myReader.nextLine();
                }
                myReader.close();
              } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
              }
        System.out.println();

          TokenMintTransaction mintTx = new TokenMintTransaction()
                  .setTokenId(tokenId)
                  .addMetadata(CID.getBytes())
              .freezeWith(client);
  
          TokenMintTransaction mintTxSign = mintTx.sign(supplyKey);
          TransactionResponse mintTxSubmit = mintTxSign.execute(client);
          TransactionReceipt mintRx = mintTxSubmit.getReceipt(client);
          System.out.println("Created NFT " +tokenId + "with serial: " +mintRx.serials);

          TokenAssociateTransaction associateUserTx = new TokenAssociateTransaction()
                  .setAccountId(userAccountId)
                  .setTokenIds(Collections.singletonList(tokenId))
              .freezeWith(client)
                  .sign(userKey);

          TransactionResponse associateUserTxSubmit = associateUserTx.execute(client);
          TransactionReceipt associateUserRx = associateUserTxSubmit.getReceipt(client);
          System.out.println("NFT association with User's account: " +associateUserRx.status);
          AccountBalance balanceCheckTreasury = new AccountBalanceQuery().setAccountId(treasuryId).execute(client);
          System.out.println("Treasury balance: " +balanceCheckTreasury.tokens + "NFTs of ID " +tokenId);
          AccountBalance balanceCheckUser = new AccountBalanceQuery().setAccountId(userAccountId).execute(client);
          System.out.println("User's balance: " +balanceCheckUser.tokens + "NFTs of ID " +tokenId);
  
          TransferTransaction tokenTransferTx = new TransferTransaction()
                  .addNftTransfer( new NftId(tokenId, 1), treasuryId, userAccountId)
                  .freezeWith(client)
                  .sign(treasuryKey);
  
          TransactionResponse tokenTransferSubmit = tokenTransferTx.execute(client);
          TransactionReceipt tokenTransferRx = tokenTransferSubmit.getReceipt(client);
  
          System.out.println("NFT transfer from Treasury to User: " +tokenTransferRx.status);

          AccountBalance balanceCheckTreasury2 = new AccountBalanceQuery().setAccountId(treasuryId).execute(client);
          System.out.println("Treasury balance: " +balanceCheckTreasury2.tokens + "NFTs of ID " + tokenId);

          AccountBalance balanceCheckUser2 = new AccountBalanceQuery().setAccountId(userAccountId).execute(client);
          System.out.println("User's balance: " +balanceCheckUser2.tokens +  "NFTs of ID " +tokenId);
              
        
    }
}
