package com.atoudeft.banque;

import java.io.Serializable;
import java.util.Date;

public abstract class Operation implements Serializable {
    protected TypeOperation typeOperation;
    protected Date date;

    public Operation(TypeOperation typeOperation) {
        this.typeOperation = typeOperation;
        this.date = new Date(System.currentTimeMillis());
    }


}
