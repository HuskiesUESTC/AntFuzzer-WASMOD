package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.FuzzInfo;
import edu.uestc.antfuzzer.framework.enums.AFLExceptionStatus;
import edu.uestc.antfuzzer.framework.exception.AFLException;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;

@Component
public class RedisUtil {

    @Autowired
    private AFLUtil aflUtil;

    private static final String FUZZ_INFO_KEY = "fuzz_info_%s";

    private static final String CUR_INPUT_FILEPATH_KEY = "cur_input_filepath_%s";
    private static final String CUR_INPUT_FILE_PATH = "./fuzz/interface%s/out_dir/.cur_input";
    private static final String CUR_INPUT_CONTENT_KEY = "cur_input_content_%s";

    /**
     * 将 fuzzing 的结果放入 redis中
     */
    public void pushFuzzInfo(FuzzInfo fuzzInfo) {
        byte[] mem = fuzzInfo.getMem();
        byte status = fuzzInfo.getStatus().getValue();
        int id = fuzzInfo.getId();
        int len = mem.length + 1;
        byte[] result = new byte[len];
        result[0] = status;
        String key = String.format(FUZZ_INFO_KEY, id);
        Jedis client = new Jedis();
        client.lpush(key.getBytes(StandardCharsets.UTF_8), result);
    }

    public void clearFuzzInfo(int id) {
        String key = String.format(FUZZ_INFO_KEY, id);
        Jedis client = new Jedis();
        try {
            if (client.exists(key))
                client.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearFuzzInfo() {
        clearFuzzInfo(1);
        clearFuzzInfo(2);
    }

    public void clearFilepath(int id) {
        String key = String.format(CUR_INPUT_FILEPATH_KEY, id);
        try {
            Jedis client = new Jedis();
            if (client.exists(key))
                client.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从 redis 中获取生成的文件路径
     */
    public String getInputFilepath(int id) throws AFLException {
        String key = String.format(CUR_INPUT_FILEPATH_KEY, id);
        try {
            Jedis jedis = new Jedis();
            jedis.brpop(10, key);
        } catch (Exception e) {
            if (!aflUtil.isAFLRunning())
                throw new AFLException(AFLExceptionStatus.PROCESS_EXIT, "afl" + aflUtil.getTo().getId() + "中断");
            e.printStackTrace();
        }
        return String.format(CUR_INPUT_FILE_PATH, id);
    }
}
