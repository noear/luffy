package org.noear.luffy.utils;

import java.util.Random;

public class TextUtils {
    /** 是否为空 */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /** 是否为数字 */
    public static boolean isNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /** 生成随机字符串 */
    public static String codeByRandom(int len) {
        char codeTemplate[] = {
                'a', 'b', 'c', 'd', 'e', 'f',
                'g', 'h', 'j', 'k',
                'm', 'n', 'p', 'q', 'r',
                's', 't', 'u', 'v', 'w', 'x',
                'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'J', 'K', 'L',
                'M', 'N', 'P', 'Q', 'R',
                'S', 'T', 'U', 'V', 'W', 'X',
                'Y', 'Z',
                '2', '3', '4', '5',
                '6', '7', '8', '9'
        };

        int TEMPLATE_SIZE = codeTemplate.length;
        StringBuilder sb = StringUtils.borrowBuilder();
        sb.setLength(0);

        Random random = new Random();
        for (int i = 0; i < len; i++) {
            sb.append(codeTemplate[random.nextInt(TEMPLATE_SIZE) % TEMPLATE_SIZE]);
        }

        return StringUtils.releaseBuilder(sb);
    }

    public static String codeByRandomEx(int len) {
        char codeTemplate[] = {
                'a', 'b', 'c', 'd', 'e', 'f',
                'g', 'h', 'i', 'j', 'k', 'l',
                'm', 'n', 'o', 'p', 'q', 'r',
                's', 't', 'u', 'v', 'w', 'x',
                'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L',
                'M', 'N', 'O', 'P', 'Q', 'R',
                'S', 'T', 'U', 'V', 'W', 'X',
                'Y', 'Z',
                '0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9'
        };

        int TEMPLATE_SIZE = codeTemplate.length;
        StringBuilder sb = StringUtils.borrowBuilder();
        sb.setLength(0);

        Random random = new Random();
        for (int i = 0; i < len; i++) {
            sb.append(codeTemplate[random.nextInt(TEMPLATE_SIZE) % TEMPLATE_SIZE]);
        }

        return StringUtils.releaseBuilder(sb);
    }
}
