package com.blockchain.mcsblockchain.pojo.mine.PoW;

import java.math.BigInteger;

public class PowResult {
    /**
     * 计数器
     */
    private Long nonce;
    /**
     * 新区快的哈希值
     */
    private String hash;
    /**
     * 目标难度值
     */
    private BigInteger target;

    public PowResult(long nonce, String hash, BigInteger target) {
        this.nonce = nonce;
        this.hash = hash;
        this.target = target;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public BigInteger getTarget() {
        return target;
    }

    public void setTarget(BigInteger target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "PowResult{" +
                "nonce=" + nonce +
                ", hash='" + hash + '\'' +
                ", target=" + target +
                '}';
    }
}
