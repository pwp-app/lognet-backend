package app.pwp.lognet.utils.network;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Http {
    public static HttpResult doGet(String url) throws Exception {
        HttpClient httpClient = HttpClients.createDefault();
        // 声明URIBuilder
        URIBuilder uriBuilder = new URIBuilder(url);

        // 创建httpGet对象，相当于设置url请求地址
        HttpGet httpGet = new HttpGet(uriBuilder.build());

        // 使用HttpClient执行httpGet，相当于按回车，发起请求
        CloseableHttpResponse response = null;
        try {
            response = (CloseableHttpResponse) httpClient.execute(httpGet);
        } catch (IOException e) {
            HttpResult httpResult = new HttpResult(400, "请求失败");
            return httpResult;
        }

        HttpResult httpResult = new HttpResult();
        // 解析数据封装HttpResult
        if (response.getEntity() != null) {
            httpResult.setCode(response.getStatusLine().getStatusCode());
            httpResult.setBody(EntityUtils.toString(response.getEntity(),"UTF-8"));
        } else {
            httpResult.setCode(response.getStatusLine().getStatusCode());
        }

        return httpResult;
    }

    public static HttpResult doGet(String url, Map<String, Object> map) throws Exception {
        HttpClient httpClient = HttpClients.createDefault();
        // 声明URIBuilder
        URIBuilder uriBuilder = new URIBuilder(url);

        // 判断参数map是否为非空
        if (map != null) {
            // 遍历参数
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                // 设置参数
                uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
            }
        }

        // 创建httpGet对象，相当于设置url请求地址
        HttpGet httpGet = new HttpGet(uriBuilder.build());

        // 使用HttpClient执行httpGet，相当于按回车，发起请求
        CloseableHttpResponse response = null;
        try {
            response = (CloseableHttpResponse) httpClient.execute(httpGet);
        } catch (IOException e) {
            HttpResult httpResult = new HttpResult(400, "请求失败");
            return httpResult;
        }

        HttpResult httpResult = new HttpResult();
        // 解析数据封装HttpResult
        if (response.getEntity() != null) {
            httpResult.setCode(response.getStatusLine().getStatusCode());
            httpResult.setBody(EntityUtils.toString(response.getEntity(),"UTF-8"));
        } else {
            httpResult.setCode(response.getStatusLine().getStatusCode());
        }

        return httpResult;
    }

    public static HttpResult doPost(String url, Map<String, Object> map) throws Exception {
        HttpClient httpClient = HttpClients.createDefault();
        // 声明httpPost请求
        HttpPost httpPost = new HttpPost(url);
        // 判断map不为空
        if (map != null) {
            // 声明存放参数的List集合
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // 遍历map，设置参数到list中
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            // 创建form表单对象
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "UTF-8");
            // 把表单对象设置到httpPost中
            httpPost.setEntity(formEntity);
        }

        // 使用HttpClient发起请求，返回response
        CloseableHttpResponse response = null;
        try {
            response = (CloseableHttpResponse) httpClient.execute(httpPost);
        } catch (IOException e) {
            HttpResult httpResult = new HttpResult(400, "请求失败");
            return httpResult;
        }

        // 解析response封装返回对象httpResult
        HttpResult httpResult = new HttpResult();

        // 解析数据封装HttpResult
        if (response.getEntity() != null) {
            httpResult.setCode(response.getStatusLine().getStatusCode());
            httpResult.setBody(EntityUtils.toString(response.getEntity(),"UTF-8"));
        } else {
            httpResult.setCode(response.getStatusLine().getStatusCode());
        }

        // 返回结果
        return httpResult;
    }
}