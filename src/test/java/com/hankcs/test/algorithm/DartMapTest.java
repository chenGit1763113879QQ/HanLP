package com.hankcs.test.algorithm;

import com.hankcs.hanlp.collection.dartsclone.DartMap;
import com.hankcs.hanlp.collection.trie.DoubleArrayTrie;
import com.hankcs.hanlp.corpus.io.IOUtil;
import junit.framework.TestCase;

import java.util.*;

public class DartMapTest extends TestCase
{
    private static final String DATA_TEST_OUT_BIN = "data/test/out.bin";
    Set<String> validKeySet;
    Set<String> invalidKeySet;
    private DartMap<Integer> dartMap;

    public void setUp() throws Exception
    {
        IOUtil.LineIterator iterator = new IOUtil.LineIterator("data/dictionary/CoreNatureDictionary.ngram.txt");
        validKeySet = new TreeSet<String>();
        while (iterator.hasNext())
        {
            validKeySet.add(iterator.next().split("\\s")[0]);
        }
    }

    public void testGenerateInvalidKeySet() throws Exception
    {
        invalidKeySet = new TreeSet<String>();
        Random random = new Random(System.currentTimeMillis());
        while (invalidKeySet.size() < validKeySet.size())
        {
            int length = random.nextInt(10) + 1;
            StringBuilder key = new StringBuilder(length);
            for (int i = 0; i < length; ++i)
            {
                key.append(random.nextInt(Character.MAX_VALUE));
            }
            if (validKeySet.contains(key.toString())) continue;
            invalidKeySet.add(key.toString());
        }
    }

    public void testBuild() throws Exception
    {
        ArrayList<String> keyList = new ArrayList<String>(validKeySet);
        ArrayList<Integer> valList = new ArrayList<Integer>(keyList.size());
        for (String key : keyList)
        {
            valList.add(key.length());
        }

        dartMap = new DartMap<Integer>(keyList, valList);
    }

    public void testContainsAndNoteContains() throws Exception
    {
        testBuild();
        for (String key : validKeySet)
        {
            assertEquals(key.length(), (int)dartMap.get(key));
        }

        testGenerateInvalidKeySet();
        for (String key : invalidKeySet)
        {
            assertEquals(null, dartMap.get(key));
        }
    }

    public void testCommPrefixSearch() throws Exception
    {
        testBuild();
        System.out.println(dartMap.commonPrefixSearch("????????????"));
    }

    public void testBenchmark() throws Exception
    {
        testBuild();
        long start;
        {

        }
        DoubleArrayTrie<Integer> trie = new DoubleArrayTrie<Integer>();
        TreeMap<String, Integer> map = new TreeMap<String, Integer>();
        for (String key : validKeySet)
        {
            map.put(key, key.length());
        }
        trie.build(map);

        // TreeMap
        start = System.currentTimeMillis();
        for (String key : validKeySet)
        {
            assertEquals(key.length(), (int)map.get(key));
        }
        System.out.printf("TreeMap: %d ms\n", System.currentTimeMillis() - start);
        map = null;
        // DAT
        start = System.currentTimeMillis();
        for (String key : validKeySet)
        {
            assertEquals(key.length(), (int)trie.get(key));
        }
        System.out.printf("DAT: %d ms\n", System.currentTimeMillis() - start);
        trie = null;
        // DAWG
        start = System.currentTimeMillis();
        for (String key : validKeySet)
        {
            assertEquals(key.length(), (int)dartMap.get(key));
        }
        System.out.printf("DAWG: %d ms\n", System.currentTimeMillis() - start);

        /**
         * result:
         * TreeMap: 677 ms
         * DAT: 310 ms
         * DAWG: 858 ms
         *
         * ???????????????????????????????????????????????????
         */
    }
}