package features;

import org.junit.Test;
import org.noear.luffy.utils.ConfigUtils;
import org.noear.snack.ONode;

import java.util.Properties;

public class ConfigUtilsTest {
    @Test
    public void test10(){
        String prop = "user.user_id=1\nuser.name=noear";
        ONode node = ConfigUtils.getNode(prop);

        System.out.println(node.toJson());

        assert node.get("user").get("user_id").getInt() == 1;
    }

    @Test
    public void test11(){
        String prop = "user_id=1\nname=noear";
        ONode node = ConfigUtils.getNode(prop);

        System.out.println(node.toJson());

        assert node.get("user_id").getInt() == 1;
    }

    @Test
    public void test12(){
        String prop = "name[0]=1\nname[1]=2";
        ONode node = ConfigUtils.getNode(prop);

        System.out.println(node.toJson());

        assert node.get("name").get(0).getInt() == 1;
    }

    @Test
    public void test13(){
        String prop = "name[1]=2\nname[0]=1";
        ONode node = ConfigUtils.getNode(prop);

        System.out.println(node.toJson());

        assert node.get("name").get(0).getInt() == 1;
    }

    @Test
    public void test1x(){
        String prop = "app.id=speech\n" +
                "knowledge.init.knowledgeTitles[0].kdTitle=听不清\n" +
                "knowledge.init.knowledgeTitles[0].keyWords=[你说什么，没听清，听不清楚，再说一遍]\n" +
                "knowledge.init.knowledgeTitles[0].question=[没听懂，听不清楚]\n" +
                "knowledge.init.knowledgeTitles[1].kdInfos[0]=你好\n" +
                "knowledge.init.knowledgeTitles[1].kdInfos[1]=hello\n" +
                "knowledge.init.knowledgeTitles[1].kdInfos[2]=hi\n" +
                "knowledge.init.knowledgeTitles[1].kdTitle=无应答\n" +
                "server.port=9001\n";
        ONode node = ConfigUtils.getNode(prop);

        System.out.println(node.toJson());

        assert node.select("app.id").getString().equals("speech");
        assert node.select("knowledge.init.knowledgeTitles[1].kdInfos").count()==3;
    }

    @Test
    public void test1x2(){
        String prop = "server.port=9001\n" +
                "app.id=speech\n" +
                "knowledge.init.knowledgeTitles[0].kdTitle=听不清\n" +
                "knowledge.init.knowledgeTitles[0].keyWords=[你说什么，没听清，听不清楚，再说一遍]\n" +
                "knowledge.init.knowledgeTitles[0].question=[没听懂，听不清楚]\n" +
                "knowledge.init.knowledgeTitles[1].kdTitle=无应答\n" +
                "knowledge.init.knowledgeTitles[1].kdInfos[0]=你好\n" +
                "knowledge.init.knowledgeTitles[1].kdInfos[1]=hello\n" +
                "knowledge.init.knowledgeTitles[1].kdInfos[2]=hi";
        Properties node = ConfigUtils.getProp(prop);

        System.out.println(node);

        assert node.getProperty("app.id").equals("speech");
        assert node.getProperty("knowledge.init.knowledgeTitles[1].kdInfos[0]").equals("你好");
    }

    @Test
    public void test2x(){
        String prop = "app:\n" +
                "  id: speech\n" +
                "knowledge:\n" +
                "  init:\n" +
                "    knowledgeTitles:\n" +
                "    - kdTitle: 听不清\n" +
                "      keyWords: '[你说什么，没听清，听不清楚，再说一遍]'\n" +
                "      question: '[没听懂，听不清楚]'\n" +
                "    - kdInfos:\n" +
                "      - 你好\n" +
                "      - hello\n" +
                "      - hi\n" +
                "      kdTitle: 无应答\n" +
                "server:\n" +
                "  port: 9001\n";
        ONode node = ConfigUtils.getNode(prop);

        System.out.println(node.toJson());

        assert node.select("app.id").getString().equals("speech");
        assert node.select("knowledge.init.knowledgeTitles[1].kdInfos").count()==3;
    }
}
