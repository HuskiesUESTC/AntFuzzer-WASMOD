package edu.uestc.antfuzzer.framework.bean.afl;

import lombok.Data;

import java.util.ArrayList;
import java.util.PriorityQueue;

@Data
public class AFLGeneratorInfoCollection {

    ArrayList<AFLGeneratorInfo> list;
    PriorityQueue<AFLGeneratorInfo> queue;

    public AFLGeneratorInfoCollection() {
        list = new ArrayList<>(10);
        queue = new PriorityQueue<>((o1, o2) -> {
            int priority1 = o1.getPriority() * 100 - o1.getIndex();
            int priority2 = o2.getPriority() * 100 - o2.getIndex();
            return priority2 - priority1;
        });
    }
}
