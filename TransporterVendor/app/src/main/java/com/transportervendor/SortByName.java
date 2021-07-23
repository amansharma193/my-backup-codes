package com.transportervendor;

import com.transportervendor.beans.State;

import java.util.Comparator;

public class SortByName implements Comparator<State> {
    @Override
    public int compare(State o1, State o2) {
        return o1.getStateName().compareTo(o2.getStateName());
    }
}
