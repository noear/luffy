package org.noear.luffy.cap.extend.sited.dao.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by yuety on 2016/11/28.
 */

 class DataContext {
    private Map<SdNode,DataBlock> _data = new HashMap();


    public void add(SdNode node,Integer tag,String text){
        if(_data.containsKey(node)){
            _data.get(node).put(tag,text);
        }else{
            DataBlock dt = new DataBlock();
            dt.put(tag,text);
            _data.put(node,dt);
        }
    }

    public Set<SdNode> nodes(){
        return _data.keySet();
    }

    public DataBlock get(SdNode node){
        return _data.get(node);
    }

}


