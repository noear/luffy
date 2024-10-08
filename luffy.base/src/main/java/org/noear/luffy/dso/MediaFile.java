package org.noear.luffy.dso;

import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.IoUtil;

import java.io.*;

public class MediaFile {
    /**
     * 内容类型（有些地方会动态构建，所以不能只读）
     */
    public String contentType;
    /**
     * 内容大小
     */
    public long contentSize;

    /**
     * 内容流
     */
    public InputStream content;

    /**
     * 文件名（带扩展名，例：demo.jpg）
     */
    public String name;

    /**
     * 扩展名（例：jpg）
     */
    public String extension;


    public MediaFile() {

    }


    /**
     * 将内容流迁移到..
     *
     * @param file 文件
     */
    public void transferTo(File file) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            IoUtil.transferTo(content, stream);
        }
    }

    /**
     * 将内容流迁移到..
     *
     * @param stream 输出流
     */
    public void transferTo(OutputStream stream) throws IOException {
        IoUtil.transferTo(content, stream);
    }

    /**
     * 转为 UploadedFile 类型
     * */
    public UploadedFile toUploadedFile() {
        return new UploadedFile(contentType, contentSize, content, name, extension);
    }
}
