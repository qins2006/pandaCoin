## pandaCoin

一款基于海豚BaaS平台快速开发的一套区块链系统。

## 项目简介

------

panda一款基于海豚BaaS平台快速开发的一套区块链系统。基于平台更简单的区块链数据服务。以更少的代价更低的学习成本，快速实现真正的区块链应用。这里以熊猫币为例子实现数字货币系统的大部分功能。  
本文档的受众群体为 有一定的数字货币基础的开发人员。  

其功能主要实现：  

> * ECDSA钱包
> * 交易客户端类
> * 创建主币种panda币
> * 创建子币种
> * 主币种转账（panda币转账消耗panda币作为gas）
> * 子币种转账（子币种转账消耗panda币作为gas）
> * 转账接口
> * 企业区块更新
> * 地址余额查询

## 文档目录

 1. 如何对接HiteBaaS平台  
 2. 项目说明（如何开发自己的数字货币系统）  
 3. 接口文档  


#对接HiteBaaS平台  
官网： https://www.hitebaas.com  
申请入驻平台： https://www.hitebaas.com/hitebaas/index/apply.html  
企业申请指南：该页面简述了如何对接平台，最简单的话可直接联系页面最下方的邮箱地址 hitebaas@synctech.net.cn ，会有专人客服和你对接。正如平台所说“以更少的代价更低的学习成本”便可创建自己的区块链应用。  
  
在平台下方“API工具下载” ，下载工具 hitebaas-api-0.0.1-SNAPSHOT.jar。  
或者点击链接下载：https://www.hitebaas.com/hitebaas/index/api.zip  

和平台人员对接后：  
1、确认数据格式。   
2、获得平台给的appId、appKey、secretKey（这很重要，后面将使用这三个字符串和平台数据交互）。  
3、登录入驻企业后台，下载区块更新节点（后续会讲到如何从该节点上获取本项目相关的交易）。  
  
pandaCoin 入驻HiteBaaS的约定数据字段如下（仅供参考）  

| 数据英文名称        | 数据中文名称   | 
| --------   | -----:  | 
| tradehash     | 交易哈希 | 
| fromAddress       |   转出地址   |
| toAddress       |    转入地址    |
| amount       |    交易币额    |
| coinType       |    币种类型    |
| gas       |    燃油费    |
| gasCoinType       |    燃油费币种    |
| type       |    交易种类    |
| sign       |    签名    |
| publickey       |    公钥    |

对应项目内java类名TradeEntity。

**引入工具类 hitebaas-api-0.0.1-SNAPSHOT.jar。**

将包安装到本地仓库：
```
mvn install:install-file -DgroupId=com.hitebaas -DartifactId=hitebaas-api -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -Dfile=hitebaas-api-0.0.1-SNAPSHOT.jar
``` 
maven项目引入：
``` 
<dependency>
	  <groupId>com.hitebaas</groupId>
	  <artifactId>hitebaas-api</artifactId>
	  <version>0.0.1-SNAPSHOT</version>
  </dependency>
```
使用方式：
```
DataHelper<TradeEntity> dataHelper = new DataHelper<TradeEntity>();
boolean b = dataHelper.send(tradeEntity, appId, appKey, secretKey, null);
```
panda-server(项目后台)将使用该工具包和HiteBaaS平台交互。  

与平台的人员联调下TradeEntity的数据格式没问题，能打包上链就OK了，到这里和平台相关的对接工作就节本完成了。  
 
#项目说明
涉及到的一些技术： maven、spring boot、mongoDB

**项目结构说明：**
> 
panda-all  
---- panda-client  
---- panda-common  
---- panda-parent  
---- panda-server  
---- pom.xml  

