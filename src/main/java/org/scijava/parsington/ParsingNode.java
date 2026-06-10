package org.scijava.parsington;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ParsingNode {
    List<Operator> payload = null;
    List<ParsingNode> next = new ArrayList<>();
    char value = 0;

    ParsingNode hasValueNext(char v) {
        int ndx = getIndex(v);
        if (ndx >= 0) {
            return next.get(ndx);
        } else {
            return null;
        }
    }

    char getValue() {
        return value;
    }

    // Int vs char  ? ok
    Comparator<ParsingNode> vComparator = Comparator.comparingInt(ParsingNode::getValue);

    int getIndex(char v) {
        ParsingNode pn = new ParsingNode();
        pn.value = v;
        return Collections.binarySearch(next, pn, vComparator);
    }


    ParsingNode addNextValue(char v, Operator op) {
        int ndx = getIndex(v);
        ParsingNode n;
        if (ndx >= 0) {
            n = next.get(ndx);
        } else {
            n = new ParsingNode();
            n.value = v;
            next.add(-ndx - 1, n);
        }
        if (op != null) {
            if (n.payload == null) {
                n.payload = new ArrayList<>(6);
            }
            n.payload.add(op);
        }
        return n;
    }
}
