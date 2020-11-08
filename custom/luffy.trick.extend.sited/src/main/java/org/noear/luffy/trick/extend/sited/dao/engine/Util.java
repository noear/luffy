package org.noear.luffy.trick.extend.sited.dao.engine;


import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Created by yuety on 15/8/21.
 */
class Util {

    protected static final String NEXT_CALL = "CALL::";
    protected static final String defUA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.10240";

    protected static __ICache cache = new __FileCache();


    protected static Element getElement(Element n, String tag) {
        NodeList temp = n.getElementsByTagName(tag);
        if (temp.getLength() > 0)
            return (Element) (temp.item(0));
        else
            return null;
    }

    protected static Element getXmlroot(String xml) throws Exception {
        StringReader sr = new StringReader(xml);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dombuild = factory.newDocumentBuilder();

        return dombuild.parse(new InputSource(sr)).getDocumentElement();
    }

    //
    //----------------------------
    //

    protected static String urlEncode(String str, SdNode config) {
        try {
            return URLEncoder.encode(str, config.encode());
        } catch (Exception ex) {
            return "";
        }
    }

    protected synchronized static void http(SdSource source, boolean isUpdate, HttpMessage msg) {


    }

    /*生成MD5值*/
    public static String md5(String code) {

        String s = null;

        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            byte[] code_byts = code.getBytes("UTF-8");

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(code_byts);
            byte tmp[] = md.digest();          // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char str[] = new char[16 * 2];   // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            int k = 0;                                // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) {          // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i];                 // 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];  // 取字节中高 4 位的数字转换,
                // >>> 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf];            // 取字节中低 4 位的数字转换
            }
            s = new String(str);                                 // 换后的结果转换为字符串

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    //-------------
    //


    public static String toJson(Map<String,String> data){
        StringBuilder sb  = new StringBuilder();

        if(data!=null) {
            sb.append("{");

            for (String k : data.keySet()) {
                String v = data.get(k);
                sb.append("\"").append(k).append("\"").append(":");

                if(v.startsWith("{")){
                    sb.append(v);
                }else {
                    _WriteValue(sb, v);
                }

                sb.append(",");
            }

            if (sb.length() > 4) {
                sb.deleteCharAt(sb.length() - 1);
            }

            sb.append("}");
        }else{
            sb.append("{}");
        }

        return sb.toString();
    }

    private static final void _WriteValue(StringBuilder _Writer, String val)
    {

        if (val == null)
        {
            _Writer.append("null");
        }
        else
        {
            _Writer.append('\"');

            int n = val.length();
            char c;
            for (int i = 0; i < n; i++)
            {
                c = val.charAt(i);
                switch (c)
                {
                    case '\\':
                        _Writer.append("\\\\"); //20110809
                        break;

                    case '\"':
                        _Writer.append("\\\"");
                        break;

                    case '\n':
                        _Writer.append("\\n");
                        break;

                    case '\r':
                        _Writer.append("\\r");
                        break;

                    case '\t':
                        _Writer.append("\\t");
                        break;

                    case '\f':
                        _Writer.append("\\f");
                        break;

                    case '\b':
                        _Writer.append("\\b");
                        break;

                    default:
                        _Writer.append(c);
                        break;
                }
            }

            _Writer.append('\"');
        }
    }
}
