package com.atoudeft.banque;

import java.io.Serializable;

public enum TypeCompte implements Serializable {
    CHEQUE,
    EPARGNE;

    public static TypeCompte fromString(String type) {
        switch (type.toUpperCase()) {
            case "CHEQUE":
                return CHEQUE;
            case "EPARGNE":
                return EPARGNE;
            default:
                return null;
        }
    }
}
