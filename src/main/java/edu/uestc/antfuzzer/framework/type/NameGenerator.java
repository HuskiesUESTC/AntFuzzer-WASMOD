package edu.uestc.antfuzzer.framework.type;

import com.google.gson.Gson;
import edu.uestc.antfuzzer.framework.type.interfaces.TypeGenerator;
import edu.uestc.antfuzzer.framework.util.EOSUtil;

import java.util.*;

public class NameGenerator implements TypeGenerator {

    private final String activeAccount;
    private final List<EOSUtil.Account> accountPool;
    private final Random random;
    private final Set<String> whiteSet;

    public NameGenerator(Random random, String activeAccount, List<EOSUtil.Account> accountPool) {
        this.random = random;
        this.activeAccount = activeAccount;
        this.accountPool = accountPool;

        whiteSet = new HashSet<>();
        whiteSet.add("from");
        whiteSet.add("author");
        whiteSet.add("account");
    }

    @Override
    public String generate() {
        String result = activeAccount;
        int size = accountPool.size();
        if (size != 0)
            result = accountPool.get(random.nextInt(size)).getName();
        return result;
    }

    /**
     * 参数中的 name 可能与 action push 者的 name 相同，需要从 json 中查找是否有与 whiteSet 中相同名称并且值存在于 account pool 中的参数
     */
    public String generate(String json) {
        Gson gson = new Gson();
        if (whiteSet.size() == 0)
            return generate();
        HashMap<String, Object> data = gson.fromJson(json, HashMap.class);
        for (Map.Entry<String, Object> item : data.entrySet()) {
            String key = item.getKey();
            if (whiteSet.contains(key)) {
                for (EOSUtil.Account account : accountPool) {
                    String value = "";
                    try {
                        value = (String) item.getValue();
                    } catch (ClassCastException ignored) {
                    }
                    if (account.getName().contentEquals(value))
                        return value;
                }
            }
        }
        return generate();
    }
}
