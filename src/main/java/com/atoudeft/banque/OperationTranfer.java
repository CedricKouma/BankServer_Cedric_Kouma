package com.atoudeft.banque;

public class OperationTranfer extends Operation{
    private double montant;
    private String numCompteDestinataire;

    public OperationTranfer(double montant, String numCompteDestinataire) {
        super(TypeOperation.FACTURE);
        this.montant = montant;
        this.numCompteDestinataire = numCompteDestinataire;
    }

    @Override
    public String toString() {
        return date + " " + typeOperation.name() + " " + montant;
    }
}
