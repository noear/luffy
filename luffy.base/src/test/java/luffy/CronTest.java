package luffy;

import org.junit.Test;
import org.noear.luffy.task.cron.CronUtils;

import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author noear 2021/7/2 created
 */
public class CronTest {
    @Test
    public void test() {
        System.out.println(ZoneOffset.of("+08"));
        System.out.println(ZoneOffset.of("+0800"));
        System.out.println(ZoneOffset.ofHours(8));


        System.out.println(ZoneOffset.of("-08"));
        System.out.println(ZoneOffset.of("-0800"));
        System.out.println(ZoneOffset.ofHours(-8));
    }

    @Test
    public void test2() {
        System.out.println(TimeZone.getTimeZone("+08"));
        System.out.println(TimeZone.getTimeZone("+0800"));


        System.out.println(TimeZone.getTimeZone("-08"));
        System.out.println(TimeZone.getTimeZone("-0800"));
    }

    @Test
    public void test3() {
        System.out.println(TimeZone.getTimeZone(ZoneOffset.of("+08")));
        System.out.println(TimeZone.getTimeZone(ZoneOffset.of("+0800")));


        System.out.println(TimeZone.getTimeZone(ZoneOffset.of("-08")));
        System.out.println(TimeZone.getTimeZone(ZoneOffset.of("-0800")));
    }

    @Test
    public void test4() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.ofHours(0)));
        println(new Date());
        println(CronUtils.getNextTime("0 0 1 * * ? +00", new Date()));
        println(CronUtils.getNextTime("0 0 1 * * ? +08", new Date()));
    }

    @Test
    public void test5() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.ofHours(0)));
        System.out.println(new Date());
        System.out.println(CronUtils.isSatisfiedBy("0 1 * * * ? +00", new Date()));
        System.out.println(CronUtils.isSatisfiedBy("0 0 1 * * ? +00", new Date()));
        System.out.println(CronUtils.isSatisfiedBy("0 0 1 * * ? +08", new Date()));
    }

    private void println(Date date) {
        System.out.println(date.getTime() + " :: " + date);
    }
}
