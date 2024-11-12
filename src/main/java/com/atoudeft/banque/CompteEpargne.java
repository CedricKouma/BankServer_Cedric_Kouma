package com.atoudeft.banque;

import com.atoudeft.banque.serveur.OperationRetrait;

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
        if(solde >= 0){
            solde += montant;
            historique.ajouterDebut(new OperationDepot(montant));

            return true;
        }
        return false;
    }

    @Override
    public boolean debiter(double montant) {
        double montantAvantOperation = solde;
        if(solde >= 0 && solde >= montant){
            solde -= montant;
            if(montantAvantOperation >= LIMITE_SOLDE){
                solde -= FRAIS;
            }
            historique.ajouterDebut(new OperationRetrait(montant));
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean payerFacture(String numeroFacture, double montant, String description) {
        double montantAvantOperation = solde;
        if(solde > 0 && solde >= montant){
            solde -= montant;
            if(montantAvantOperation >= LIMITE_SOLDE){
                solde -= FRAIS;
            }
            historique.ajouterDebut(new OperationFacture(montant, numeroFacture, description));
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean transferer(double montant, String numeroCompteDestinataire) {
        double montantAvantOperation = solde;
        if(solde > 0 && solde >= montant){
            solde -= montant;
            if(montantAvantOperation >= LIMITE_SOLDE){
                solde -= FRAIS;
            }
            historique.ajouterDebut(new OperationTranfer(montant, numeroCompteDestinataire));
            return true;
        }
        else {
            return false;
        }
    }

    public void ajouterInterets(){
        double interets = solde * tauxInteret;
        solde += interets;
    }

    @Override
    public String afficherHistorique() {
        StringBuilder historiqueEnTantQueString = new StringBuilder();
        if (historique.estVide()) {
            return "Historique vide";
        }

        Noeud noeudCourant = historique.getTete();
        while (noeudCourant != null) {
            historiqueEnTantQueString.append(noeudCourant.getElement().toString()).append("\n");
            noeudCourant = noeudCourant.getSuivant();
        }
        return historiqueEnTantQueString.toString();
    }
}
