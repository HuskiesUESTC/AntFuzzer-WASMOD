package edu.uestc.antfuzzer.framework.util.middleware.fuzzer.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.Handler;
import edu.uestc.antfuzzer.framework.bean.abi.Action;
import edu.uestc.antfuzzer.framework.bean.afl.AFLGeneratorInfoCollection;
import edu.uestc.antfuzzer.framework.bean.result.ActionFuzzingResult;
import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import edu.uestc.antfuzzer.framework.util.BitMapUtil;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.TypeUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;
import edu.uestc.antfuzzer.framework.util.middleware.MiddlewareUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;

@Component
public class GenerateAFLSeed extends BeforeCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Autowired
    private BitMapUtil bitMapUtil;

    @Autowired
    private TypeUtil typeUtil;

    @Autowired
    private MiddlewareUtil middlewareUtil;

    @Override
    protected boolean currentCheck() throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException {
        if (!environmentUtil.isCurrentActionUsingAFLDriver())
            return true;

        LinkedList<AFLGeneratorInfoCollection> aflGeneratorInfoCollectionList = environmentUtil.getAflGeneratorInfoCollectionList();
        if (aflGeneratorInfoCollectionList == null || aflGeneratorInfoCollectionList.size() != 0) {
            aflGeneratorInfoCollectionList = new LinkedList<>();
            environmentUtil.setAflGeneratorInfoCollectionList(aflGeneratorInfoCollectionList);
        }

        Handler fuzzer = environmentUtil.getFuzzer();
        Object object = fuzzer.getObject();

        Method fuzz = fuzzer.getFuzz();
        Parameter[] fuzzParameters = fuzz.getParameters();

        for (Action action : environmentUtil.getActions()) {

            environmentUtil.setAction(action);
            environmentUtil.setLastCoverageChangeTime(0);
            environmentUtil.setActionFuzzingResult(new ActionFuzzingResult(environmentUtil.getSmartContract().getName(), action.getName()));

            if (!middlewareUtil.getBeforeActionFuzzingMiddleware().check())
                continue;

            environmentUtil.getAflGeneratorInfoCollectionList().offer(new AFLGeneratorInfoCollection());
            environmentUtil.setCurrentAFLGeneratorInfoCollection(aflGeneratorInfoCollectionList.getLast());

            if (!middlewareUtil.getBeforeFuzzingMiddleware().check())
                continue;

            FuzzingStatus fuzzingStatus = null;
            BitMapUtil.BitMap bitMap = null;
            try {
                // 为 fuzz 函数注入参数
                Object[] params = typeUtil.generateFuzzingParams(action, environmentUtil.getCurrentArgumentGenerator(), fuzzParameters);
                // 执行 fuzz 函数，并获取结果
                fuzzingStatus = (FuzzingStatus) fuzz.invoke(object, params);
                if (fuzzingStatus == null)
                    fuzzingStatus = FuzzingStatus.NEXT;
                bitMap = bitMapUtil.getBitMap();
                // 如果参数生成错误直接返回错误信息
            } catch (Exception e) {
                e.printStackTrace();
            }
            environmentUtil.setFuzzingStatus(fuzzingStatus);
            environmentUtil.setBitmap(bitMap);

            middlewareUtil.getAfterFuzzingMiddleware().check();
            middlewareUtil.getAfterActionFuzzingMiddleware().check();
        }
        return true;
    }
}
