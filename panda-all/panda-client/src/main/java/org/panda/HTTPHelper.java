package org.panda;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

import cn.panda.entity.TradeEntity;

/**
 * 用于发送交易至 server项目
 * @author www
 *
 */
public class HTTPHelper {
	
	private static final Logger logger = Logger.getLogger(HTTPHelper.class);
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
			String url = "http://localhost:8080/panda-server/panda/trade";
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
}
