package demo;

import org.junit.jupiter.api.Test;
import org.noear.luffy.cap.extend.poi.eExcel;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author noear 2021/10/9 created
 */
public class DemoTest {
    @Test
    public void test() throws Exception{
        List list = new ArrayList();

        Map<String,Object> map = new LinkedHashMap<>();
        map.put("name","noear");
        map.put("age","12");

        Map<String,Object> map2 = new LinkedHashMap<>();
        map2.put("name","noear2");
        map2.put("age","122");

        list.add(map);
        list.add(map2);

        File file = new File("/Users/noear/Documents/demo/test.xls");
        if(file.exists() == false){
            file.createNewFile();
        }else{
            file.delete();
            file.createNewFile();
        }

        try(FileOutputStream fileOutputStream = new FileOutputStream(file)){
            eExcel.instance.transfer(list, fileOutputStream);

            fileOutputStream.flush();
        }

    }
}
