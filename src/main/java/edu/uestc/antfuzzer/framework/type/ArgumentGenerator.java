package edu.uestc.antfuzzer.framework.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import edu.uestc.antfuzzer.framework.bean.abi.ABI;
import edu.uestc.antfuzzer.framework.bean.abi.Action;
import edu.uestc.antfuzzer.framework.bean.abi.Struct;
import edu.uestc.antfuzzer.framework.bean.abi.Type;
import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.exception.AFLException;
import edu.uestc.antfuzzer.framework.type.interfaces.AFLGenerator;
import edu.uestc.antfuzzer.framework.type.interfaces.TypeGenerator;
import edu.uestc.antfuzzer.framework.type.number.decimal.Float32Generator;
import edu.uestc.antfuzzer.framework.type.number.decimal.Float64Generator;
import edu.uestc.antfuzzer.framework.type.number.integer.IntGenerator;
import edu.uestc.antfuzzer.framework.type.number.integer.UIntGenerator;
import edu.uestc.antfuzzer.framework.util.AFLUtil;
import edu.uestc.antfuzzer.framework.util.EOSUtil;
import lombok.Data;

import java.io.IOException;
import java.util.*;

@Data
public class ArgumentGenerator {

    private ABI abi;
    private String contract;
    private String account;
    private String publicKey;
    private List<String> memoStringPool;
    private List<EOSUtil.Account> accountPool;
    private Random random;
    private TypeGeneratorCollection typeGeneratorCollection;
    private ArgDriver argDriver;
    private AFLUtil aflUtil;
    private float rate = 0.05f;

    public ArgumentGenerator(ABI abi, String contract, String account, String publicKey, List<String> memoStringPool, List<EOSUtil.Account> accountPool, ArgDriver argDriver, AFLUtil aflUtil) {
        this.abi = abi;
        this.contract = contract;
        this.account = account;
        this.publicKey = publicKey;
        this.memoStringPool = memoStringPool;
        this.accountPool = accountPool;
        this.random = new Random(System.currentTimeMillis() % 1000000);
        this.argDriver = argDriver;
        this.aflUtil = aflUtil;

        // 设置默认的参数生成驱动
        typeGeneratorCollection = new TypeGeneratorCollection();

    }

    public String generateFuzzingArguments(Action action) throws IOException, InterruptedException, AFLException {
        Struct struct = getStruct(action.getType());
        JsonElement json = generateData(struct);
        return json.toString();
    }

    private List<Struct.Field> getStructFields(String structName) {
        List<Struct> structs = abi.getStructs();
        for (Struct struct : structs) {
            if (struct.getName().equalsIgnoreCase(structName)) {
                return struct.getFields();
            }
        }
        return new ArrayList<>();
    }

    private Struct getStruct(String structName) {
        if (structName != null) {
            List<Type> types = abi.getTypes();
            if (types != null) {
                for (Type type : types) {
                    if (type.getNewTypeName().equalsIgnoreCase(structName)) {
                        return getStruct(type.getType());
                    }
                }
            }
            List<Struct> structs = abi.getStructs();
            for (Struct struct : structs) {
                if (struct.getName().equalsIgnoreCase(structName)) {
                    return struct;
                }
            }
        }
        Struct struct = new Struct();
        struct.setName("~");
        struct.setBase(structName);
        return struct;
    }

    private String getOriginType(String typeName) {
        List<Type> types = abi.getTypes();
        for (Type type : types) {
            if (type.getNewTypeName().equalsIgnoreCase(typeName)) {
                return type.getType();
            }
        }
        return "";
    }

    private String generateDataForSimpleType(String type) throws IOException, InterruptedException, AFLException {
        TypeGenerator typeGenerator = typeGeneratorCollection.get(type.trim().toLowerCase());
        String result = "";
        if (typeGenerator != null) {
            result = typeGenerator.generate();
            try {
                AFLGenerator aflGenerator = (AFLGenerator) typeGenerator;
                aflUtil.setUsingAFLDriver(true);
            } catch (ClassCastException ignored) {
            }
        }
        return result;
    }

