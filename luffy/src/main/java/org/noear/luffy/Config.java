package org.noear.luffy;

import org.noear.luffy.dso.DbApi;

public class Config {
    public static final String code="luffy";
    public static final String code_db="luffy.db";
    public static final String code_center="luffy.center";
    public static final String code_node="luffy.node";

    public static final String code_ext="luffy_ext";

    public static final String frm_root_img = DbApi.cfgGet("_frm_root_img","/img/");


    public static final String filter_file="filter.file";
    public static final String filter_path="filter.path";

}
