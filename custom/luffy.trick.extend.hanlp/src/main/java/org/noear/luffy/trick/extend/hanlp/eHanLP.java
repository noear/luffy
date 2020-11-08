package org.noear.luffy.trick.extend.hanlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import com.hankcs.hanlp.mining.word.WordInfo;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import java.util.List;

public class eHanLP {

    public String convertToSimplifiedChinese(String traditionalChineseString) {
        return HanLP.convertToSimplifiedChinese(traditionalChineseString);
    }

    public String convertToTraditionalChinese(String simplifiedChineseString) {
        return HanLP.convertToTraditionalChinese(simplifiedChineseString);
    }

    public String s2t(String s) {
        return HanLP.s2t(s);
    }

    public String t2s(String t) {
        return HanLP.t2s(t);
    }

    public String s2tw(String s) {
        return HanLP.s2tw(s);
    }

    public String tw2s(String tw) {
        return HanLP.tw2s(tw);
    }

    public String s2hk(String s) {
        return HanLP.s2hk(s);
    }

    public String hk2s(String hk) {
        return HanLP.hk2s(hk);
    }

    public String t2tw(String t) {
        return HanLP.t2tw(t);
    }

    public String tw2t(String tw) {
        return HanLP.tw2t(tw);
    }

    public String t2hk(String t) {
        return HanLP.t2hk(t);
    }

    public String hk2t(String hk) {
        return HanLP.hk2t(hk);
    }

    public String hk2tw(String hk) {
        return HanLP.hk2tw(hk);
    }

    public String tw2hk(String tw) {
        return HanLP.tw2hk(tw);
    }

    public String convertToPinyinString(String text, String separator, boolean remainNone) {
        return HanLP.convertToPinyinString(text, separator, remainNone);
    }

    public List<Pinyin> convertToPinyinList(String text) {
        return HanLP.convertToPinyinList(text);
    }

    public String convertToPinyinFirstCharString(String text, String separator, boolean remainNone) {
        return HanLP.convertToPinyinFirstCharString(text, separator, remainNone);
    }

    public List<Term> segment(String text) {
        return HanLP.segment(text);
    }

    public Segment newSegment() {
        return HanLP.newSegment();
    }

    public Segment newSegment(String algorithm) {
        return HanLP.newSegment(algorithm);
    }

    public CoNLLSentence parseDependency(String sentence) {
        return HanLP.parseDependency(sentence);
    }

    public List<String> extractPhrase(String text, int size) {
        return HanLP.extractPhrase(text, size);
    }

    public List<WordInfo> extractWords(String text, int size) {
        return HanLP.extractWords(text, size);
    }

    public List<WordInfo> extractWords(String text, int size, boolean newWordsOnly) {
        return HanLP.extractWords(text, size, newWordsOnly);
    }

    public List<String> extractKeyword(String document, int size) {
        return HanLP.extractKeyword(document, size);
    }

    public List<String> extractSummary(String document, int size) {
        return HanLP.extractSummary(document, size);
    }

    public String getSummary(String document, int max_length) {
        return HanLP.getSummary(document, max_length);
    }

    public List<String> extractSummary(String document, int size, String sentence_separator) {
        return HanLP.extractSummary(document, size, sentence_separator);
    }

    public String getSummary(String document, int max_length, String sentence_separator) {
        return HanLP.getSummary(document, max_length, sentence_separator);
    }
}
