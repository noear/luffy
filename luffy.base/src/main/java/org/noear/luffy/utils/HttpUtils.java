package org.noear.luffy.utils;

import okhttp3.*;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Note;
import org.noear.weed.cache.ICacheServiceEx;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class HttpUtils {
    private final static Supplier<Dispatcher> httpClientDispatcher = () -> {
        Dispatcher temp = new Dispatcher();
        temp.setMaxRequests(20000);
        temp.setMaxRequestsPerHost(10000);
        return temp;
    };

    private final static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .dispatcher(httpClientDispatcher.get())
            .addInterceptor(HttpInterceptor.instance)
            .build();

    public static HttpUtils http(String url) {
        //默认为长时间
        return new HttpUtils(url, httpClient);
    }


    private OkHttpClient _client;
    private String _url;
    private Charset _charset;
    private Map<String, String> _cookies;
    private RequestBody _body;
    private List<KeyValue> _form;
    private boolean _multipart = false;

    private MultipartBody.Builder _part_builer;
    private Request.Builder _builder;

    public HttpUtils(String url, OkHttpClient client) {
        if (client == null) {
            _client = httpClient;
        } else {
            _client = client;
        }

        _url = url;
        _builder = new Request.Builder().url(url);
    }

    /**
     * 短时间处理
     */
    public HttpUtils asShortHttp() {
        return timeout(10, 10, 60);
    }

    /**
     * 长时间处理
     */
    public HttpUtils asLongHttp() {
        return timeout(30, 60*5, 60*5);
    }

    public HttpUtils timeout(int timeoutSeconds) {
        if (timeoutSeconds > 0) {
            _builder.tag(HttpTimeout.class, new HttpTimeout(timeoutSeconds));
        }

        return this;
    }

    public HttpUtils timeout(int connectTimeoutSeconds, int writeTimeoutSeconds, int readTimeoutSeconds) {
        if (connectTimeoutSeconds > 0) {
            _builder.tag(HttpTimeout.class, new HttpTimeout(connectTimeoutSeconds, writeTimeoutSeconds, readTimeoutSeconds));
        }

        return this;
    }

    @Note("设置multipart")
    public HttpUtils multipart(boolean multipart) {
        _multipart = multipart;
        return this;
    }

    @Note("设置UA")
    public HttpUtils userAgent(String ua) {
        _builder.header("User-Agent", ua);
        return this;
    }

    @Note("设置charset")
    public HttpUtils charset(String charset) {
        _charset = Charset.forName(charset);
        return this;
    }

    @Note("设置请求头")
    public HttpUtils headers(Map<String, Object> headers) {
        if (headers != null) {
            headers.forEach((k, v) -> {
                _builder.header(k, v.toString());
            });
        }

        return this;
    }

    @Note("设置请求头")
    public HttpUtils header(String name, String value) {
        if (name != null) {
            _builder.header(name, value);
        }
        return this;
    }

    @Note("添加请求头（可添加多个同名头）")
    public HttpUtils headerAdd(String name, String value) {
        if (name != null) {
            _builder.addHeader(name, value);
        }
        return this;
    }

    @Note("设置表单数据")
    public HttpUtils data(Map<String, Object> data) {
        tryInitForm();

        if (data != null) {
            data.forEach((k, v) -> _form.add(new KeyValue(k, v.toString())));
        }

        return this;
    }

    @Note("设置表单数据")
    public HttpUtils data(String key, String value) {
        tryInitForm();
        _form.add(new KeyValue(key, value));
        return this;
    }

    @Note("设置表单文件")
    public HttpUtils data(String key, String filename, InputStream inputStream, String contentType) {
        multipart(true);
        tryInitPartBuilder(MultipartBody.FORM);

        _part_builer.addFormDataPart(key,
                filename,
                new StreamBody(contentType, inputStream));

        return this;
    }

    @Note("设置BODY txt")
    public HttpUtils bodyTxt(String txt) {
        return bodyTxt(txt, null);
    }

    @Note("设置BODY txt及内容类型")
    public HttpUtils bodyTxt(String txt, String contentType) {
        if (contentType == null) {
            _body = FormBody.create(null, txt);
        } else {
            _body = FormBody.create(MediaType.parse(contentType), txt);
        }

        return this;
    }

    @Note("设置BODY raw")
    public HttpUtils bodyRaw(InputStream raw){
        return bodyRaw(raw, null);
    }

    @Note("设置BODY raw及内容类型")
    public HttpUtils bodyRaw(InputStream raw, String contentType) {
        _body = new StreamBody(contentType, raw);

        return this;
    }


    @Note("设置请求cookies")
    public HttpUtils cookies(Map<String, Object> cookies) {
        if (cookies != null) {
            tryInitCookies();

            cookies.forEach((k, v) -> {
                _cookies.put(k, v.toString());
            });
        }

        return this;
    }

    @Note("执行请求，返回响应对象")
    public Response exec(String mothod) throws Exception {
        if (_multipart) {
            tryInitPartBuilder(MultipartBody.FORM);

            if (_form != null) {
                _form.forEach((kv) -> {
                    _part_builer.addFormDataPart(kv.key, kv.value);
                });
            }

            try {
                _body = _part_builer.build();
            }catch (IllegalStateException ex){
                //这里不要取消（内容为空时，会出错）
            }
        } else {
            if (_form != null) {
                FormBody.Builder fb = new FormBody.Builder(_charset);

                _form.forEach((kv) -> {
                    fb.add(kv.key, kv.value);
                });
                _body = fb.build();
            }
        }

        if (_cookies != null) {
            _builder.header("Cookie", getRequestCookieString(_cookies));
        }


        switch (mothod.toUpperCase()) {
            case "GET":
                _builder.method("GET", null);
                break;
            case "POST":
                _builder.method("POST", bodyDo());
                break;
            case "PUT":
                _builder.method("PUT", bodyDo());
                break;
            case "DELETE":
                _builder.method("DELETE", bodyDo());
                break;
            case "PATCH":
                _builder.method("PATCH", bodyDo());
                break;
            case "HEAD":
                _builder.method("HEAD", null);
                break;
            case "OPTIONS":
                _builder.method("OPTIONS", null);
                break;
            case "TRACE":
                _builder.method("TRACE", null);
                break;
            default:
                throw new IllegalArgumentException("This method is not supported");
        }

        Call call = _client.newCall(_builder.build());
        return call.execute();
    }

    private RequestBody bodyDo() {
        if (_body == null) {
            _body = RequestBody.create(null, "");
        }

        return _body;
    }

    private int _cache=0;
    @Note("缓存时间")
    public HttpUtils cache(int seconds) throws Exception {
        _cache = seconds;
        return this;
    }

    @Note("执行请求，返回字符串")
    public String exec2(String mothod) throws Exception {
        if (_cache > 0) {
            ICacheServiceEx c = (ICacheServiceEx) Solon.global().shared().get("cache");
            if (c != null) {
                String url_key = _url;
                if(_form != null) {
                    url_key = EncryptUtils.md5(_url + ONode.stringify(_form));
                }

                return c.getBy(_cache, url_key, (uc) -> exec2_do(mothod));
            } else {
                return exec2_do(mothod);
            }
        } else {
            return exec2_do(mothod);
        }
    }

    private String exec2_do(String mothod) throws Exception {
        Response tmp = exec(mothod);
        int code = tmp.code();
        String text = tmp.body().string();

        //如果非空，直接返回
        if(TextUtils.isEmpty(text) == false){
            return text;
        }

        if (code >= 200 && code <= 300) {
            return text;
        } else {
            throw new HttpResultException(code + " 错误：" + text);
        }
    }

    @Note("发起GET请求，返回字符串（REST.select 从服务端获取一或多项资源）")
    public String get() throws Exception {
        return exec2("GET");
    }

    @Note("发起POST请求，返回字符串（REST.create 在服务端新建一项资源）")
    public String post() throws Exception {
        return exec2("POST");
    }

    @Note("发起PUT请求，返回字符串（REST.update 客户端提供改变后的完整资源）")
    public String put() throws Exception {
        return exec2("PUT");
    }

    @Note("发起PATCH请求，返回字符串（REST.update 客户端提供改变的属性）")
    public String patch() throws Exception {
        return exec2("PATCH");
    }

    @Note("发起DELETE请求，返回字符串（REST.delete 从服务端删除资源）")
    public String delete() throws Exception {
        return exec2("DELETE");
    }

    private static String getRequestCookieString(Map<String, String> cookies) {
        StringBuilder sb = StringUtils.borrowBuilder();
        boolean first = true;

        for (Map.Entry<String, String> kv : cookies.entrySet()) {
            sb.append(kv.getKey()).append('=').append(kv.getValue());
            if (!first) {
                sb.append("; ");
            } else {
                first = false;
            }
        }

        return StringUtils.releaseBuilder(sb);
    }

    private void tryInitPartBuilder(MediaType type) {
        if (_part_builer == null) {
            _part_builer = new MultipartBody.Builder().setType(type);
        }
    }

    private void tryInitForm() {
        if (_form == null) {
            _form = new ArrayList<>();
        }
    }

    private void tryInitCookies() {
        if (_cookies == null) {
            _cookies = new HashMap<>();
        }
    }

    public static class StreamBody extends RequestBody {
        private MediaType _contentType = null;
        private InputStream _inputStream = null;

        public StreamBody(String contentType, InputStream inputStream) {
            if (contentType != null) {
                _contentType = MediaType.parse(contentType);
            }

            _inputStream = inputStream;
        }

        @Override
        public MediaType contentType() {
            return _contentType;
        }

        @Override
        public long contentLength() throws IOException {
            return _inputStream.available();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            Source source = null;

            try {
                source = Okio.source(_inputStream);
                sink.writeAll(source);
            } finally {
                Util.closeQuietly(source);
            }
        }
    }

    public static class KeyValue {
        String key;
        String value;

        public KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
