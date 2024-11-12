package com.atoudeft.banque;

import com.atoudeft.banque.serveur.OperationRetrait;

public class CompteCheque extends CompteBancaire{
    /**
     * Crée un compte bancaire.
     *
     * @param numero numéro du compte
     */
    public CompteCheque(String numero) {
        super(numero, TypeCompte.CHEQUE);
    }

    @Override
    public boolean crediter(double montant) {
        if(solde > 0){
            solde += montant;
            historique.ajouterDebut(new OperationDepot(montant));
            return true;
        }
        return false;
    }

    @Override
    public boolean debiter(double montant) {
        if(solde > 0){
            solde -= montant;
            historique.ajouterDebut(new OperationRetrait(montant));
            return true;
        }
        return false;
    }

    @Override
    public boolean payerFacture(String numeroFacture, double montant, String description) {
        if(solde > 0){
            solde -= montant;
            historique.ajouterDebut(new OperationFacture(montant, numeroFacture, description));
            return true;
        }
        return false;
    }

    @Override
    public boolean transferer(double montant, String numeroCompteDestinataire) {
        if(solde > 0){
            solde -= montant;
            historique.ajouterDebut(new OperationTranfer(montant, numeroCompteDestinataire));
            return true;
        }
        return false;
    }

    @Override
    public String afficherHistorique() {
        String historiqueEnTantQueString = "";
        if (historique.estVide()) {
            return "Historique vide";
        }

        Noeud noeudCourant = historique.getTete();
        while (noeudCourant != null) {
            historiqueEnTantQueString = noeudCourant.getElement().toString() + "\n";
            noeudCourant = noeudCourant.getSuivant();
        }
        return historiqueEnTantQueString;
    }
}
