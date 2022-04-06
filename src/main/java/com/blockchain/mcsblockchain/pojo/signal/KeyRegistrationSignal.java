package com.blockchain.mcsblockchain.pojo.signal;

import com.blockchain.mcsblockchain.pojo.crypto.PKType;
import com.blockchain.mcsblockchain.pojo.crypto.Signature;

import java.io.Serializable;

public class KeyRegistrationSignal implements Serializable {
    private String cloudServerPublicKey;        // the public key of enclave in sgx
    private PKType publicKeyOfNode;             // the public key of node
    //use private key of node to sign on the content of address of node: denoted as sig(H(PKni),SKni);
    private Signature signatureOfNode;

    public String getCloudServerPublicKey() {
        return cloudServerPublicKey;
    }

    public void setCloudServerPublicKey(String cloudServerPublicKey) {
        this.cloudServerPublicKey = cloudServerPublicKey;
    }

    public PKType getPublicKeyOfNode() {
        return publicKeyOfNode;
    }

    public void setPublicKeyOfNode(PKType publicKeyOfNode) {
        this.publicKeyOfNode = publicKeyOfNode;
    }

    public Signature getSignatureOfNode() {
        return signatureOfNode;
    }

    public void setSignatureOfNode(Signature signatureOfNode) {
        this.signatureOfNode = signatureOfNode;
    }

    public KeyRegistrationSignal() {
    }

    public KeyRegistrationSignal(String cloudServerPublicKey, PKType publicKeyOfNode, Signature signatureOfNode) {
        this.cloudServerPublicKey = cloudServerPublicKey;
        this.publicKeyOfNode = publicKeyOfNode;
        this.signatureOfNode = signatureOfNode;
    }

    @Override
    public String toString() {
        return "KeyRegistrationSignal{" +
                "cloudServerPublicKey='" + cloudServerPublicKey + '\'' +
                ", publicKeyOfNode=" + publicKeyOfNode +
                ", signatureOfNode=" + signatureOfNode +
                '}';
    }
}
