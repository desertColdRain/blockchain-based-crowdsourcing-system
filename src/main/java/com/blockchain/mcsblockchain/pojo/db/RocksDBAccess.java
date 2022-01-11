package com.blockchain.mcsblockchain.pojo.db;

import com.blockchain.mcsblockchain.Utils.FastJson;
import com.blockchain.mcsblockchain.Utils.SerializeUtils;
import com.blockchain.mcsblockchain.conf.AppConfig;
import com.blockchain.mcsblockchain.net.base.Node;
import com.blockchain.mcsblockchain.net.conf.TioProps;
import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.core.Block;
import com.blockchain.mcsblockchain.pojo.core.Transaction;
import com.blockchain.mcsblockchain.pojo.core.TransactionPool;
import com.google.common.base.Optional;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//RocksDB 操作封装
@Component
public class RocksDBAccess implements DBAccess{

    @Autowired
    AppConfig appConfig;

    static Logger logger= LoggerFactory.getLogger(RocksDBAccess.class);

    private RocksDB rocksDB;

    @Autowired
    private TioProps tioProps;

    public RocksDBAccess() {
    }

    @PostConstruct
    public void initRocksDB() throws RocksDBException {
        File directory = new File(System.getProperty("user.dir") + "/" + appConfig.getDataDir());
        if(!directory.exists()){
            directory.mkdirs();
        }
        rocksDB=RocksDB.open(new Options().setCreateIfMissing(true),appConfig.getDataDir());
    }

    @Override
    public boolean putLastBlockIndex(Object lastBlock) {
        return this.put(LAST_BLOCK_INDEX, lastBlock);
    }

    @Override
    public Optional<Object> getLastBlockIndex() {
        return this.get(LAST_BLOCK_INDEX);
    }

    @Override
    public boolean putBlock(Block block) {
        return this.put(BLOCKS_BUCKET_PREFIX + block.getHeader().getIndex(), block);
    }

    @Override
    public Optional<Block> getBlock(Object blockIndex) {


        Optional<Object> object = this.get(BLOCKS_BUCKET_PREFIX + String.valueOf(blockIndex));
        if (object.isPresent()) {
            return Optional.of((Block) object.get());
        }
        return Optional.<Block>absent();
    }

    @Override
    public Optional<Block> getLastBlock() {
        Optional<Object> blockIndex = getLastBlockIndex();
        if (blockIndex.isPresent()) {
            return this.getBlock(blockIndex.get().toString());
        }
        return Optional.absent();
    }

    @Override
    public boolean putAccount(Account account) {
        return this.put(WALLETS_BUCKET_PREFIX + account.getAccountAddr(), account);
    }

    @Override
    public Optional<Account> getAccount(String address) throws NoSuchAlgorithmException {

        Optional<Object> object = this.get(WALLETS_BUCKET_PREFIX + address);
        if (object.isPresent()) {
            return Optional.of((Account) object.get());
        }
        return Optional.absent();
    }

    @Override
    public boolean putTx(Transaction tx) {
        return this.put(TX_POOL+tx.getTransactionHash(),tx);
    }

    @Override
    public boolean deleteTransaction(String txHash) {
        return this.delete(TX_POOL+txHash);
    }

    @Override
    public TransactionPool getAllTxs() throws IOException, ClassNotFoundException {
        TransactionPool txPool=new TransactionPool();
        List<Object> objects=seekByKey(TX_POOL);
        for(Object o:objects){
            txPool.addTransaction((Transaction) o);
        }
        return txPool;
    }

    @Override
    public Optional<Transaction> getTx(String txHash) throws IOException, ClassNotFoundException {

        Optional<Object> object=this.get(TX_POOL+txHash);
        if(object.isPresent()){
            return Optional.of((Transaction) object.get());
        }
        return Optional.absent();
    }

    @Override
    public List<Account> getAllAccounts() throws IOException, ClassNotFoundException {
        List<Object> objects = seekByKey(WALLETS_BUCKET_PREFIX);
        List<Account> accounts = new ArrayList<>();
        for (Object o : objects) {
            accounts.add((Account) o);
        }
        return accounts;
    }

