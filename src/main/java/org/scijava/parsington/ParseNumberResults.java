package org.scijava.parsington;

public class ParseNumberResults {
    private final int EMPTY = -1;
    private final int GROUPALL = 1;
    private final int[] beginGroup = {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY};
    private final int[] endGroup = {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY};
    private int signNdx = EMPTY;

    private NumberType numberType;
    private Number number = null;

    void setSignIndex(int signNdx) {
        this.signNdx = signNdx;
    }

    void setSignGroup(int signGroup) {
        if (signNdx != EMPTY) {
            beginGroup[signGroup] = signNdx;
            endGroup[signGroup] = signNdx + 1;
        }
    }

    String getGroup(String in, int group) {
        if ((beginGroup[group] != EMPTY) && (endGroup[group] != EMPTY)) {
            return in.substring(beginGroup[group], endGroup[group]);
        } else {
            return "";
        }
    }

    int getLength() {
        return getGroupLength(GROUPALL);
    }

    Number getNumber() {
        return number;
    }

    void setNumber(Number number) {
        this.number = number;
    }

    void setBegin(int ndx) {
        setBeginGroup(GROUPALL, ndx);
    }

    void setEnd(int ndx) {
        setEndGroup(GROUPALL, ndx);
    }

    int setBeginGroup(int group, int ndx) {
        beginGroup[group] = ndx;
        return group;
    }

    int getBeginGroup(int group) {
        return beginGroup[group];
    }

    void setEndGroup(int group, int ndx) {
        endGroup[group] = ndx;
    }

    int getEndGroup(int group) {
        return endGroup[group];
    }

    int offsetBeginGroup(int group, int offset) {
        return beginGroup[group] += offset;
    }

    void offsetEndGroup(int group, int offset) {
        endGroup[group] += offset;
    }

    int getGroupLength(int group) {
        if ((beginGroup[group] != EMPTY) && (endGroup[group] != EMPTY)) {
            return endGroup[group] - beginGroup[group];
        }
        return 0;
    }

    boolean hasGroupBegin(int group) {
        return beginGroup[group] != EMPTY;
    }

    boolean groupIsEmpty(int group) {
        return beginGroup[group] == EMPTY;
    }

    void resetGroup(int group) {
        beginGroup[group] = EMPTY;
        endGroup[group] = EMPTY;
    }

    int setGroup(int group, int start, int stop) {
        beginGroup[group] = start;
        endGroup[group] = stop;
        return group;
    }

    // NB destination first
    void copyGroup(int dest, int source) {
        beginGroup[dest] = beginGroup[source];
        endGroup[dest] = endGroup[source];
    }

    void setEndGroupMaxOfRange(int destination, int sourceStart, int sourceStop) {
        int maxEnd = endGroup[sourceStart];
        for (int g = sourceStart + 1; g <= sourceStop; g++) {
            if (endGroup[g] > maxEnd) {
                maxEnd = endGroup[g];
            }
        }
        endGroup[destination] = maxEnd;
    }

    void setGroupBeginIfNotSet(int group, int start) {
        if (beginGroup[group] == EMPTY) {
            beginGroup[group] = start;
        }
    }

    int endGroupBeginGroup(int groupEnd, int groupBegin, int startEnd) {
        endGroup[groupEnd] = startEnd;
        beginGroup[groupBegin] = startEnd;
        return groupBegin;
    }

    public NumberType getNumberType() {
        return numberType;
    }

    public void setNumberType(NumberType numberType) {
        this.numberType = numberType;
    }

    public enum NumberType {
        NOT_A_NUMBER, INTEGER, // or LONG or BIGINT
        DOUBLE, // or FLOAT
        HEXADECIMAL, OCTAL, // INTEGER or LONG
        BINARY, // INTEGER, DOUBLE, FLOAT
    }

}
