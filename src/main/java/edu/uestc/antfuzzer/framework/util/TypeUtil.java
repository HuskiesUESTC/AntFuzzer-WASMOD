package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.annotation.Param;
import edu.uestc.antfuzzer.framework.bean.SmartContract;
import edu.uestc.antfuzzer.framework.bean.abi.Action;
import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.exception.AFLException;
import edu.uestc.antfuzzer.framework.type.ArgumentGenerator;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.math.BigInteger;

@Component
public class TypeUtil {

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private EOSUtil eosUtil;

    @Getter
    @Autowired
    private AFLUtil aflUtil;

    @Setter
    @Getter
    private ArgDriver argDriver;

    @Autowired
    private EnvironmentUtil environmentUtil;

    public Object[] generateBeforeParams(ArgumentGenerator generator, Parameter[] parameters) {
        int count = parameters.length;
        Object[] params = new Object[count];
        for (int i = 0; i < count; i++) {
            Param parameter = parameters[i].getAnnotation(Param.class);
            if (parameter == null) {
                params[i] = null;
                continue;
            }
            switch (parameter.value()) {
                case ArgGenerator:
                    params[i] = generator;
                    break;
            }
        }
        return params;
    }

    public Object[] generateFuzzingParams(Action action, ArgumentGenerator generator, Parameter[] parameters) throws IOException, InterruptedException, AFLException {
        int count = parameters.length;
        Object[] params = new Object[count];
        for (int i = 0; i < count; i++) {
            Param parameter = parameters[i].getAnnotation(Param.class);
            if (parameter == null) {
                params[i] = null;
                continue;
            }
            switch (parameter.value()) {
                case Action:
                    params[i] = action.getName();
                    break;
                case Arguments:
                    params[i] = generator.generateFuzzingArguments(action);
                    break;
                case ArgGenerator:
                    params[i] = generator;
                    break;
            }
        }
        return params;
    }

    public ArgumentGenerator getGenerator(SmartContract smartContract) {
        return new ArgumentGenerator(
                smartContract.getAbi(),
                smartContract.getName(),
                smartContract.getName(),
                configUtil.getFrameworkConfig().getAccount().getPublicKey(),
                smartContract.getType().getMemoStringPool(),
                eosUtil.getAccountPool(),
                getArgDriver(),
                getAflUtil()
        );
    }


    private int charToSymbol(char c) {
        if (c >= 'a' && c <= 'z')
            return (c - 'a') + 6;
        if (c >= '1' && c <= '5')
            return (c - '1') + 1;
        return 0;
    }

    public String stringToName(String str) {
        BigInteger name = BigInteger.valueOf(0);
        int i = 0;
        for (; i < str.length() && i < 12; ++i) {
            name = name.or(BigInteger.valueOf(charToSymbol(str.charAt(i)) & 0x1f).shiftLeft(64 - 5 * (i + 1)));
        }
        if (i == 12) {
            str = str + "\0";
            name = name.or(BigInteger.valueOf(charToSymbol((char) (str.charAt(12) & 0x0F))));
        }
        return name.toString();
    }


}