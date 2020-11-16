package org.noear.luffy.dso;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.Solon;;
import org.noear.luffy.Config;
import org.noear.luffy.utils.*;
import org.noear.solon.core.NvMap;
import org.noear.weed.DbContext;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;

/**
 * 初始化工具类（提供引擎初始化支持）
 * */
public class InitUtil {

    public static DbContext db(){
        return DbUtil.db();
    }

    public static void tryInitDb(){
        try{
             do_initDb();
        }catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    private static void do_initDbTable(String sql) throws Exception {
        db().exe(sql);
    }

    private static void do_initDb() throws Exception {
        int num = db().dbTables().size();
        if (num >= 8) {
            return;
        }


        do_initDbTable(" CREATE TABLE IF NOT EXISTS `a_config` (\n" +
                "  `cfg_id` int NOT NULL AUTO_INCREMENT COMMENT '配置ID',\n" +
                "  `tag` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '分组标签',\n" +
                "  `label` varchar(100) NOT NULL DEFAULT '' COMMENT '标记',\n" +
                "  `name` varchar(100) NOT NULL COMMENT '名称',\n" +
                "  `value` varchar(999) NOT NULL DEFAULT '' COMMENT '值',\n" +
                "  `note` varchar(100) NOT NULL DEFAULT '' COMMENT '备注',\n" +
                "  `edit_type` varchar(40) DEFAULT NULL COMMENT '编辑类型',\n" +
                "  `edit_placeholder` varchar(100) DEFAULT NULL COMMENT '编辑提示',\n" +
                "  `is_disabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '禁止使用',\n" +
                "  `is_exclude` tinyint(1) NOT NULL DEFAULT '0' COMMENT '排除导入',\n" +
                "  `is_modified` tinyint(1) NOT NULL DEFAULT '0' COMMENT '可修改的（终端用户）',\n" +
                "  `create_fulltime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  `update_fulltime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',\n" +
                "  PRIMARY KEY (`cfg_id`),\n" +
                "  UNIQUE KEY `IX_a_config__key` (`name`) USING BTREE,\n" +
                "  KEY `IX_a_config__tag` (`tag`) USING BTREE,\n" +
                "  KEY `IX_a_config__label` (`label`) USING BTREE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='jt-配置表' ");

        do_initDbTable(" CREATE TABLE IF NOT EXISTS `a_file` (\n" +
                "  `file_id` int NOT NULL AUTO_INCREMENT COMMENT '文件ID',\n" +
                "  `tag` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '分组村签',\n" +
                "  `label` varchar(100) NOT NULL DEFAULT '' COMMENT '标记',\n" +
                "  `path` varchar(100) NOT NULL COMMENT '文件路径',\n" +
                "  `rank` int NOT NULL DEFAULT '0' COMMENT '排列（小的排前）',\n" +
                "  `is_staticize` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否静态',\n" +
                "  `is_editable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可编辑',\n" +
                "  `is_disabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否禁用',\n" +
                "  `is_exclude` tinyint(1) NOT NULL DEFAULT '0' COMMENT '排除导入',\n" +
                "  `link_to` varchar(100) DEFAULT NULL COMMENT '连接到',\n" +
                "  `edit_mode` varchar(40) NOT NULL DEFAULT '' COMMENT '编辑模式',\n" +
                "  `content_type` varchar(60) NOT NULL DEFAULT '' COMMENT '内容类型',\n" +
                "  `content` longtext COMMENT '内容',\n" +
                "  `note` varchar(99) DEFAULT '' COMMENT '备注',\n" +
                "  `plan_state` int NOT NULL DEFAULT '0' COMMENT '计划状态',\n" +
                "  `plan_begin_time` datetime DEFAULT NULL COMMENT '计划开始执行时间',\n" +
                "  `plan_last_time` datetime DEFAULT NULL COMMENT '计划最后执行时间',\n" +
                "  `plan_last_timespan` bigint NOT NULL DEFAULT '0' COMMENT '计划最后执行时间长度',\n" +
                "  `plan_interval` varchar(200) NOT NULL DEFAULT '' COMMENT '计划执行间隔',\n" +
                "  `plan_max` int NOT NULL DEFAULT '0' COMMENT '计划执行最多次数',\n" +
                "  `plan_count` int NOT NULL DEFAULT '0' COMMENT '计划执行累计次数',\n" +
                "  `create_fulltime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  `update_fulltime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                "  PRIMARY KEY (`file_id`),\n" +
                "  UNIQUE KEY `IX_a_file__key` (`path`) USING BTREE,\n" +
                "  KEY `IX_a_file__tag` (`tag`) USING BTREE,\n" +
                "  KEY `IX_a_file__label` (`label`) USING BTREE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='jt-文件表' ");

        do_initDbTable(" CREATE TABLE IF NOT EXISTS `a_image` (\n" +
                "  `img_id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID',\n" +
                "  `tag` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '分组标签',\n" +
                "  `label` varchar(100) NOT NULL DEFAULT '' COMMENT '标签',\n" +
                "  `path` varchar(100) NOT NULL COMMENT '文件路径',\n" +
                "  `content_type` varchar(100) NOT NULL COMMENT '内容类型',\n" +
                "  `data` longtext COMMENT '数据',\n" +
                "  `data_size` bigint NOT NULL DEFAULT '0' COMMENT '数据长度',\n" +
                "  `data_md5` varchar(40) DEFAULT NULL COMMENT '数据MD5值',\n" +
                "  `note` varchar(100) DEFAULT NULL COMMENT '备注',\n" +
                "  `create_fulltime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  `update_fulltime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',\n" +
                "  PRIMARY KEY (`img_id`),\n" +
                "  UNIQUE KEY `IX_a_image__key` (`path`) USING BTREE,\n" +
                "  KEY `IX_a_image__tag` (`tag`) USING BTREE,\n" +
                "  KEY `IX_a_image__label` (`label`) USING BTREE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='jt-图片表' ");

        do_initDbTable(" CREATE TABLE IF NOT EXISTS `a_log` (\n" +
                "  `log_id` bigint NOT NULL AUTO_INCREMENT,\n" +
                "  `level` int NOT NULL DEFAULT '0' COMMENT '等级',\n" +
                "  `tag` varchar(100) NOT NULL DEFAULT '' COMMENT '标签',\n" +
                "  `tag1` varchar(100) NOT NULL DEFAULT '' COMMENT '标签1',\n" +
                "  `tag2` varchar(100) NOT NULL DEFAULT '' COMMENT '标签2',\n" +
                "  `tag3` varchar(100) NOT NULL DEFAULT '' COMMENT '标签3',\n" +
                "  `tag4` varchar(100) NOT NULL DEFAULT '' COMMENT '标签4',\n" +
                "  `summary` varchar(1000) NOT NULL DEFAULT '' COMMENT '摘要',\n" +
                "  `content` longtext COMMENT '内容',\n" +
                "  `log_date` int NOT NULL DEFAULT '0' COMMENT '记录日期',\n" +
                "  `log_fulltime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录完整时间',\n" +
                "  `from` varchar(100) DEFAULT NULL COMMENT '来源',\n" +
                "  PRIMARY KEY (`log_id`),\n" +
                "  KEY `IX_a_log__date` (`log_date`) USING BTREE,\n" +
                "  KEY `IX_a_log__tag` (`tag`) USING BTREE,\n" +
                "  KEY `IX_a_log__tag1` (`tag1`) USING BTREE,\n" +
                "  KEY `IX_a_log__tag2` (`tag2`) USING BTREE,\n" +
                "  KEY `IX_a_log__tag3` (`tag3`) USING BTREE,\n" +
                "  KEY `IX_a_log__tag4` (`tag4`) USING BTREE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='jt-日志表' ");


        do_initDbTable(" CREATE TABLE IF NOT EXISTS `a_menu` (\n" +
                "  `menu_id` int NOT NULL AUTO_INCREMENT COMMENT '菜单ID',\n" +
                "  `pid` int NOT NULL DEFAULT '0' COMMENT '父级ID',\n" +
                "  `tag` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '分组标签',\n" +
                "  `label` varchar(100) DEFAULT '' COMMENT '分类：主菜单、顶部菜单、底部菜单等',\n" +
                "  `txt` varchar(100) DEFAULT NULL COMMENT '文本内容',\n" +
                "  `url` varchar(512) DEFAULT NULL COMMENT '链接的url',\n" +
                "  `target` varchar(32) DEFAULT NULL COMMENT '打开的方式',\n" +
                "  `level` int NOT NULL DEFAULT '0' COMMENT '级别',\n" +
                "  `icon` varchar(64) DEFAULT NULL COMMENT '菜单的icon',\n" +
                "  `flag` varchar(32) DEFAULT NULL COMMENT '菜单标识',\n" +
                "  `order_number` int DEFAULT '0' COMMENT '排序字段',\n" +
                "  `rel_table` varchar(40) DEFAULT NULL COMMENT '该菜单是否和其他表关联',\n" +
                "  `rel_id` bigint unsigned DEFAULT NULL COMMENT '关联的具体数据id',\n" +
                "  `is_disabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否禁用',\n" +
                "  `is_exclude` tinyint(1) NOT NULL DEFAULT '0' COMMENT '排除导入',\n" +
                "  `create_fulltime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  `update_fulltime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',\n" +
                "  PRIMARY KEY (`menu_id`),\n" +
                "  KEY `IX_a_menu__order_number` (`order_number`) USING BTREE,\n" +
                "  KEY `IX_a_menu__tag` (`tag`) USING BTREE,\n" +
                "  KEY `IX_a_menu__label` (`label`) USING BTREE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='jt-菜单表' ");

        do_initDbTable(" CREATE TABLE IF NOT EXISTS `a_message` (\n" +
                "  `msg_id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',\n" +
                "  `topic` varchar(100) NOT NULL DEFAULT '' COMMENT '主题',\n" +
                "  `topic_source` varchar(100) DEFAULT NULL COMMENT '原始主题',\n" +
                "  `content` varchar(4000) NOT NULL COMMENT '消息内容',\n" +
                "  `state` int NOT NULL DEFAULT '0' COMMENT '状态（-2无派发对象 ; -1:忽略；0:未处理；1处理中；2已完成；3派发超次数）',\n" +
                "  `dist_count` int NOT NULL DEFAULT '0' COMMENT '派发累记次数',\n" +
                "  `dist_ntime` bigint NOT NULL DEFAULT '0' COMMENT '下次派发时间',\n" +
                "  `log_date` int NOT NULL DEFAULT '0' COMMENT '记录日期（yyyyMMdd）',\n" +
                "  `log_fulltime` datetime NOT NULL COMMENT '记录时间',\n" +
                "  PRIMARY KEY (`msg_id`),\n" +
                "  KEY `IX_a_message__topic` (`topic`) USING BTREE,\n" +
                "  KEY `IX_a_message__state` (`state`) USING BTREE,\n" +
                "  KEY `IX_a_message__dist_ntime` (`dist_ntime`) USING BTREE,\n" +
                "  KEY `IX_a_message__dist_count` (`dist_count`) USING BTREE,\n" +
                "  KEY `IX_a_message__log_date` (`log_date`) USING BTREE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='jt-消息表' ");

        do_initDbTable(" CREATE TABLE IF NOT EXISTS `a_message_distribution` (\n" +
                "  `dist_id` bigint NOT NULL AUTO_INCREMENT COMMENT '分发ID',\n" +
                "  `msg_id` bigint NOT NULL COMMENT '待分发的消息ID',\n" +
                "  `file_id` int NOT NULL DEFAULT '0',\n" +
                "  `receive_url` varchar(200) NOT NULL DEFAULT '',\n" +
                "  `receive_way` int NOT NULL DEFAULT '0' COMMENT '接收方式（0HTTP异步等待；1HTTP同步等待；2HTTP异步不等待）',\n" +
                "  `duration` int NOT NULL DEFAULT '0' COMMENT '消耗时长（s）',\n" +
                "  `state` int NOT NULL DEFAULT '0' COMMENT '分发状态（-1忽略；0开始；1失败；2成功；）',\n" +
                "  `log_date` int NOT NULL DEFAULT '0' COMMENT '分发日期（yyyyMMdd）',\n" +
                "  `log_fulltime` datetime NOT NULL COMMENT '分发时间',\n" +
                "  PRIMARY KEY (`dist_id`),\n" +
                "  UNIQUE KEY `IX_a_message_distribution__key` (`msg_id`,`file_id`) USING BTREE,\n" +
                "  KEY `IX_a_message_distribution__date` (`log_date`) USING BTREE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='jt-消息派发表' ");

        do_initDbTable(" CREATE TABLE IF NOT EXISTS `a_plugin` (\n" +
                "  `plugin_id` int NOT NULL AUTO_INCREMENT COMMENT '插件ID',\n" +
                "  `plugin_tag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '插件标签',\n" +
                "  `tag` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',\n" +
                "  `label` varchar(100) NOT NULL DEFAULT '' COMMENT '标记',\n" +
                "  `category` varchar(40) NOT NULL DEFAULT '' COMMENT '分类（预留）',\n" +
                "  `name` varchar(40) NOT NULL COMMENT '名称',\n" +
                "  `author` varchar(40) NOT NULL COMMENT '作者',\n" +
                "  `contacts` varchar(99) DEFAULT NULL COMMENT '联系方式',\n" +
                "  `ver_code` int NOT NULL DEFAULT '0' COMMENT '版本代号',\n" +
                "  `ver_name` varchar(40) NOT NULL COMMENT '版本名称',\n" +
                "  `description` varchar(255) DEFAULT NULL COMMENT '描述',\n" +
                "  `thumbnail` varchar(255) DEFAULT NULL COMMENT '缩略图',\n" +
                "  `url` varchar(255) NOT NULL DEFAULT '' COMMENT '地址',\n" +
                "  `is_installed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已安装（相对于自己）',\n" +
                "  `is_approved` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否审核通过',\n" +
                "  `num_downloads` int NOT NULL DEFAULT '0' COMMENT '下载量',\n" +
                "  `create_fulltime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  `update_fulltime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',\n" +
                "  PRIMARY KEY (`plugin_id`),\n" +
                "  UNIQUE KEY `IX_a_plugin__key` (`plugin_tag`) USING BTREE,\n" +
                "  KEY `IX_a_plugin__tag` (`tag`) USING BTREE,\n" +
                "  KEY `IX_a_plugin__category` (`category`) USING BTREE,\n" +
                "  KEY `IX_a_plugin__label` (`label`) USING BTREE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='jt-插件表' ");


        System.out.println("Complete table structure");
    }

    public static void tryInitNode(SolonApp app){
        String node = app.cfg().get(Config.code_node);
        if (TextUtils.isEmpty(node)) {
            node = app.cfg().argx().get("node");
        }

        if(TextUtils.isEmpty(node)==false){
            app.cfg().argx().put("node", node);
            app.cfg().put(Config.code_node, node);

            try {
                String addr = JtUtil.g.localAddr();

                DbApi.cfgSetNote(node, addr, "cluster.node");
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public static void tryInitCore(SolonApp app){
        try{
             do_initCore(app);
        }catch (Throwable ex){
            ex.printStackTrace();
        }
    }
    private static void do_initCore(SolonApp app) throws Exception {

        if (PluginUtil.install("_core.noear")) {
            //
            // 第一次安装时，做一些初始化
            //
            db().table("a_config")
                    .set("value", "Iv1H81dI2ZNzDS2n")
                    .where("name=?", "_frm_admin_pwd")
                    .update();

            db().table("a_config")
                    .set("value", "0")
                    .where("name=?", "_frm_enable_dev")
                    .update();

            db().table("a_file")
                    .set("link_to", "")
                    .where("path='/'")
                    .update();
        }

        System.out.println("Complete _core loading");
    }

    public static String tryInitExtend(NvMap xarg) {
        String extend = xarg.get("extend");
        if (extend == null) {
            extend = do_buildRoot();
        }

        return extend;
    }

    private static String do_buildRoot() {
        //String fileName = "setup.htm";

        URL temp = Utils.getResource("");

        if (temp == null) {
            return null;
        } else {
            String uri = temp.toString();
            if (uri.startsWith("file:/")) {
                int idx = uri.lastIndexOf("/target/");
                uri = uri.substring(5, idx + 8);
            } else {
                int idx = uri.indexOf("jar!/");
                idx = uri.lastIndexOf("/", idx) + 1;

                uri = uri.substring(9, idx);
            }

            uri = uri + Config.code_ext+ "/";
            File dir = new File(uri);
            if (dir.exists() == false) {
                dir.mkdir();
            }

            return uri;
        }
    }

    public static void trySaveConfig(String extend,NvMap map) throws Exception {
        File file = new File(extend + "_db.properties");
        file.delete();
        file.createNewFile();

        StringBuilder sb = StringUtils.borrowBuilder();
        map.forEach((k, v) -> {
            if("center".equals(k) || "node".equals(k)){
                sb.append(Config.code).append(".");
            }else{
                sb.append(Config.code_db).append(".");
            }
            sb.append(k).append("=").append(v).append("\r\n");
        });

        FileWriter fw = new FileWriter(file);
        fw.write(StringUtils.releaseBuilder(sb));
        fw.flush();
        fw.close();
    }
}
