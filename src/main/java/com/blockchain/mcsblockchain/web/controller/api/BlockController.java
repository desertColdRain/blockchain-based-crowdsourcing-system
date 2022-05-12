package com.blockchain.mcsblockchain.web.controller.api;

import com.blockchain.mcsblockchain.Utils.JsonVo;
import com.blockchain.mcsblockchain.net.base.Node;
import com.blockchain.mcsblockchain.pojo.core.Block;
import com.blockchain.mcsblockchain.pojo.core.Blockchain;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.blockchain.mcsblockchain.web.vo.req.NodeVo;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yangjian
 * @since 2018-04-07 上午10:50.
 */
@RestController
@RequestMapping("/api/chain")
@Api(tags = "Chain API", description = "区块链相关的 API")
public class BlockController {

	@Autowired
	private DBAccess dbAccess;
	@Autowired
	private Blockchain blockChain;

	/**
	 * 启动挖矿
	 * @return
	 */
	@ApiOperation(value="启动挖矿")
	@GetMapping("/mining")
	public JsonVo mining() throws Exception
	{
		Block block = blockChain.mining();
		JsonVo vo = new JsonVo();
		vo.setCode(JsonVo.CODE_SUCCESS);
		vo.setMessage("Create a new block");
		vo.setItem(block);
		return vo;
	}

	/**
	 * 浏览头区块
	 * @param request
	 * @return
	 */
	@ApiOperation(value="浏览头区块信息", notes="获取最新的区块信息")
	@PostMapping("/block/head")
	//JsonVo
	public String blockHead(HttpServletRequest request)
	{
		Optional<Block> block = dbAccess.getLastBlock();
		JsonVo success = JsonVo.success();
		if (block.isPresent()) {
			success.setItem(block.get());
		}
		return block.get().toString();

	}

	@ApiOperation(value="查询整个区块链",notes = "获取区块链上的所有信息")
	@PostMapping("block/blockchain")
	public String blockchain(HttpServletRequest request){
		JsonVo success = JsonVo.success();
		List<Block> blocks = new ArrayList<>();
		Optional<Object> lastBlockIndex = dbAccess.getLastBlockIndex();
		if(lastBlockIndex.isPresent()){
			int index=(int)lastBlockIndex.get();
			while(index>0){
				Optional<Block> block = dbAccess.getBlock(index);
				if(block.isPresent()){
					blocks.add(block.get());
				}
				index--;
			}
			success.setItem(blocks);

			return blocks.toString();
		}

		else{
			success.setCode(JsonVo.CODE_FAIL);
			success.setMessage("There is no block in the blockchain");
			return null;
		}
		//return success;
	}

	/**
	 * 添加节点
	 * @param node
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value="添加节点", notes="添加并连接一个节点")
	@ApiImplicitParam(name = "node", required = true, dataType = "NodeVo")
	@PostMapping("/node/add")
	public JsonVo addNode(@RequestBody NodeVo node) throws Exception {

		System.out.println("您输入的IP地址为： "+node.getIp());
		Preconditions.checkNotNull(node.getIp(), "server ip is needed.");
		Preconditions.checkNotNull(node.getPort(), "server port is need.");
		SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
		sdf.applyPattern("yyyy年MM月dd HH:mm:ss");// a为am/pm的标记
		Date date = new Date();// 获取当前时间
		System.out.println("现在时间：" + sdf.format(date));
		blockChain.addNode(node.getIp(), node.getPort(),sdf.format(date),"");
		return JsonVo.success();
	}

	/**
	 * 查看节点列表
	 * @return
	 */
	@ApiOperation(value="获取节点列表", notes="获取当前接连相连接的所有节点")
	@PostMapping("node/view")
	public JsonVo nodeList()
	{
		Optional<List<Node>> nodeList = dbAccess.getNodeList();
		JsonVo success = JsonVo.success();
		if (nodeList.isPresent()) {
			success.setItem(nodeList.get());
		}
		return success;
	}

}
