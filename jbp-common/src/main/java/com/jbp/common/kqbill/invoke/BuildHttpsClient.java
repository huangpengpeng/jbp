package com.jbp.common.kqbill.invoke;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bill99.crypto.service.CryptoService;
import com.bill99.crypto.service.processor.P7CryptoServiceProcessor;
import com.jbp.common.kqbill.contants.Bill99ConfigInfo;
import com.jbp.common.kqbill.utils.Bill99CertConfigLoader;
import com.jbp.common.kqbill.utils.FileLoader;
import com.jbp.common.kqbill.utils.RequestUrLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class BuildHttpsClient {

    private static final Logger logger = LoggerFactory.getLogger(BuildHttpsClient.class);

    //获取加密服务对象
    private static CryptoService cryptoService = P7CryptoServiceProcessor.getInstance();


	/*
     * @method function 请求快钱
     */
	public static String requestKQ(JSONObject reqData) throws Exception{
        /**
         * 对body明文进行加密+签名
         */
        JSONObject encryptBody =null;
        try {
            //设置加密证书参数
            cryptoService.setBill99CertConfig(Bill99CertConfigLoader.loadConfig());
            //调用加密方法
            encryptBody = cryptoService.seal(reqData.getString("requestBody"));
        }catch (Exception e){
            logger.error("请求快钱，加密加签出错。",e);
        }

        /**
         * 生成商户请求快钱的密文
         */
        JSONObject requestString = new JSONObject();
        requestString.put("head",reqData.get("head"));
        requestString.put("requestBody",encryptBody);
        logger.info("商户请求快钱密文 = {}" , requestString.toJSONString());
        /**
         * 请求快钱，解密验签获取返回明文
         */
        String messageType =reqData.getJSONObject("head").getString("messageType");
        //环境指定：sandbox-快钱测试环境；prod-快钱生产环境
        String url = RequestUrLoader.getRequestUrl(messageType,Bill99ConfigInfo.UMGW_ENV);
        logger.info("请求快钱环境：{}", Bill99ConfigInfo.UMGW_ENV);
        logger.info("请求快钱接口地址：{}",url);
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = new HttpPost(url);
        if(url.startsWith("https")) {
            httpClient = createHttpsClient();
        }else {
            httpClient = HttpClients.createDefault();// 兼容没有开启SSL认证
        }
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Integer.parseInt(Bill99ConfigInfo.SO_TIMEOUT))
                .setConnectTimeout(Integer.parseInt(Bill99ConfigInfo.CONN_TIMEOUT)).build();
        httpPost.setConfig(requestConfig);
        httpPost.setEntity(new StringEntity(requestString.toJSONString(), Consts.UTF_8));
        String resp = httpClient.execute(httpPost, new ResponseHandler<String>(){
            @Override
            public String handleResponse(HttpResponse httpresponse) throws ClientProtocolException, IOException {
                int statusCode = httpresponse.getStatusLine().getStatusCode();
                logger.info("99bill-response-statusCode = {}",statusCode);
                if( 200 != statusCode ){
                    logger.error("与快钱通信异常，业务状态未知，请查询确认！http-status-Code = {}",statusCode);
                    return null ;
                }else{
                    String entityStr = "";
                    HttpEntity entity = httpresponse.getEntity();
                    if( null != entity ) {
                        entityStr = EntityUtils.toString(entity, Consts.UTF_8);
                        logger.info("快钱返回原始信息 = {}" ,entityStr);
                        JSONObject respEncryptObject = JSONObject.parseObject(entityStr);
                        JSONObject respHead = respEncryptObject.getJSONObject("head");
                        JSONObject respEncryptBody = respEncryptObject.getJSONObject("responseBody");

                        JSONObject respDecryptObject = new JSONObject();
                        String respSignedData = respEncryptBody.getString("signedData");
                        String respEnvelopedData = respEncryptBody.getString("envelopedData");
                        if( "0000".equals(respHead.getString("responseCode")) ){
                            String respDecryptBody = null;
                            try {
                                respDecryptBody = cryptoService.unSeal(respEnvelopedData,respSignedData);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            respDecryptObject.put("head",respHead);
                            respDecryptObject.put("responseBody", JSON.parse(respDecryptBody));
                            if( StringUtils.isNotEmpty(respDecryptBody) ){
                                logger.info("快钱返回明文信息 = {}" ,respDecryptObject.toJSONString());
                                return respDecryptObject.toJSONString();
                            }else{
                                logger.error("快钱返回报文解密验签失败，请检查报错情况！");
                                return null;
                            }
                        }else{
                            logger.error("请求快钱出错, head报错信息 = {}",respHead.toJSONString());
                            return respHead.toJSONString();
                        }
                    }else{
                        logger.error("快钱返回内容为空，业务状态未知，请查询确认！");
                        return null ;
                    }
                }
            }
        });
		return resp;
	}


	
	/*
     * @method function 读取ssl证书，设置请求属性，构建请求
     */

    private static CloseableHttpClient createHttpsClient() throws Exception {

        logger.info("loading merchant ssl client cert , location：{}", Bill99ConfigInfo.SSL_PRI_PATH);
            InputStream keystoreFileInputStream =
                    FileLoader.getCertFileStream(Bill99ConfigInfo.SSL_PRI_PATH);
        KeyStore keyStore = KeyStore.getInstance(Bill99ConfigInfo.STORE_TYPE);
            String password = Bill99ConfigInfo.SSL_PRI_PWD;
            try{
            keyStore.load(keystoreFileInputStream, password.toCharArray());
            HttpClientCert httpClientCert= new HttpClientCert();
            httpClientCert.setKeyStore(keyStore);
            httpClientCert.setKeyStorePwd(password);
            
            String soTimeout = Bill99ConfigInfo.SO_TIMEOUT;
            String connTimeout = Bill99ConfigInfo.CONN_TIMEOUT;
            String sslVersion = Bill99ConfigInfo.TLS_VERSION;
            httpClientCert.setConnTimeout(Integer.parseInt(connTimeout));
            httpClientCert.setSoTimeout(Integer.parseInt(soTimeout));
            httpClientCert.setSSLVersion(sslVersion);
            
            HttpsClientFactory factory = new HttpsClientFactory();
            return factory.createSSLClient(httpClientCert);

	        }catch (Exception e) {
	            throw e;
	        }finally {
	           
	            if(keystoreFileInputStream != null) {
	                keystoreFileInputStream.close();
	            }
	        }
    }


}
