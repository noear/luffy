package org.noear.luffy.dso;

import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.handle.Context;
import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AConfigM;
import org.noear.luffy.utils.*;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.weed.DbContext;

import javax.naming.ldap.LdapContext;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 引擎扩展工具，提供一些基础的操作支持
 * */
public class JtUtil {
    public static final JtUtil g = new JtUtil();

    private final Map<String,DbContext> _db_cache = new HashMap<>();

    @Note("获取Ldap用户信息")
    public LdapUser ldap(String cfg, String userFilter, String userPassword) throws Exception {
        if (TextUtils.isEmpty(cfg)) {
            return null;
        }

        if (cfg.startsWith("@")) {
            //转为真实的配置
            cfg = cfgGet(cfg.substring(1));
        }

        Properties prop = Utils.buildProperties(cfg);
        String url = prop.getProperty("url");
        String baseDn = prop.getProperty("baseDn");
        String username = prop.getProperty("username");
        String paasword = prop.getProperty("paasword");

        LdapContext ldapCtx = null;

        try {
            ldapCtx = LdapUtils.ldapConnect(url, username, paasword);

            return LdapUtils.ldapAuth(ldapCtx, baseDn, userFilter, userPassword);
        } finally {
            if (ldapCtx != null) {
                ldapCtx.close();
            }
        }
    }

    /**
     * 生成GUID
     */
    @Note("生成GUID")
    public String guid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    @Note("获取当前用户IP")
    public String ip() {
        return IPUtils.getIP(Context.current());
    }

    /**
     * 生成数据库上下文
     */
    @Note("生成数据库上下文")
    public DbContext db(String cfg) throws Exception{
        return db(cfg,null);
    }

    @Note("生成数据库上下文")
    public DbContext db(String cfg, DbContext def) throws Exception {
        if (TextUtils.isEmpty(cfg)) {
            return def;
        }

        if (cfg.startsWith("@")) {
            //转为真实的配置
            cfg = cfgGet(cfg.substring(1));
        }

        DbContext tmp = _db_cache.get(cfg);

        if (tmp == null) {
            synchronized (cfg) {
                tmp = _db_cache.get(cfg);

                if (tmp == null) {
                    Map<String, String> _map = new HashMap<>();
                    if (cfg.startsWith("{")) {
                        //
                        // json
                        //
                        Map<String, Object> tmp2 = (Map<String, Object>) ONode.load(cfg).toData();
                        tmp2.forEach((k, v) -> {
                            _map.put(k, v.toString());
                        });

                    } else {
                        //
                        // properties
                        //
                        Properties prop = Utils.buildProperties(cfg);
                        prop.forEach((k, v) -> {
                            _map.put(k.toString(), v.toString());
                        });
                    }

                    tmp = DbBuilder.getDb(_map);

                    if (tmp != null) {
                        _db_cache.put(cfg, tmp);
                    }
                }
            }
        }

        return tmp;
    }

    /**
     * 空的只读集合
     * */
    private final Map<String, Object> _empMap = Collections.unmodifiableMap(new HashMap<>());
    @Note("空的Map<String,Object>集合")
    public Map<String, Object> empMap() {
        return _empMap;
    }


    private List<Object> _empList = Collections.unmodifiableList(new ArrayList<>());
    @Note("空的List<Object>集合")
    public List<Object> empList() {
        return _empList;
    }

    private Set<Object> _empSet = Collections.unmodifiableSet(new HashSet<>());
    @Note("创建一个Set<Object>集合")
    public Set<Object> empSet() {
        return _empSet;
    }


    /**
     * 新建集合
     * */
    @Note("创建一个Map<String,Object>集合")
    public Map<String, Object> newMap() {
        return new HashMap<>();
    }


    @Note("创建一个List<Object>集合")
    public List<Object> newList() {
        return new ArrayList<>();
    }

    @Note("创建一个List<Object>集合")
    public List<Object> newList(Object[] ary) {
        return Arrays.asList(ary);
    }

    @Note("创建一个Set<Object>集合")
    public Set<Object> newSet() {
        return new HashSet<>();
    }

    @Note("创建一个ByteArrayOutputStream空对象")
    public OutputStream newOutputStream(){
        return new ByteArrayOutputStream();
    }

    @Note("创建一个XFile空对象")
    public UploadedFile newXfile(){
        return new UploadedFile();
    }

    @Note("创建一个URI")
    public URI newUri(String str){
        return URI.create(str);
    }

    @Note("执行 HTTP 请求")
    public HttpUtils http(String url) {
        return new HttpUtils(url);
    }


