package com.blockchain.mcsblockchain.pojo.mine.RPCA;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface RPCAConsensus {

    //对信任列表中的节点的候选集做并集
    public void unionTxCandidate(TxCandidateSet txCandidateSet);
    //验证交易并形成本地的候选集
    public TxCandidateSet genTxCandidate() throws IOException, ClassNotFoundException, NoSuchAlgorithmException;
    //候选集产生之后，开始打包新的区块
    void generateBlock() throws Exception;
}
