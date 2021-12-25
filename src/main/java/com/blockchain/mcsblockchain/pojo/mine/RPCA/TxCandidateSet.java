package com.blockchain.mcsblockchain.pojo.mine.RPCA;

import com.blockchain.mcsblockchain.pojo.core.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TxCandidateSet {
    //交易共识候选集
    private List<Transaction> candidateSet;

    public TxCandidateSet() {
        this.candidateSet=new ArrayList<>();
    }

    public List<Transaction> getCandidateSet() {
        return candidateSet;
    }
    public void addTransaction(Transaction tx){
        boolean exist=false;
        for(Transaction transaction:candidateSet){
            if(Objects.equals(transaction.getTransactionHash(), tx.getTransactionHash())) {
                exist=true;
                break;
            }
        }
        if(!exist) candidateSet.add(tx);

    }

    public void setCandidateSet(List<Transaction> candidateSet) {
        this.candidateSet = candidateSet;
    }
}