    private JsonElement generateData(Struct struct) throws IOException, InterruptedException, AFLException {
        JsonObject json = new JsonObject();
        List<Struct.Field> fields = null;
        if (struct == null || (fields = struct.getFields()) == null)
            return json;

        for (Struct.Field field : fields) {
            String name = field.getName();
            String type = field.getType();
            // 如果需要处理的是数组
            if (field.getType().contains("[]")) {
                random.setSeed(random.nextInt());
                int arrayLength = argDriver == ArgDriver.afl ? 4 : random.nextInt(9) + 1;
                JsonArray fieldValues = new JsonArray();
                String baseStructName = type.replace("[]", "");
                Struct baseStruct = getStruct(baseStructName);
                for (int i = 0; i < arrayLength; i++) {
                    JsonElement fieldValue = (baseStruct.getName().equalsIgnoreCase("~")) ?
                            (new JsonPrimitive(generateDataForSimpleType(baseStruct.getBase()))) :
                            generateData(baseStruct);
                    fieldValues.add(fieldValue);
                }
                json.add(name, fieldValues);
            } else {
                Struct fieldStruct = getStruct(type);
                JsonElement fieldValue = (fieldStruct.getName().equalsIgnoreCase("~")) ?
                        (new JsonPrimitive(generateDataForSimpleType(fieldStruct.getBase()))) :
                        generateData(fieldStruct);
                json.add(name, fieldValue);
            }
        }
        return json;
    }

    public Object generateSpecialTypeArgument(String type) throws IOException, InterruptedException, AFLException {
        TypeGenerator typeGenerator = typeGeneratorCollection.get(type);
        Object result = null;
        if (typeGenerator != null) {
            result = typeGenerator.generate();
            try {
                AFLGenerator aflGenerator = (AFLGenerator) typeGenerator;
                aflUtil.setUsingAFLDriver(true);
            } catch (ClassCastException ignored) {
            }
        }
        return result;
    }


    /**
     * 延迟加载
     */
    public class TypeGeneratorCollection {

        private final Map<String, TypeGenerator> collections = new HashMap<>();

        public TypeGenerator get(String type) {
            TypeGenerator typeGenerator = null;
            if ((typeGenerator = collections.get(type)) != null)
                return typeGenerator;
            switch (type) {
                case "int8":
                    typeGenerator = new IntGenerator(random, argDriver, aflUtil, 1, rate);
                    break;
                case "int16":
                    typeGenerator = new IntGenerator(random, argDriver, aflUtil, 2, rate);
                    break;
                case "int32":
                    typeGenerator = new IntGenerator(random, argDriver, aflUtil, 4, rate);
                    break;
                case "int64":
                    typeGenerator = new IntGenerator(random, argDriver, aflUtil, 8, rate);
                    break;
                case "int128":
                    typeGenerator = new IntGenerator(random, argDriver, aflUtil, 16, rate);
                    break;
                case "uint8":
                    typeGenerator = new UIntGenerator(random, argDriver, aflUtil, 1, rate);
                    break;
                case "uint16":
                    typeGenerator = new UIntGenerator(random, argDriver, aflUtil, 2, rate);
                    break;
                case "uint32":
                    typeGenerator = new UIntGenerator(random, argDriver, aflUtil, 4, rate);
                    break;
                case "uint64":
                    typeGenerator = new UIntGenerator(random, argDriver, aflUtil, 8, rate);
                    break;
                case "uint128":
                    typeGenerator = new UIntGenerator(random, argDriver, aflUtil, 16, rate);
                    break;
                case "varint32":
                    typeGenerator = get("int32");
                    break;
                case "uvarint32":
                    typeGenerator = get("uint32");
                    break;
                case "float32":
                    typeGenerator = new Float32Generator(random, argDriver, aflUtil, rate);
                    break;
                case "float64":
                    typeGenerator = new Float64Generator(random, argDriver, aflUtil, rate);
                    break;
                case "float128":
                    typeGenerator = get("float64");
                    break;
                case "string":
                    typeGenerator = new StringGenerator(memoStringPool, random, argDriver, aflUtil);
                    break;
                case "symbol":
                    typeGenerator = () -> "4,EOS";
                    break;
                case "asset":
                    typeGenerator = () -> {
                        String first = String.valueOf(random.nextInt(1000));
                        String second = String.valueOf(random.nextInt(10000) + 10000).substring(1);
                        return String.format("%s.%s EOS", first, second);
                    };
                    break;
                case "name":
                    typeGenerator = new NameGenerator(random, account, accountPool);
                    break;
                case "public_key":
                    typeGenerator = () -> {
                        int size = accountPool.size();
                        if (size != 0)
                            publicKey = accountPool.get(random.nextInt(size)).getPublicKey();
                        return publicKey;
                    };
                    break;
                case "bool":
                    typeGenerator = () -> String.valueOf(random.nextBoolean());
                    break;
                default:
                    break;
            }
            if (typeGenerator != null)
                collections.put(type, typeGenerator);
            return typeGenerator;
        }
    }
}