    @Note("编码html")
    public String htmlEncode(String str) {
        if (str == null) {
            return "";
        } else {
            str = str.replaceAll("<", "&lt;");
            str = str.replaceAll(">", "&gt;");
        }
        return str;
    }


    @Note("编码url")
    public String urlEncode(String str) throws Exception{
        if(str == null){
            return str;
        }

       return URLEncoder.encode(str, "utf-8");
    }

    @Note("解码url")
    public String urlDecode(String str) throws Exception{
        if(str == null){
            return str;
        }

        return URLDecoder.decode(str, "utf-8");
    }



    @Note("获取执行器清单")
    public Set<String> executorList(){
        return ExecutorFactory.list();
    }

    @Note("添加共享对象（key, 以 _ 开头）")
    public boolean sharedAdd(String key, Object obj){
        if(TextUtils.isEmpty(key)){
            return false;
        }

        if(key.startsWith("_")) {
            Solon.global().sharedAdd(key, obj);
            return true;
        }else{
            return false;
        }
    }

    @Note("检查共享对象")
    public boolean sharedHas(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }

        return Solon.global().shared().containsKey(key);
    }





    @Note("生成md5码")
    public String md5(String str) {
        return EncryptUtils.md5(str, "UTF-8");
    }
    @Note("生成md5码")
    public String md5(String str, String charset) {
        return EncryptUtils.md5(str, charset);
    }


    @Note("生成sha1码")
    public String sha1(String str) {
        return EncryptUtils.sha1(str, "UTF-8");//UTF-16LE, utf-8
    }
    @Note("生成sha1码")
    public String sha1(String str, String charset) {
        return EncryptUtils.sha1(str, charset);
    }


    @Note("生成sha256码")
    public String sha256(String str) {
        return EncryptUtils.sha256(str, "UTF-8");//UTF-16LE, utf-8
    }
    @Note("生成sha256码")
    public String sha256(String str, String charset) {
        return EncryptUtils.sha256(str, charset);
    }


    @Note("HMAC 加密")
    public byte[] hmac(String str, String key, String algorithm){
        return EncryptUtils.hmac(str,key,algorithm, null);
    }

    @Note("HMAC 加密")
    public byte[] hmac(String str, String key, String algorithm, String charset) {
        return EncryptUtils.hmac(str, key, algorithm, charset);
    }

    @Note("byte[]转为16位字符串编码")
    public String toX16(byte[] bytes) {
        return EncryptUtils.toX16(bytes);
    }

    @Note("byte[]转为64位字符串编码")
    public String toX64(byte[] bytes) {
        return EncryptUtils.toX64(bytes);
    }

    @Note("AES 加密")
    public String aesEncrypt(String str, String password){
        return EncryptUtils.aesEncrypt(str, password);
    }
    @Note("AES 加密")
    public String aesEncrypt(String str, String password, String algorithm, String offset, String charset) {
        return EncryptUtils.aesEncrypt(str, password, algorithm, offset, charset);
    }

    @Note("AES 解密")
    public String aesDecrypt(String str, String password){
        return EncryptUtils.aesDecrypt(str, password);
    }
    @Note("AES 解密")
    public String aesDecrypt(String str, String password, String algorithm, String offset, String charset){
        return EncryptUtils.aesDecrypt(str, password, algorithm, offset, charset);
    }

    /**
     * base64
     */
    @Note("BASE64编码")
    public String base64Encode(String text) {
        return Base64Utils.encode(text);
    }

    @Note("BASE64解码")
    public String base64Decode(String text) {
        return Base64Utils.decode(text);
    }

    /**
     * 生成随机码
     */
    @Note("生成随机码")
    public String codeByRandom(int len) {
        return TextUtils.codeByRandom(len);
    }

    /**
     * 字符码转为图片
     */
    @Note("字符码转为图片")
    public BufferedImage codeToImage(String code) throws Exception {
        return ImageUtils.getValidationImage(code);
    }

    @Note("InputStream转为String")
    public String streamToString(InputStream inStream) throws Exception {
        return IOUtils.toString(inStream, "utf-8");
    }

    @Note("OutStream转为InputStream")
    public InputStream streamOutToIn(OutputStream outStream) throws Exception
    {
        return IOUtils.outToIn(outStream);
    }

    @Note("String转为InputStream")
    public InputStream stringToStream(String str) throws Exception{
        return new ByteArrayInputStream(str.getBytes("UTF-8"));
    }

    @Note("Object转为ONode")
    public ONode oNode(Object obj) throws Exception {
        if(obj == null){
            return new ONode();
        }else {
            if(obj instanceof String){
                String tmp = ((String) obj).trim();

                if(tmp.length()==0){
                    return new ONode();
                }

                if(tmp.startsWith("{")){
                    return ONode.load(tmp);
                }
            }

            return ONode.load(obj);
        }
    }

    @Note("生成分页数据模型")
    public PagingModel paging(Context ctx, int pageSize) {
        return new PagingModel(ctx, pageSize, false);
    }

    @Note("生成分页数据模型")
    public PagingModel paging(Context ctx, int pageSize, boolean fixedSize) {
        return new PagingModel(ctx, pageSize, fixedSize);
    }

    @Note("格式化活动时间")
    public String liveTime(Date date) {
        return TimeUtils.liveTime(date);
    }

    @Note("格式化活动字符串")
    public String liveString(String str, int len, String salt) {
        if (str == null) {
            return "";
        }

        if (str.length() > len) {
            if (salt == null) {
                return str.substring(0, len);
            } else {
                return str.substring(0, len) + salt;
            }
        } else {
            return str;
        }
    }


    @Note("是否为数字")
    public boolean isNumber(String str) {
        return TextUtils.isNumber(str);
    }



    @Note("日志")
    public boolean log(Map<String,Object> data) throws Exception{
        return LogUtil.log(data);
    }

    @Note("日志")
    public boolean log(String content) throws Exception{
        return LogUtil.log(null,LogLevel.INFO,content);
    }

    private String _localAddr;
    @Note("服务地址")
    public String localAddr(){
        if(_localAddr == null) {
            _localAddr = LocalUtil.getLocalAddress(Solon.global().port());
        }

        return _localAddr;
    }

    @Note("运行时状态")
    public RuntimeStatus runtimeStatus(){
        RuntimeStatus rs = RuntimeUtils.getStatus();
        rs.address = localAddr();

        return rs;
    }

    @Note("设置上下文状态（用于在模板中停止请求）")
    public int statusSet(int status) throws Exception{
        Context.current().status(status);
        throw new RuntimeException(status+" status");
    }

    @Note("将对象转为string")
    public String stringOf(Object obj){
        if(obj == null){
            return null;
        }

        if(obj instanceof Throwable){
            return ExceptionUtils.getString((Throwable)obj);
        }

        return obj.toString();
    }

    /**
     * 配置获取
     */
    @Note("配置获取")
    public String cfgGet(String name) throws Exception {
        return JtBridge.cfgGet(name);
    }

    @Note("配置获取")
    public String cfgGet(String name,String def) throws Exception {
        String tmp = JtBridge.cfgGet(name);
        if(tmp == null){
            return def;
        }else{
            return tmp;
        }
    }

    @Note("配置获取")
    public AConfigM cfg(String name) throws Exception {
        String tmp = JtBridge.cfgGet(name);
        return new AConfigM(tmp);
    }

    @Note("转换为配置对象")
    public AConfigM cfgOf(String value){
        return new AConfigM(value);
    }

    /**
     * 配置设置
     */
    @Note("配置设置")
    public boolean cfgSet(String name, String value) throws Exception {
        return JtBridge.cfgSet(name, value);
    }

    @Note("线程睡眠")
    public void sleep(long millis) throws InterruptedException{
        Thread.sleep(millis);
    }


    @Note("嘿嘿")
    public String heihei(String text, String... mobileS){
        List<String> ary = Arrays.asList(mobileS);
        return HeiheiApi.push(ary, text);
    }


    protected Map<String, Object> _ridS = new ConcurrentHashMap<>();
    @Note("添加参考接口字典")
    public void ridAdd(String key, Object obj) {
        _ridS.putIfAbsent(key, obj);
    }

    protected Map<String, Object> ridGet(){
        return _ridS;
    }

    /**
     *
     ****************************/
    @Note("获取接口开放清单")
    public List<Map<String, Object>> interfaceList() {
        Map<String, Object> tmp = new HashMap<>();

        tmp.putAll(Solon.global().shared());
        tmp.put("XUtil.http(url)", HttpUtils.class);
        tmp.put("XUtil.db(cfg)", DbContext.class);
        tmp.put("XUtil.paging(ctx,pageSize)", PagingModel.class);


        tmp.put("ctx", Context.class);

        tmp.put("XFile", UploadedFile.class);

        tmp.put("new Datetime()", Datetime.class);
        tmp.put("new Timecount()", Timecount.class);
        tmp.put("new Timespan(date)", Timespan.class);

        Map<String, Object> tmp2 = ridGet();
        if(tmp2 != null){
            tmp.putAll(tmp2);
        }

        List<Map<String, Object>> list = MethodUtils.getMethods(tmp);

        JtFun.g.openList(list);

        //排序
        Collections.sort(list, Comparator.comparing(m -> m.get("name").toString().toLowerCase()));

        return list;
    }
}
