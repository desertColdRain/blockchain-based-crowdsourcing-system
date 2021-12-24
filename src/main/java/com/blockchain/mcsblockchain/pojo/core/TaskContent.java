package com.blockchain.mcsblockchain.pojo.core;

import java.io.*;
import java.math.BigDecimal;

public class TaskContent implements Serializable {

    private String requirements;        //任务要求
    private BigDecimal bonus;           //任务奖金

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }
    public String serialize() throws IOException
    {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(this);
        String res = byteOut.toString("ISO-8859-1");
        return res;
    }

    public TaskContent deserialize(String str_input) throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(str_input.getBytes("ISO-8859-1"));
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        TaskContent res =(TaskContent) objIn.readObject();
        return res;
    }
}
