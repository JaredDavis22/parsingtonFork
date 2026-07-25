package org.scijava.parsington;

public class ParseNumberResults {
    int ndxSign = -1;
    ParseNumber.NumberType numberType;
    int[] beginGroup = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    int[] endGroup = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    Number number =null;

    void setSignGroup(int signGroup) {
        if (ndxSign != -1) {
            beginGroup[signGroup] = ndxSign;
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

}