panda-client : 模拟App钱包客户端，用于向系统后台（panda-server）发送交易。  
panda-common ： 一些公共的模型、工具类。   
panda-parent :  版本管理。  
panda-server ：项目核心后台。  
**1、交易客户端类**
项目里将使用它模拟APP移动客户端端。  
```
/**
	 * 模拟发送交易工具
	 * 若在spring项目里面可使用restTemplate 会更加优雅
	 * @param te
	 * @param blockHash
	 * @param workload
	 * @return
	 */
	public static boolean sendTrade(TradeEntity te) {
		CloseableHttpResponse response = null;
		HttpPost httpPost = null;
		try {
			String url = "http://localhost:8080/panda-server/panda/trade";//项目后台接口
			System.out.println(url);
			String senMsg = new Gson().toJson(te);
			httpPost = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(15000).setSocketTimeout(15000).build();
            httpPost.setConfig(requestConfig);
			CloseableHttpClient httpClient = HttpClients.createDefault();
			StringEntity entity = new StringEntity(senMsg, Charset.forName("UTF-8"));
			entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
			httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);
			if(200 == response.getStatusLine().getStatusCode()) {
				 String resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
				 System.out.println(resultString);
				return true;
			}else {
				return false;
			}
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}finally {
			try {
				if(httpPost != null) {
					httpPost.releaseConnection();
				}
				if(response!=null)
					response.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
        return false;
	}
```
**2、创建钱包**
panda-common项目的测试类Test.java
```
@org.junit.Test
	public void test() {
		try {
			System.out.println("-----------------------------------------------");
			WalletAddress wa = ECDSAUtils.genWalletAddresst();
			System.out.println("地址： 	" + wa.getAddress());
			System.out.println("公钥： 	" + wa.getPublicKey());
			System.out.println("私钥： 	" + wa.getPrivateKey());
			System.out.println("-----------------------------------------------");

			System.out.println("-----------------------------------------------");
			String content = "aasd我是测试字符串+--2";
			String sign = ECDSAUtils.sign(content.getBytes("UTF-8"), wa.getPrivateKey());
			System.out.println("签名sign:	" + sign);

			System.out.println("-----------------------------------------------");
			System.out.println("-----------------------------------------------");
			boolean b = ECDSAUtils.verify(content.getBytes("UTF-8"), wa.getPublicKey(), sign);
			System.out.println("签名验证：	" + b);
			System.out.println("-----------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
```
该方法将生成地址、公钥、私钥。  
如：app客户端上生成，则把它们保存在app移动端。由用户自己保管自己的秘钥。  

**3、创建主币种panda币**  
***（主币在项目启动之初就应该创建，项目保证了只能创建名为panda的主币种且只能创建一次，开发者可根据自己的需要去更改名称）***  
所有交易包括创建币种、交易。都将通过上诉和平台约定的字段来提现。  
我们这里做个共识。 
字段 type（交易种类）等于 1时，为创建币种交易。  
满足条件fromAddress(转出地址)=toAddress（转入地址），amount（交易额）为币种初始量。  
主币种：创建主币时coinType='panda',gasCoinType='panda'币。  
panda-client项目的CreateCoin单元测试方法。  
```
/**
	 * 创建主币种panda币（熊猫币）
	 */
@org.junit.Test
	public void createPanda() {
		try {
			//私钥
			String privateKey = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCBDeowxYhK+Qkrv9ScbY1qtRXMvZFxeFmUNxOzEgq6VuA==";
			//公钥
			String publicKey= "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE/5xeVT8EEdauYQUs5KV21Ozv9xmWzyMDoGaFg9ywyAcAoawz+Aj0ius4jyvjy+wGnafelonUYJHoC/bOroQiFw==";
			//地址
			String address = "0x4173c715a814a30c9167d24aeb01aaef";
			HTTPHelper httpHelper = new HTTPHelper();
			TradeEntity te = new TradeEntity();
			te.setAmount("10000000");//主币种初始额
			te.setCoinType("panda");//主币种名称
			te.setFromAddress(address);//转出地址（初始额接收地址）
			te.setGas("0");//主币种这里为0.
			te.setGasCoinType("panda");//gas币种也为0
			te.setToAddress(address);//转入地址=转出地址
			te.setType("1");//交易类型1
			te.setTradehash(te.md5Tradehash());//生成交易哈希
			te.setPublickey(publicKey);//公钥
			String sign = ECDSAUtils.sign(te.getTradehash().getBytes("UTF-8"), privateKey);//签名
			te.setSign(sign);
			//发送创建主币交易至项目后台，后台转发至HiteBaas平台
			boolean b = HTTPHelper.sendTrade(te);
			if(b) {
				System.out.println(b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
```
**4、子币种：**  
创建币种时coinType='panda',gasCoinType='子币种名称'币并且需消耗1000panda币作为燃油费。  

