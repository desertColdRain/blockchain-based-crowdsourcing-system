package com.blockchain.mcsblockchain.pojo.net.base;

import com.blockchain.mcsblockchain.pojo.core.Transaction;
import com.blockchain.mcsblockchain.pojo.mine.RPCA.TxCandidateSet;

import java.util.List;

public class Node {

    private String ip;
    private int port;
    private TxCandidateSet txCandidateSet;

    public Node() {

    }

    public TxCandidateSet getTxCandidateSet() {
        return txCandidateSet;
    }

    public void setTxCandidateSet(TxCandidateSet txCandidateSet) {
        this.txCandidateSet = txCandidateSet;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
