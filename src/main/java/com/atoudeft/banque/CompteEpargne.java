package com.atoudeft.banque;

public class CompteEpargne extends CompteBancaire{
    /**
     * Crée un compte bancaire.
     *
     * @param numero numéro du compte
     */
    private static final double LIMITE_SOLDE = 1000.0;
    private static final double FRAIS = 2.0;
    private double tauxInteret;

    public CompteEpargne(String numero, double tauxInteret) {
        super(numero, TypeCompte.EPARGNE);
        this.tauxInteret = tauxInteret;
    }

    @Override
    public boolean crediter(double montant) {
        if(solde > 0){
            solde += montant;
            return true;
        }
        return false;
    }

    @Override
    public boolean debiter(double montant) {
        double montantAvantOperation = solde;
        if(solde > 0 && solde >= montant){
            solde -= montant;
            if(montantAvantOperation >= LIMITE_SOLDE){
                solde -= FRAIS;
            }
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean payerFacture(String numeroFacture, double montant, String description) {
        return false;
    }

    @Override
    public boolean transferer(double montant, String numeroCompteDestinataire) {
        return false;
    }

    public void ajouterInterets(){
        double interets = solde * tauxInteret;
        solde += interets;
    }
}
