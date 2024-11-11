package com.atoudeft.banque;

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
            return true;
        }
        return false;
    }

    @Override
    public boolean payerFacture(String numeroFacture, double montant, String description) {
        return false;
    }

    @Override
    public boolean transferer(double montant, String numeroCompteDestinataire) {
        return false;
    }
}
