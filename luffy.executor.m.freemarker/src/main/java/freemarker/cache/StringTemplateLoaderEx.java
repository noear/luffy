package freemarker.cache;

import freemarker.template.utility.StringUtil;
import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.executor.m.freemarker.FreemarkerJtExecutor;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.utils.StringUtils;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义 StringTemplateLoader；用于实现更强的控制能力
 * */
public class StringTemplateLoaderEx implements TemplateLoader {
    private final Map<String, StringTemplateSource> templates = new HashMap<String, StringTemplateSource>();

    /**
     * Puts a template into the loader. A call to this method is identical to
     * the call to the three-arg {@link #putTemplate(String, String, long)}
     * passing <tt>System.currentTimeMillis()</tt> as the third argument.
     *
     * <p>Note that this method is not thread safe! Don't call it after FreeMarker has started using this template
     * loader.
     *
     * @param name the name of the template.
     * @param templateContent the source code of the template.
     */
    public void putTemplate(String name, String templateContent) {
        putTemplate(name, templateContent, System.currentTimeMillis());
    }

    /**
     * Puts a template into the loader. The name can contain slashes to denote
     * logical directory structure, but must not start with a slash. If the
     * method is called multiple times for the same name and with different
     * last modified time, the configuration's template cache will reload the
     * template according to its own refresh settings (note that if the refresh
     * is disabled in the template cache, the template will not be reloaded).
     * Also, since the cache uses lastModified to trigger reloads, calling the
     * method with different source and identical timestamp won't trigger
     * reloading.
     *
     * <p>Note that this method is not thread safe! Don't call it after FreeMarker has started using this template
     * loader.
     *
     * @param name the name of the template.
     * @param templateContent the source code of the template.
     * @param lastModified the time of last modification of the template in
     * terms of <tt>System.currentTimeMillis()</tt>
     */
    public void putTemplate(String name, String templateContent, long lastModified) {
        templates.put(name, new StringTemplateSource(name, templateContent, lastModified));
    }

    /**
     * Removes the template with the specified name if it was added earlier.
     *
     * <p>Note that this method is not thread safe! Don't call it after FreeMarker has started using this template
     * loader.
     *
     * @param name Exactly the key with which the template was added.
     *
     * @return Whether a template was found with the given key (and hence was removed now)
     *
     * @since 2.3.24
     */
    public boolean removeTemplate(String name) {
        return templates.remove(name) != null;
    }

    public void removeTemplateAll() {
         templates.clear();
    }

    public boolean contains(String name){
        return templates.containsKey(name);
    }

    public void closeTemplateSource(Object templateSource) {
    }

    public Object findTemplateSource(String name) {
        /**
         * name 不会是 / 开头（自动会去掉）
         * */
        if (name.indexOf("/") >= 0) {
            String path = "/" + name;
            String path2 = path; //AFileUtil.path2(path);//不用转为*
            name = path2.replace("/", "__");

            Object tml = templates.get(name);

            if (tml != null) {
                return tml;
            }

            try {
                AFileModel file = ExecutorFactory.fileGet(path2);

                if (FreemarkerJtExecutor.singleton().put(name, file)) {
                    return templates.get(name);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;

        } else {
            return templates.get(name);
        }
    }

    public long getLastModified(Object templateSource) {
        return ((StringTemplateSource) templateSource).lastModified;
    }

    public Reader getReader(Object templateSource, String encoding) {
        return new StringReader(((StringTemplateSource) templateSource).templateContent);
    }

    private static class StringTemplateSource {
        private final String name;
        private final String templateContent;
        private final long lastModified;

        StringTemplateSource(String name, String templateContent, long lastModified) {
            if (name == null) {
                throw new IllegalArgumentException("name == null");
            }
            if (templateContent == null) {
                throw new IllegalArgumentException("source == null");
            }
            if (lastModified < -1L) {
                throw new IllegalArgumentException("lastModified < -1L");
            }
            this.name = name;
            this.templateContent = templateContent;
            this.lastModified = lastModified;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            StringTemplateSource other = (StringTemplateSource) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }


        @Override
        public String toString() {
            return name;
        }

    }

    /**
     * Show class name and some details that are useful in template-not-found errors.
     *
     * @since 2.3.21
     */
    @Override
    public String toString() {
        StringBuilder sb = StringUtils.borrowBuilder();

        sb.append(TemplateLoaderUtils.getClassNameForToString(this));
        sb.append("(Map { ");
        int cnt = 0;
        for (String name : templates.keySet()) {
            cnt++;
            if (cnt != 1) {
                sb.append(", ");
            }
            if (cnt > 10) {
                sb.append("...");
                break;
            }
            sb.append(StringUtil.jQuote(name));
            sb.append("=...");
        }
        if (cnt != 0) {
            sb.append(' ');
        }
        sb.append("})");

        return StringUtils.releaseBuilder(sb);
    }
}
