package com.atoudeft.banque.serveur;

import com.atoudeft.banque.Operation;
import com.atoudeft.banque.TypeOperation;

public class OperationRetrait  extends Operation {
    private double montant;
    public OperationRetrait(double montant) {
        super(TypeOperation.RETRAIT);
        this.montant = montant;
    }

    @Override
    public String toString() {
        return date + " " + typeOperation.name() + " " + montant;
    }
}
