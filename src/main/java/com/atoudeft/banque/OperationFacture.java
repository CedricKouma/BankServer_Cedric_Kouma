package com.atoudeft.banque;

public class OperationFacture extends Operation{
    private double montant;
    private String numCompteDestinataire;

    public OperationFacture(double montant, String numCompteDestinataire) {
        super(TypeOperation.FACTURE);
        this.montant = montant;
        this.numCompteDestinataire = numCompteDestinataire;
    }

    @Override
    public String toString() {
        return date + " " + typeOperation.name() + " " + montant;
    }
}
