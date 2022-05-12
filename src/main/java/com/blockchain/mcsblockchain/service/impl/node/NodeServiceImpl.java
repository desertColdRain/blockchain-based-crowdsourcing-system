package com.blockchain.mcsblockchain.service.impl.node;

import com.blockchain.mcsblockchain.Utils.Constants;
import com.blockchain.mcsblockchain.Utils.JsonVo;
import com.blockchain.mcsblockchain.common.dto.NodeDto;
import com.blockchain.mcsblockchain.net.base.Node;
import com.blockchain.mcsblockchain.pojo.Return.Result;
import com.blockchain.mcsblockchain.pojo.core.Blockchain;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.blockchain.mcsblockchain.service.inter.node.NodeService;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class NodeServiceImpl implements NodeService {
    @Autowired
    DBAccess dbAccess;
    @Autowired
    Blockchain blockchain;

    @Override
    public Result getNodeList() {
        Optional<List<Node>> nodeList = dbAccess.getNodeList();
        System.out.println("进入获取节点列表功能");
        if (nodeList.isPresent()) {
            System.out.println("获取节点列表成功");
            System.out.println(nodeList.get());
            return new Result("获取节点列表成功", Constants.CODE_SUCCESS,nodeList.get());
        }
        else{
            System.out.println("当前节点列表为空");
            return new Result("当前节点列表为空",Constants.CODE_FAIL_SERVER,null);
        }
    }

    @Override
    public Result addNodeTo(@RequestBody NodeDto nodeDto) throws Exception {
        System.out.println(nodeDto);
        System.out.println("进入添加节点功能");
        System.out.println(nodeDto);
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy年MM月dd HH:mm:ss");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        System.out.println("现在时间：" + sdf.format(date));
        blockchain.addNode(nodeDto.getIp(), nodeDto.getPort(), sdf.format(date), nodeDto.getNotes());
        System.out.println(nodeDto);
        return new Result("添加节点成功",Constants.CODE_SUCCESS,nodeDto);
    }

    @Override
    public Result deleteNode(Node node) {
        System.out.println("进入删除节点功能");
        System.out.println(node);
        Optional<List<Node>> nodeList = dbAccess.getNodeList();
        if(nodeList.isPresent()){
            for(Node n:nodeList.get()){
                System.out.println(n+"  no");
                if(n.equals(node)) {
                    System.out.println("匹配成功");
                    nodeList.get().remove(node);
                    break;
                }
            }
            System.out.println(nodeList.get().size());
            dbAccess.putNodeList(nodeList.get());
            return new Result("删除节点成功",Constants.CODE_SUCCESS,nodeList);
        }
        else{
            return new Result("您要删除的节点不存在",Constants.CODE_FAIL_CLIENT,null);
        }
       // return null;
    }
}