```
/**
	 * 创建子币种deer币种(小鹿币)
	 */
	@org.junit.Test
	public void createDeer() {
		try {
			//私钥
			String privateKey = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCBDeowxYhK+Qkrv9ScbY1qtRXMvZFxeFmUNxOzEgq6VuA==";
			//公钥
			String publicKey= "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE/5xeVT8EEdauYQUs5KV21Ozv9xmWzyMDoGaFg9ywyAcAoawz+Aj0ius4jyvjy+wGnafelonUYJHoC/bOroQiFw==";
			//地址
			String address = "0x4173c715a814a30c9167d24aeb01aaef";
			
			HTTPHelper httpHelper = new HTTPHelper();
			TradeEntity te = new TradeEntity();
			te.setAmount("1000000");//初始币额
			te.setCoinType("Deer");//子币种名称
			te.setFromAddress(address);//创建地址（初始额接收地址）
			te.setGas("1000");//消耗1000的panda币作为gas
			te.setGasCoinType("panda");//使用主币种作为燃油费
			te.setToAddress(address);//转出地址=转入地址
			te.setType("1");//交易类型为1
			te.setTradehash(te.md5Tradehash());//生成交易哈希值
			te.setPublickey(publicKey);//公钥
			String sign = ECDSAUtils.sign(te.getTradehash().getBytes("UTF-8"), privateKey);//签名
			te.setSign(sign);
			//发送创建主币交易至项目后台，后台转发至HiteBaas平台
			boolean b = HTTPHelper.sendTrade(te);
			if(b) {
				System.out.println(b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
```
**5、转账交易：与各种主流币类似。**  
主币/子币交易：  
```
/**
	 * 币种交易
	 */
	@org.junit.Test
	public void trade() {
		
		try {
			String privateKey = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCBDeowxYhK+Qkrv9ScbY1qtRXMvZFxeFmUNxOzEgq6VuA==";
			String publicKey= "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE/5xeVT8EEdauYQUs5KV21Ozv9xmWzyMDoGaFg9ywyAcAoawz+Aj0ius4jyvjy+wGnafelonUYJHoC/bOroQiFw==";
			String address = "0x4173c715a814a30c9167d24aeb01aaef";
			
			String address0 = "0x694bc428d78c5149158c29a2a02a54b4";
			String publicKey0= "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEHnHSQn7sgHgfV1adaUhZ45O0v5EUkR1Oee1Vilg6SEW6kBmkl/jVN/uXjluZYGA7g0iGMeiM7sJei2iUd6pqzw==";
			String privateKey0 = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCCek3iy79+kskR1HkKYaX7AzEKrasnUqOqKdss/RrdUyQ==";
			
			HTTPHelper httpHelper = new HTTPHelper();
			TradeEntity te = new TradeEntity();
			te.setAmount("10.02");//转账币额
			te.setCoinType("Deer");//币种名称，如果这里是主币转账的话则为  panda
			te.setFromAddress(address);//转出地址
			te.setGas("0.0052");//gas
			//te.setGasCoinType("panda");
			te.setGasCoinType("panda");//使用主币作为燃油费
			te.setToAddress(address0);//转入地址
			te.setType("0");//交易类型0
			te.setTradehash(te.md5Tradehash());//生成交易哈希
			te.setPublickey(publicKey);//转出公钥
			String sign = ECDSAUtils.sign(te.getTradehash().getBytes("UTF-8"), privateKey);//使用转出私钥签名
			te.setSign(sign);
			//发送创建主币交易至项目后台，后台转发至HiteBaas平台
			boolean b = HTTPHelper.sendTrade(te);
			if(b) {
				System.out.println(b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
```
**6、企业区块更新**  
在官网上 登录 企业自己的后台，下载自己企业的HiteBaaS更新节点 hitebaas-client.rar  
  
> 启动企业节点步骤：  
1：打开enterprise.txt文件夹，将对应的appId,appKey,secretKey的xx换成对应项目的值，appId,appKey,secretKey可在个人中心对应项目点查询里得到    
2：执行sh脚本  
  
执行脚本后本地便开始更新自己企业的区块（如果自己数据量多的话请保证磁盘容量充足）。  
  
**该节点提供了一个接口。（这是重中之重），我们的一切交易都将由这个接口来确认是否成功上链。**  
后台项目panda-server会启动一个定时器BlockUpdateTimer.java 去轮询访问这个本地接口，获取交易队列。  
并将交易的信息，保存/更新至 MongoDB（以供所有app用户查询查询）。  
 
**7、通用数据查询接口**  
后台项目panda-server提供给App用户的查询接口。  
请求方式：GET。  
> http://localhost:8080/panda-server/data/要查询的43位地址@币种名称  
 如下查询地址 0x4173c715a814a30c9167d24aeb01aaef 有多少Deer 币：  
 http://localhost:8080/panda-server/data/0x4173c715a814a30c9167d24aeb01aaef@Deer  
 如下查询地址 0x4173c715a814a30c9167d24aeb01aaef panda 币：  
 http://localhost:8080/panda-server/data/0x4173c715a814a30c9167d24aeb01aaef@panda  
  
正常返回余额数据如下：  
```
{
   "code": 0,
   "msg": "Success",
   "content":    {
      "amount": 999989.98,
      "address": "0x4173c715a814a30c9167d24aeb01aaef"
   }
}
```

**后续将会推出如何将智能合约与HiteBaaS平台结合**
