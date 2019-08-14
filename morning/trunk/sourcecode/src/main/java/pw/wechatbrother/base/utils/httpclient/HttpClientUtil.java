package pw.wechatbrother.base.utils.httpclient;

import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

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
				e.printStackTrace();
			}
			HttpGet get = new HttpGet(uri);
			resp = client.execute(get);
			int statusCode = resp.getStatusLine().getStatusCode();
			if(statusCode>=200&&statusCode<300) {
				HttpEntity entity = resp.getEntity();
				if(entity!=null){
					resposecontent = EntityUtils.toString(entity, "utf-8");
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
	 * @param requestUrl 输出get的url和参数的拼装
	 * @return String
	 * httpclient的get方法可以调用
	 */
	public static String httpClinetGetFunctionRetrunString(String requestUrl){
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
				e.printStackTrace();
			}
			HttpGet get = new HttpGet(uri);
			resp = client.execute(get);
			int statusCode = resp.getStatusLine().getStatusCode();
			if(statusCode>=200&&statusCode<300) {
				HttpEntity entity = resp.getEntity();
				if(entity!=null){
					resposecontent = EntityUtils.toString(entity, "utf-8");
					//jsonObject = JSONObject.fromObject(resposecontent);
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
		return resposecontent;
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
			 uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//HttpPost post = new HttpPost(requestUrl);
			HttpPost post = new HttpPost(uri);
			post.addHeader("Content-type","application/json");
			StringEntity entity = new StringEntity(outputStr, ContentType.create("application/json", "UTF-8"));
			post.setEntity(entity);
			//设置请求超时
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(4000).setConnectTimeout(4000).build();//设置请求和传输超时时间
			post.setConfig(requestConfig);
			resp = client.execute(post);
			int statusCode = resp.getStatusLine().getStatusCode();
			if(statusCode>=200&&statusCode<300) {
				 resposecontent = EntityUtils.toString(resp.getEntity(), "utf-8");
				if(resposecontent!=null&&!resposecontent.equals("")){
					jsonObject = JSONObject.fromObject(resposecontent);
				}else{
					System.out.println("调用接口："+requestUrl+"，请求参数："+outputStr+"。返回结果集为空！！");
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
	public static JSONObject httpClinetPostFunction(String requestUrl, String outputStr,int timeout){
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
			//HttpPost post = new HttpPost(requestUrl);
			HttpPost post = new HttpPost(uri);
			post.addHeader("Content-type","application/json");
			StringEntity entity = new StringEntity(outputStr, ContentType.create("application/json", "UTF-8"));
			post.setEntity(entity);
			//设置请求超时
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();//设置请求和传输超时时间
			post.setConfig(requestConfig);
			resp = client.execute(post);
			int statusCode = resp.getStatusLine().getStatusCode();
			if(statusCode>=200&&statusCode<300) {
				resposecontent = EntityUtils.toString(resp.getEntity(), "utf-8");
				if(resposecontent!=null&&!resposecontent.equals("")){
					jsonObject = JSONObject.fromObject(resposecontent);
				}else{
					System.out.println("调用接口："+requestUrl+"，请求参数："+outputStr+"。返回结果集为空！！");
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
	public static String httpClinetPostFunctionReturnString(String requestUrl, String outputStr){
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
			//HttpPost post = new HttpPost(requestUrl);
			HttpPost post = new HttpPost(uri);
			post.addHeader("Content-type","application/json");
			StringEntity entity = new StringEntity(outputStr, ContentType.create("application/json", "UTF-8"));
			post.setEntity(entity);
			//设置请求超时
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(4000).setConnectTimeout(4000).build();//设置请求和传输超时时间
			post.setConfig(requestConfig);
			resp = client.execute(post);
			int statusCode = resp.getStatusLine().getStatusCode();
			if(statusCode>=200&&statusCode<300) {
				resposecontent = EntityUtils.toString(resp.getEntity(), "utf-8");
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
		return resposecontent;
	}
	//用apache接口实现http的post提交数据
	public static String sendHttpClientPost(String path,
											Map<String, String> params, String encode) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				list.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}
		try {
			// 实现将请求的参数封装到表单中，即请求体中
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, encode);
			// 使用post方式提交数据
			HttpPost httpPost = new HttpPost(path);
			httpPost.setEntity(entity);
			// 执行post请求，并获取服务器端的响应HttpResponse
			CloseableHttpClient client=null;
			client = HttpClients.createDefault();
			CloseableHttpResponse httpResponse = null;
			httpResponse = client.execute(httpPost);
			//HttpResponse httpResponse = client.execute(httpPost);

			//获取服务器端返回的状态码和输入流，将输入流转换成字符串
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				InputStream inputStream = httpResponse.getEntity().getContent();
				return changeInputStream(inputStream, encode);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";

	}

	/*
	 * // 把从输入流InputStream按指定编码格式encode变成字符串String
	 */
	public static String changeInputStream(InputStream inputStream,
										   String encode) {

		// ByteArrayOutputStream 一般叫做内存流
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		String result = "";
		if (inputStream != null) {

			try {
				while ((len = inputStream.read(data)) != -1) {
					byteArrayOutputStream.write(data, 0, len);

				}
				result = new String(byteArrayOutputStream.toByteArray(), encode);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return result;
	}


	/**
	 * @data 20160229
	 * @author 郑景立
	 * @param url   上传的URL地址
	 * @param path  上传的路径
	 * @return
	 */
	public static JSONObject postMedia(String url,String path) {
		CloseableHttpClient client = null;
		CloseableHttpResponse resp = null;
		try {
			client = HttpClients.createDefault();
			/*
			String url = WeixinFinalValue.POST_MEDIA;
			url = url.replace("ACCESS_TOKEN", RefreshAccessTokenTask.at);
			url = url.replace("TYPE", type);*/
			HttpPost post = new HttpPost(url);
			FileBody fb = new FileBody(new File(path));
			HttpEntity reqEntity = MultipartEntityBuilder
						.create().addPart("media", fb).build();
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
