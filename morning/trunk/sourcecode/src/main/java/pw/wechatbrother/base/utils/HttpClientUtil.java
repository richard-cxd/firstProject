package pw.wechatbrother.base.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONObject;

/**
 * @data20160229
 * @author zhengjingli
 * 尝试一下用httpclient来封装一下微信的各种接口 
 */
public class HttpClientUtil {

	/**
	 * @data 20160229
	 * @author zhengjingli
	 * @param requestUrl 输出get的url和参数的拼装
	 * @return JSONObject
	 * httpclient的get方法可以调用
	 */
	public static JSONObject httpClinetGetFunction(String requestUrl){
		JSONObject jsonObject = null;
		String resposecontent = null;
		CloseableHttpClient client = null;
		CloseableHttpResponse resp = null;
		try {
			client = HttpClients.createDefault();
			URL url = new URL(requestUrl);
			URI uri =null;
			try {
				uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpGet get = new HttpGet(uri);
			resp = client.execute(get);
			int statusCode = resp.getStatusLine().getStatusCode();
			if(statusCode>=200&&statusCode<300) {
				HttpEntity entity = resp.getEntity();
				if(entity!=null){
					resposecontent = EntityUtils.toString(entity,"utf-8");
					jsonObject = JSONObject.fromObject(resposecontent);
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(resp!=null) resp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(client!=null) client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}


	/**
	 * @data 20160229
	 * @author zhengjingli
	 * @param requestUrl Post请求的url地址
	 * @param outputStr  要发送的参数
	 * @return JSONObject
	 * httpclient的POST方法可以调用
	 */
	public static JSONObject httpClinetPostFunction(String requestUrl, String outputStr){
		JSONObject jsonObject = null;
		String resposecontent = null;
		CloseableHttpClient client = null;
		CloseableHttpResponse resp = null;
		try {
			client = HttpClients.createDefault();
			URL url = new URL(requestUrl);
			URI uri =null;
			try {
				uri = new URI(url.getProtocol(), url.getAuthority(), url.getPath(), url.getQuery(), null);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//HttpPost post = new HttpPost(requestUrl);
			HttpPost post = new HttpPost(uri);
			post.addHeader("Content-type","application/json");
			System.out.println(outputStr);
			StringEntity entity = new StringEntity(outputStr, ContentType.create("application/json", "UTF-8"));
			post.setEntity(entity);
			resp = client.execute(post);
			int statusCode = resp.getStatusLine().getStatusCode();
			if(statusCode>=200&&statusCode<300) {
				resposecontent = EntityUtils.toString(resp.getEntity(),"utf-8");
				jsonObject = JSONObject.fromObject(resposecontent);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(resp!=null) resp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(client!=null) client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}

	//把JSONObject转换成键值对形式返回
	public static HashMap<String, Object> transfer(JSONObject jsonObject){
		HashMap<String, Object> data = new HashMap<String, Object>();
		Iterator it = jsonObject.keys();
		// 遍历jsonObject数据，添加到Map对象
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			Object value = jsonObject.get(key);
			data.put(key, value);
		}
		return data;
	}

	//调用文件服务器接口上传文件
	public static JSONObject uploadFile(File file,String url) {
		CloseableHttpClient client = null;
		CloseableHttpResponse resp = null;
		try {
			client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			// 以浏览器兼容模式运行，防止文件名乱码。
			FileBody bin = new FileBody(file);
			HttpEntity reqEntity = MultipartEntityBuilder.create()
					.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
					.addPart("uploadFile", bin)
					.setCharset(CharsetUtils.get("UTF-8")).build();
			//设置请求超时
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(4000).setConnectTimeout(4000).build();//设置请求和传输超时时间
			post.setConfig(requestConfig);

			post.setEntity(reqEntity);
			resp = client.execute(post);
			int sc = resp.getStatusLine().getStatusCode();
			if(sc>=200&&sc<300) {
				String json = EntityUtils.toString(resp.getEntity());
				return JSONObject.fromObject(json);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(client!=null) client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(resp!=null) resp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
