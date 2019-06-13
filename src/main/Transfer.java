package main;

import accounts.Account;

import java.io.Serializable;

public class Transfer extends Transaction implements Serializable {

    private Account recipient;

    public Transfer(double amount, Account recipient){
        super(amount, TransactionType.TRANSFEROUT, "To Account " + recipient.getAccountNum());
        this.recipient = recipient;
    }

    public Account getRecipient(){
        return recipient;
    }

}
