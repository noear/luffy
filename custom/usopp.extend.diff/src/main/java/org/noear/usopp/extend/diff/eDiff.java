package org.noear.usopp.extend.diff;

import difflib.DiffUtils;

import java.util.List;

public class eDiff {
    public Object diff(List<String> original, List<String> revised) {
        return DiffUtils.diff(original, revised);
    }
}
