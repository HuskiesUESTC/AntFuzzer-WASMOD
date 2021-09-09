package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.Handler;
import edu.uestc.antfuzzer.framework.bean.SmartContract;
import edu.uestc.antfuzzer.framework.bean.abi.Action;
import edu.uestc.antfuzzer.framework.bean.result.ActionFuzzingResult;
import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import edu.uestc.antfuzzer.framework.exception.AFLException;
import edu.uestc.antfuzzer.framework.util.middleware.MiddlewareUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Set;

@Component
public class FuzzUtil {

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private JsonUtil jsonUtil;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private MiddlewareUtil middlewareUtil;

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Autowired
    private SmartContractUtil smartContractUtil;

    @Autowired
    private TypeUtil typeUtil;

    @Autowired
    private BitMapUtil bitMapUtil;

    @Autowired
    private AFLUtil aflUtil;

    @Autowired
    private LogUtil logUtil;

    public void fuzzing() throws Throwable {
        // 扫描合法的智能合约文件夹
        String[] smartContractNames = fileUtil.getValidSmartContractDirs();

        middlewareUtil.getBeforeEOSFuzzingMiddleware().check();

        /*
         * 检测所有的合约
         */
        int count = 0;
        for (String smartContractName : smartContractNames) {

            environmentUtil.setNumber(++count);
            SmartContract smartContract = smartContractUtil.loadSmartContract(smartContractName);
            environmentUtil.setSmartContract(smartContract); // 加载合约
            environmentUtil.setLastCoverage(0);

            if (!middlewareUtil.getBeforeSmartContractFuzzingMiddleware().check())
                continue;
            /*
             * 检测所有的漏洞类型
             */

            Set<Handler> fuzzers = environmentUtil.getFuzzers();
            for (Handler fuzzer : fuzzers) {
                System.out.println(fuzzer.getFuzzerInfo().getVulnerability());
                environmentUtil.setFuzzer(fuzzer); // 加载fuzzer

                if (!middlewareUtil.getBeforeFuzzerFuzzingMiddleware().check())
                    continue;

                List<Action> actions = environmentUtil.getActions();

                Method fuzz = fuzzer.getFuzz();
                Parameter[] fuzzParameters = fuzz.getParameters();

                middlewareUtil.getGenerateAFLSeedMiddleware().check(); // 如果使用了AFL生成参数，则生成种子文件
                /*
                 * 检测每个合约的每个 action
                 */
                for (Action action : actions) {

                    environmentUtil.setAction(action);
                    environmentUtil.setLastCoverageChangeTime(0);

                    if (!middlewareUtil.getBeforeActionFuzzingMiddleware().check())
                        continue;

                    Object object = fuzzer.getObject();
                    /*
                     * 开始每次fuzzing
                     */
                    FuzzingIteration:
                    for (int i = 0; i < environmentUtil.getCurrentActionTotalFuzzingCount(); i++) {

                        if (!middlewareUtil.getBeforeFuzzingMiddleware().check())
                            continue;
                        System.out.println(count);
                        System.out.println(smartContractName);
                        System.out.println(fuzzer.getFuzzerInfo().getVulnerability());
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
                        } catch (InvocationTargetException | AFLException exception) {
                            logUtil.logException(exception);
                            AFLException e = null;
                            if (exception instanceof InvocationTargetException) {
                                Throwable targetException = ((InvocationTargetException) exception).getTargetException();
                                boolean isAFLException = (targetException instanceof AFLException);
                                if (!isAFLException) {
                                    targetException.printStackTrace();
                                    continue;
                                }
                                e = (AFLException) targetException;
                            } else {
                                e = (AFLException) exception;
                            }
                            switch (e.getExceptionStatus()) {
                                // 如果当前coverage文件不存在，则直接返回之前的bitmap
                                case COVERAGE_FILE_NOT_EXISTS:
                                    // 如果是参数有误，则直接返回之前的bitmap
                                case INVALID_ARGUMENTS:
                                    System.out.println("exception from invalid argument");
                                    e.printStackTrace();
                                    fuzzingStatus = FuzzingStatus.NEXT;
                                    bitMap = bitMapUtil.getPreviousBitMap();
                                    ActionFuzzingResult actionFuzzingResult = environmentUtil.getActionFuzzingResult();
                                    actionFuzzingResult.setInvalidArgumentCount(actionFuzzingResult.getInvalidArgumentCount() + 1);
                                    break;
                                // 如果是当前文件不存在，则跳过当前执行
                                case CUR_INPUT_FILE_NOT_EXISTS:
                                    if (aflUtil.isAFLRunning())
                                        continue FuzzingIteration;
                                    // 如果afl进程已经退出
                                case PROCESS_EXIT:
                                    aflUtil.setUsingAFLDriver(true);
                                    break FuzzingIteration;
                            }
                        }

                        environmentUtil.setFuzzingStatus(fuzzingStatus);
                        environmentUtil.setBitmap(bitMap);

                        if (!middlewareUtil.getAfterFuzzingMiddleware().check())
                            break;
                    }

                    middlewareUtil.getAfterActionFuzzingMiddleware().check();
                }

                middlewareUtil.getAfterFuzzerFuzzingMiddleware().check();

            }

            middlewareUtil.getAfterSmartContractFuzzingMiddleware().check();

        }

        middlewareUtil.getAfterEOSFuzzingMiddleware().check();
    }
}