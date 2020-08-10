package cn.panda.utils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.util.Base64Utils;

import cn.panda.ecdsa.WalletAddress;

/**
 * 提供ecdsa生成钱包（公钥，私钥，地址）、签名、验证
 * @author www
 *
 */
public class ECDSAUtils {
	private static Logger logger = Logger.getLogger(ECDSAUtils.class);
	/**
     * 加密算法
     */
    private static final String KEY_ALGORITHM = "EC";
    /**
     * 签名算法
     */
    private static final String SIGNATURE_ALGORITHM = "SHA1withECDSA";
	
	/**
	 * md5计算摘要
	 * @param content
	 * @return
	 */
	public static String encryptMD5(String content){
		try {
			if(StringUtils.isEmpty(content)){
				logger.error("加密明文不能为空");
			}
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] result = md.digest(content.getBytes("UTF-8"));
			return HexUtils.toHexString(result);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	/**
	 * 生成panda币钱包
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	 public static WalletAddress genWalletAddresst() throws NoSuchAlgorithmException {
		 KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
	     keyPairGen.initialize(256);
	     KeyPair keyPair = keyPairGen.generateKeyPair();
	     WalletAddress walletAddress = new WalletAddress();
	     String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
	     String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
	     walletAddress.setPublicKey(publicKey);
	     walletAddress.setPrivateKey(privateKey);
	     walletAddress.setAddress("0x" + encryptMD5(publicKey));//使用公钥md5码作为地址
	     return walletAddress;
	 }
	 /**
	  * 使用私钥签名
	  * @param data
	  * @param privateKey
	  * @return
	  * @throws Exception
	  */
	public static String sign(byte[] data, String privateKey) throws Exception {
	    byte[] keyBytes =Base64Utils.decodeFromString(privateKey);
	    PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
	    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
	    PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
	    Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
	    signature.initSign(privateK);
	    signature.update(data);
	    return Base64Utils.encodeToString(signature.sign());
	}
	/**
	 * 使用公钥验证签名
	 * @param data
	 * @param publicKey
	 * @param sign
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        byte[] keyBytes = Base64Utils.decodeFromString(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64Utils.decodeFromString(sign));
    }

}
