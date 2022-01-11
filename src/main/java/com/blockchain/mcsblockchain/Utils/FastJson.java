package com.blockchain.mcsblockchain.Utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.blockchain.mcsblockchain.net.base.Node;
import java.util.List;

public class FastJson {

    public static String serialize(List<Node> nodes){
        return JSON.toJSONString(nodes);
    }

    public static  List<Node> deserialize(String s){
        return JSON.parseObject(s, new TypeReference<List<Node>>() {});
    }

}
