package cn.panda;

import cn.panda.ecdsa.WalletAddress;
import cn.panda.utils.ECDSAUtils;
/**
 * 钱包测试类
 * @author Administrator
 *
 */
public class Test {

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

}