    public void getAll() throws IOException, ClassNotFoundException, RocksDBException {
        RocksIterator iterator = rocksDB.newIterator(new ReadOptions());

        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
            System.out.println(Arrays.toString(iterator.key()) +"  "+ Arrays.toString(iterator.value()));
        }
        iterator.status();

    }

    @Override
    public List<Node> getNode() throws IOException, ClassNotFoundException {
        List<Object> objects = seekByKey(CLIENT_NODES_LIST_KEY);
        List<Node> nodes = new ArrayList<>();
        for (Object o : objects) {
            nodes.add((Node) o);
        }
        return nodes;
    }

    @Override
    public Optional<Account> getMinerAccount() throws NoSuchAlgorithmException {

        Optional<Object> object = get(MINER_ACCOUNT);
        if (object.isPresent()) {
            String minerAddress = (String) object.get();
            return getAccount(minerAddress);
        }
        return Optional.absent();
    }

    @Override
    public boolean setMinerAccount(Account account) {

        if (null != account) {
            return put(MINER_ACCOUNT, account.getAccountAddr());
        } else {
            return false;
        }
    }

    @Override
    public Optional<List<Node>> getNodeList() {
        try {
            byte[] bytes = rocksDB.get(CLIENT_NODES_LIST_KEY.getBytes());
            String s=new String(bytes);
            return Optional.of(FastJson.deserialize(s));
        } catch (Exception e) {
            //System.out.println(e);
            if (logger.isDebugEnabled()) {
                logger.error("ERROR for RocksDB : {}", e);
            }
            return Optional.absent();
        }

    }

    @Override
    public boolean putNodeList(List<Node> nodes) {
        try {
            rocksDB.put(CLIENT_NODES_LIST_KEY.getBytes(), FastJson.serialize(nodes).getBytes());
            System.out.println("数据已经存放");
            return true;
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.error("ERROR for RocksDB : {}", e);
            }
            return false;
        }
        //return this.put(CLIENT_NODES_LIST_KEY, nodes);
    }

    @Override
     public synchronized boolean addNode(Node node)
    {
        Optional<List<Node>> nodeList = getNodeList();
        if (nodeList.isPresent()) {
            //已经存在的节点跳过
            if (nodeList.get().contains(node)) {
                return false;
            }
            //跳过自身节点
            Node self = new Node(tioProps.getServerIp(), tioProps.getListenPort());
            if (self.equals(node)) {
                return false;
            }
            nodeList.get().add(node);
            return putNodeList(nodeList.get());
        }
        else {
            ArrayList<Node> nodes = new ArrayList<>();
            nodes.add(node);
            return putNodeList(nodes);
        }
    }

    @Override
    public boolean put(String key, Object value) {
        try {
            rocksDB.put(key.getBytes(), SerializeUtils.serialize(value));
            System.out.println("数据已经存放");
            return true;
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.error("ERROR for RocksDB : {}", e);
            }
            return false;
        }
    }

    @Override
    public Optional<Object> get(String key) {

        try {
            return Optional.of(SerializeUtils.deserialize(rocksDB.get(key.getBytes())));
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.error("ERROR for RocksDB : {}", e);
            }
            return Optional.absent();
        }
    }

    @Override
    public boolean delete(String key) {
        try {
            rocksDB.delete(key.getBytes());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public <T> List<T> seekByKey(String keyPrefix) throws IOException, ClassNotFoundException {

        ArrayList<T> ts = new ArrayList<>();
        RocksIterator iterator = rocksDB.newIterator(new ReadOptions());
        byte[] key = keyPrefix.getBytes();
        for (iterator.seek(key); iterator.isValid(); iterator.next()) {
            ts.add((T) SerializeUtils.deserialize(iterator.value()));
        }

        return ts;
    }

    @Override
    public Transaction getTransactionByTxHash(String txHash)
    {

        Optional<Object> objectOptional = get(txHash);
        if (objectOptional.isPresent()) {
            Integer blockIndex = (Integer) objectOptional.get();
            Optional<Block> blockOptional = getBlock(blockIndex);
            if (blockOptional.isPresent()) {
                for (Transaction transaction : blockOptional.get().getBody().getTransactionList()) {
                    if (txHash.equals(transaction.getTransactionHash())) {
                        return transaction;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void closeDB() {
        if (null != rocksDB) {
            rocksDB.close();
        }
    }
}
