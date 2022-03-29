package edu.uestc.antfuzzer.framework.util.type;

import edu.uestc.antfuzzer.framework.bean.abi.ABI;
import edu.uestc.antfuzzer.framework.bean.abi.Action;
import edu.uestc.antfuzzer.framework.bean.abi.Struct;
import edu.uestc.antfuzzer.framework.bean.abi.Type;
import edu.uestc.antfuzzer.framework.util.EOSUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Data;
import lombok.Getter;

import java.math.BigInteger;
import java.util.*;

@Data
public class ArgumentGenerator {
    public class Arguments {
        @Getter
        String fuzzingArguments;
        @Getter
        List<String> uint64;
        @Getter
        List<String> int64;
        @Getter
        List<String> uint32;
        @Getter
        List<String> int32;
        @Getter
        List<String> asset;
        @Getter
        List<String> accounts;

        Arguments() {
            uint64 = new ArrayList<>();
            int64 = new ArrayList<>();
            uint32 = new ArrayList<>();
            int32 = new ArrayList<>();
            asset = new ArrayList<>();
            accounts = new ArrayList<>();
        }
    }

    private ABI abi;
    private String contract;
    private String account;
    private String publicKey;
    private List<String> memoStringPool;
    private List<EOSUtil.Account> accountPool;
    private Random random;
    private Map<String, TypeGenerator> typeGenerators;

    public ArgumentGenerator(ABI abi, String contract, String account, String publicKey, List<String> memoStringPool, List<EOSUtil.Account> accountPool) {
        this.abi = abi;
        this.contract = contract;
        this.account = account;
        this.publicKey = publicKey;
        this.memoStringPool = memoStringPool;
        this.accountPool = accountPool;
        this.random = new Random(System.currentTimeMillis() % 1000000);
        this.loadTypeGenerators();
    }

    /* 生成fuzzing参数 并按类别分开存放在Argument类中 */
    public Arguments generateAndStoreFuzzingArguments(Action action) {
        Arguments arguments = new Arguments();
        Struct struct = getStruct(action.getType());
        JsonElement json = generateData(struct, arguments);
        arguments.fuzzingArguments = json.toString();
        return arguments;
    }

