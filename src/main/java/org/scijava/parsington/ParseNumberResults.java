package org.scijava.parsington;

public class ParseNumberResults {
    private int signNdx = -1;
    ParseNumber.NumberType numberType;
    private final int[] beginGroup = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    private final int[] endGroup = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    private Number number = null;


    void setSignIndex(int signNdx) {
        this.signNdx = signNdx;
    }

    void setSignGroup(int signGroup) {
        if (signNdx != -1) {
            beginGroup[signGroup] = signNdx;
            endGroup[signGroup] = beginGroup[signGroup] + 1;
        }
    }

    String getGroup(String in, int group) {
        if ((beginGroup[group] != -1)  && (endGroup[group] != -1)) {
            return in.substring( beginGroup[group], endGroup[group]);
        } else {
            return "";
        }
    }

    int getLength() {
        final int ONE = 1;
        if ((beginGroup[ONE] != -1)  && (endGroup[ONE] != -1)) {
            return endGroup[ONE] - beginGroup[ONE];
        }
        return 0;
    }

    Number getNumber() {
        return number;
    }
    void setNumber(Number number) {
        this.number = number;
    }

    public int[] getBeginGroup() {
        return beginGroup;
    }

    public int[] getEndGroup() {
        return endGroup;
    }
}
