package features;

import org.junit.Test;
import org.noear.luffy.task.cron.CronExpression;
import org.noear.luffy.task.cron.CronUtils;
import org.noear.luffy.utils.Datetime;

import java.text.ParseException;
import java.util.Date;

public class CronUtilsTest {
    @Test
    public void test() throws ParseException {
        String cron = "* 0/1 * * * ? *";
        CronExpression expr = CronUtils.get(cron);

        Date current = new Date();
        Date nextValidTimeAfter = expr.getNextValidTimeAfter(current);

        String currentStr = Datetime.format(current, "yyyy-MM-DD HH:mm:ss");
        String nextTimeStr = Datetime.format(nextValidTimeAfter, "yyyy-MM-DD HH:mm:ss");

        System.out.println("current :" + currentStr + " , next : " + nextTimeStr);
    }

    @Test
    public void test1() throws ParseException {
        String cron = "0 0/1 * * * ? *";
        CronExpression expr = CronUtils.get(cron);

        Date current = new Date();
        Date nextValidTimeAfter = expr.getNextValidTimeAfter(current);

        String currentStr = Datetime.format(current, "yyyy-MM-DD HH:mm:ss");
        String nextTimeStr = Datetime.format(nextValidTimeAfter, "yyyy-MM-DD HH:mm:ss");

        System.out.println("current :" + currentStr + " , next : " + nextTimeStr);
    }
}