    public String generateFuzzingArguments(Action action) {
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

    private String generateDataForSimpleType(String type) {
        TypeGenerator typeGenerator = typeGenerators.get(type.trim().toLowerCase());
        return (typeGenerator == null) ? "" : typeGenerator.generate();
    }

    private String generateDataForSimpleType(String type, Arguments arguments) {
        TypeGenerator typeGenerator = typeGenerators.get(type.trim().toLowerCase());
        if (typeGenerator != null) {
            String value = typeGenerator.generate();
            switch (type) {
                case "uint32": {
                    arguments.uint32.add(value);
                    break;
                }
                case "uint64": {
                    arguments.uint64.add(value);
                    break;
                }
                case "int32": {
                    arguments.int32.add(value);
                    break;
                }
                case "int64": {
                    arguments.int64.add(value);
                    break;
                }
                case "name": {
                    arguments.accounts.add(value);
                    break;
                }
                case "asset": {
                    arguments.asset.add(value);
                    break;
                }
            }
            return value;
        }
        return "";
    }

    private JsonElement generateData(Struct struct, Arguments arguments) {
        JsonObject json = new JsonObject();
        /* Bug fix */
        if (struct == null || struct.getFields() == null)
            return json;
        List<Struct.Field> fields = struct.getFields();
        for (Struct.Field field : fields) {
            String name = field.getName();
            String type = field.getType();
            // 如果需要处理的是数组
            if (field.getType().contains("[]")) {
                random.setSeed(random.nextInt());
                int arrayLength = random.nextInt(9) + 1;
                JsonArray fieldValues = new JsonArray();
                String baseStructName = type.replace("[]", "");
                Struct baseStruct = getStruct(baseStructName);
                for (int i = 0; i < arrayLength; i++) {
                    JsonElement fieldValue = (baseStruct.getName().equalsIgnoreCase("~")) ?
                            (new JsonPrimitive(generateDataForSimpleType(baseStruct.getBase(), arguments))) :
                            generateData(baseStruct, arguments);
                    fieldValues.add(fieldValue);
                }
                json.add(name, fieldValues);
            } else {
                Struct fieldStruct = getStruct(type);
                JsonElement fieldValue = (fieldStruct.getName().equalsIgnoreCase("~")) ?
                        (new JsonPrimitive(generateDataForSimpleType(fieldStruct.getBase(), arguments))) :
                        generateData(fieldStruct, arguments);
                json.add(name, fieldValue);
            }
        }
        return json;
    }


    private JsonElement generateData(Struct struct) {
        JsonObject json = new JsonObject();
        /* Bug fix */
        if (struct == null || struct.getFields() == null)
            return json;
        List<Struct.Field> fields = struct.getFields();
        for (Struct.Field field : fields) {
            String name = field.getName();
            String type = field.getType();
            // 如果需要处理的是数组
            if (field.getType().contains("[]")) {
                random.setSeed(random.nextInt());
                int arrayLength = random.nextInt(9) + 1;
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

    public Object generateSpecialTypeArgument(String type) {
        TypeGenerator typeGenerator = typeGenerators.get(type);
        if (type == null)
            return null;
        return typeGenerator.generate();
    }

    private void loadTypeGenerators() {
        typeGenerators = new HashMap<>();

        typeGenerators.put("int8", () -> {
            int[] result = {-128, 127, random.nextInt(128)};
            return String.valueOf(result[random.nextInt(3)]);
        });

        typeGenerators.put("uint8", () -> {
            int[] result = {0, 255, random.nextInt(256)};
            return String.valueOf(result[random.nextInt(3)]);
        });

        typeGenerators.put("int16", () -> {
            int[] result = {Short.MIN_VALUE, Short.MAX_VALUE, (short) random.nextInt(Short.MAX_VALUE)};
            return String.valueOf(result[random.nextInt(3)]);
        });

        typeGenerators.put("uint16", () -> {
            int[] result = {0, 65535, random.nextInt(65536)};
            return String.valueOf(result[random.nextInt(3)]);
        });

        typeGenerators.put("int32", () -> {
            int[] result = {Integer.MIN_VALUE + Math.abs(random.nextInt(256) + 128),
                    Integer.MAX_VALUE + -1 * (Math.abs(random.nextInt(256) + 128)),
                    random.nextInt(Integer.MAX_VALUE)};
            return String.valueOf(result[random.nextInt(3)]);
        });

        typeGenerators.put("uint32", () -> {
            long[] result = {0,
                    4294967296L + -1 * (Math.abs(random.nextInt(256) + 128)),
                    random.nextLong() % 4294967296L};
            return String.valueOf(result[random.nextInt(3)]);
        });

        typeGenerators.put("int64", () -> {
            long[] result = {Long.MIN_VALUE + Math.abs(random.nextInt(256) + 128),
                    Long.MAX_VALUE + -1 * (Math.abs(random.nextInt(256) + 128)),
                    random.nextLong()};
            return String.valueOf(result[random.nextInt(3)]);
        });

        typeGenerators.put("uint64", () -> {
            /* Bug fix */
            BigInteger[] result = {BigInteger.valueOf(0),
                    new BigInteger("18446744073709551615").add(BigInteger.valueOf(-1 * (Math.abs(random.nextInt(256) + 128)))),
                    BigInteger.valueOf(Math.abs(random.nextLong() + 1))};
            return String.valueOf(result[random.nextInt(3)]);
        });

        typeGenerators.put("int128", typeGenerators.get("int64"));
        typeGenerators.put("uint128", typeGenerators.get("uint64"));
        typeGenerators.put("varint32", typeGenerators.get("int32"));
        typeGenerators.put("uvarint32", typeGenerators.get("uint32"));

        typeGenerators.put("float32", () -> {
            float[] result = {Float.MAX_EXPONENT, Float.MAX_VALUE, Float.MIN_EXPONENT, Float.MIN_NORMAL, Float.MIN_VALUE};
            return String.valueOf(result[random.nextInt(5)]);
        });

        typeGenerators.put("float64", () -> {
            double[] result = {Double.MAX_EXPONENT, Double.MAX_VALUE, Double.MIN_EXPONENT, Double.MIN_NORMAL, Double.MIN_VALUE};
            return String.valueOf(result[random.nextInt(5)]);
        });

        typeGenerators.put("float128", typeGenerators.get("uint128"));

        typeGenerators.put("bool", () -> String.valueOf(random.nextBoolean()));

        typeGenerators.put("string", () -> {
            int size = memoStringPool.size();
            if (size == 0) {
                String dic = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                // 生成随机长度和内容的字符串
                size = random.nextInt(500);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < size; i++) {
                    sb.append(dic.charAt(random.nextInt(62)));
                }
                return sb.toString();
            }
            return memoStringPool.get(random.nextInt(size));
        });

        typeGenerators.put("symbol", () -> "4,EOS");

        typeGenerators.put("symbol_code", () -> "TOKEN");

        typeGenerators.put("asset", () -> {
            String first = String.valueOf(random.nextInt(1000));
            String second = String.valueOf(random.nextInt(10000) + 10000).substring(1);
            return String.format("%s.%s EOS", first, second);
        });

        typeGenerators.put("name", () -> {
            String activeAccount = this.account;
            int size = accountPool.size();
            if (size != 0)
                activeAccount = accountPool.get(random.nextInt(size)).getName();

            return activeAccount == null ? "" : activeAccount;
        });

        typeGenerators.put("public_key", () -> {
            String publicKey = this.publicKey;
            int size = accountPool.size();
            if (size != 0)
                publicKey = accountPool.get(random.nextInt(size)).getPublicKey();
            return publicKey;
        });
    }

    public Action getRandomAction() {
        final int actionCount = abi.getActions().size();
        if (actionCount == 0) {
            // no action
            return null;
        }
        final int actionIndex = Math.abs(random.nextInt()) % actionCount;
        return abi.getActions().get(actionIndex);
    }
}