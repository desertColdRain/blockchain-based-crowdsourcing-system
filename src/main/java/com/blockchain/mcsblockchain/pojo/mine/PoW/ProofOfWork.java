package com.blockchain.mcsblockchain.pojo.mine.PoW;

import com.blockchain.mcsblockchain.Utils.ByteUtils;
import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.blockchain.mcsblockchain.Utils.Numeric;
import com.blockchain.mcsblockchain.pojo.core.Block;
import org.apache.commons.lang3.StringUtils;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ProofOfWork {
    /**
     * 难度目标位, target=24 时大约 30 秒出一个区块
     */
    public static final int TARGET_BITS = 18;

    /**
     * 区块
     */
    private final Block block;

    /**
     * 难度目标值
     */
    private final BigInteger target;

    /**
     * <p>创建新的工作量证明，设定难度目标值</p>
     * <p>对1进行移位运算，将1向左移动 (256 - TARGET_BITS) 位，得到我们的难度目标值</p>
     * @param block
     * @return
     */
    public static ProofOfWork newProofOfWork(Block block) {
        BigInteger targetValue = BigInteger.valueOf(1).shiftLeft((256 - TARGET_BITS));
        return new ProofOfWork(block, targetValue);
    }

    private ProofOfWork(Block block, BigInteger target) {
        this.block = block;
        this.target = target;
    }

    /**
     * 运行工作量证明，开始挖矿，找到小于难度目标值的Hash
     * @return
     */
    public PowResult run() throws NoSuchAlgorithmException {
        long nonce = 0;
        String shaHex = "";
        while (nonce < Long.MAX_VALUE) {
            byte[] data = this.prepareData(nonce);
            shaHex = Cryptography.myHash(Arrays.toString(data)).trim();
            if (new BigInteger(shaHex, 16).compareTo(this.target) < 0) {
                System.out.println("The generate hash:"+shaHex);
                System.out.println("The nonce of proof of work :"+nonce);
                System.out.println("The compare to of generate:"+(new BigInteger(shaHex, 16).compareTo(this.target) <0));
                break;
            } else {
                nonce++;
            }
        }
        return new PowResult(nonce, shaHex, this.target);
    }

    /**
     * 验证区块是否有效
     * @return
     */
    public boolean  validate() throws NoSuchAlgorithmException {
        System.out.println("The nonce of validate: "+this.getBlock().getHeader().getNonce());
        byte[] data = this.prepareData(this.getBlock().getHeader().getNonce());
        System.out.println("The validate hash:"+Cryptography.myHash(Arrays.toString(data)).trim());
        System.out.println("The validate compare:"+(new BigInteger(Cryptography.myHash(Arrays.toString(data)).trim(), 16).compareTo(this.target) < 0));
        return new BigInteger(Cryptography.myHash(Arrays.toString(data)).trim(), 16).compareTo(this.target) < 0;
    }

    /**
     * 准备数据
     * <p>
     * 注意：在准备区块数据时，一定要从原始数据类型转化为byte[]，不能直接从字符串进行转换
     * @param nonce
     * @return
     */
    private byte[] prepareData(long nonce) {
        byte[] prevBlockHashBytes = {};
        if (StringUtils.isNotBlank(this.getBlock().getHeader().getPreHash())) {
            //这里要去掉 hash 值的　0x 前缀， 否则会抛出异常
            String prevHash = Numeric.cleanHexPrefix(this.getBlock().getHeader().getPreHash());
            //String prevHash=this.getBlock().getHeader().getPreHash().trim();
            prevBlockHashBytes = new BigInteger(prevHash,16).toByteArray();
        }

        return ByteUtils.merge(
                prevBlockHashBytes,
                ByteUtils.toBytes((long)this.getBlock().getHeader().getTimeStamp().getValue()),
                ByteUtils.toBytes(TARGET_BITS),
                ByteUtils.toBytes(nonce)
        );
    }


    public Block getBlock() {
        return block;
    }

    public static BigInteger getTarget() {
        return BigInteger.valueOf(1).shiftLeft((256 - TARGET_BITS));
    }
}
