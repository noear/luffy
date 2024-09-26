package org.noear.luffy.dso;

import org.noear.solon.annotation.Note;
import org.noear.solon.core.handle.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页条数据模型
 * */
public class PagingModel implements Serializable {

    public PagingModel(Context ctx, long pageSize, boolean fixedSize) {
        _page = ctx.paramAsLong("_page", 1);
        _pageSize_def = pageSize;

        if (_page < 1) {
            _page = 1;
        }

        if(fixedSize){
            _pageSize = pageSize;
        }else {
            _pageSize = ctx.paramAsInt("_pageSize", 0);

            if (_pageSize < 1) {
                _pageSize = Integer.parseInt(ctx.cookieOrDefault("_pageSize", "0"));
            }

            if (_pageSize > 99) {
                _pageSize = 99;
            }

            if (_pageSize < 1) {
                _pageSize = pageSize;
            } else {
                ctx.cookieSet("_pageSize", _pageSize + "", "", ctx.path(), Integer.MAX_VALUE);
            }
        }

    }

    private final long _page_begin = 1;

    private long _page; //从1开始
    private long _pageSize;
    private long _pageSize_def;
    private long _total;

    @Note("设置总记录数")
    public PagingModel totalSet(int total){
        _total = total;
        return this;
    }

    @Note("起始记录数")
    public long start(){
        return (int)(_pageSize * (_page - _page_begin));
    }

    @Note("分页长度")
    public long pageSize(){
        return _pageSize;
    }

    @Note("默认分页长度")
    public long pageSizeDef(){
        return _pageSize_def;
    }

    @Note("当前页码")
    public long page(){
        return _page;
    }

    @Note("页码区间的起始页")
    public List<Long> pageRange(int size) {
        List<Long> list = new ArrayList<>();

        boolean isleft = (pages() - page()) > page();//当前页，是否区于左侧
        long _bef = 0;
        if (isleft) {
            //如果在左侧
            _bef = page() - ((size - 1) / 2);
        } else {
            //如果在右侧，先算出最右侧的页码
            long _aft = page() + ((size - 1) / 2);

            if (_aft > pages()) {
                _aft = pages();
            }

            _bef = _aft - size; //最右侧-长度=最左侧
        }

        if (_bef < 1) {
            _bef = 1;
        }

        for(long i=_bef, len=_bef+size; i<len && i<=pages(); i++){
            list.add(i);
        }

        if(size>7 && pages() > size){
            if(isleft){
                if((pages() - list.get(list.size()-1)) > 3){
                    list.remove(list.size()-1);
                    list.remove(list.size()-1);
                    list.remove(list.size()-1);

                    list.add(-1l);
                    list.add(pages()-1);
                    list.add(pages());
                }
            }else{
                if((list.get(0) - 1) > 3){
                    list.remove(0);
                    list.remove(0);
                    list.remove(0);

                    list.add(0,-1l);//-1代理..
                    list.add(0,2l);
                    list.add(0,1l);
                }
            }
        }

        return list;
    }

    @Note("上一页码")
    public long pagePrev(){
        if(_page>1) {
            return _page - 1;
        }else{
            return _page;
        }
    }

    @Note("下一页码")
    public long pageNext(){
        return _page + 1;
    }

    @Note("是否有上一页")
    public boolean hasPrev() {
        return page()>1;
    }

    @Note("是否有下一页")
    public boolean hasNext() {
        return pages() >= pageNext();
    }

    @Note("总页数")
    public long pages() {
        if (_pages < 0) {
            if (_total % _pageSize > 0) {
                _pages = (_total / _pageSize) + 1;
            } else {
                _pages = (_total / _pageSize);
            }
        }

        return _pages;
    }
    private long _pages =-1;

    @Note("总记录数")
    public long total(){
        return _total;
    }
}
