package com.atoudeft.banque;

public class OperationFacture extends Operation {
    private double montant;
    private String numFacture;
    private String description;

    public OperationFacture(double montant, String numFacture, String description) {
        super(TypeOperation.TRANSFER);
        this.montant = montant;
        this.numFacture = numFacture;
        this.description = description;
    }

    @Override
    public String toString() {
        return date + " " + typeOperation.name() + " " + montant;
    }
}
