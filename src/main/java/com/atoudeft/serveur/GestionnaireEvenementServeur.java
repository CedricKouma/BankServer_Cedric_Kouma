package com.atoudeft.serveur;

import com.atoudeft.banque.*;
import com.atoudeft.banque.serveur.ConnexionBanque;
import com.atoudeft.banque.serveur.ServeurBanque;
import com.atoudeft.commun.evenement.Evenement;
import com.atoudeft.commun.evenement.GestionnaireEvenement;
import com.atoudeft.commun.net.Connexion;

/**
 * Cette classe représente un gestionnaire d'événement d'un serveur. Lorsqu'un serveur reçoit un texte d'un client,
 * il crée un événement à partir du texte reçu et alerte ce gestionnaire qui réagit en gérant l'événement.
 *
 * @author Abdelmoumène Toudeft (Abdelmoumene.Toudeft@etsmtl.ca)
 * @version 1.0
 * @since 2023-09-01
 */
public class GestionnaireEvenementServeur implements GestionnaireEvenement {
    private Serveur serveur;

    /**
     * Construit un gestionnaire d'événements pour un serveur.
     *
     * @param serveur Serveur Le serveur pour lequel ce gestionnaire gère des événements
     */
    public GestionnaireEvenementServeur(Serveur serveur) {
        this.serveur = serveur;
    }

    /**
     * Méthode de gestion d'événements. Cette méthode contiendra le code qui gère les réponses obtenues d'un client.
     *
     * @param evenement L'événement à gérer.
     */
    @Override
    public void traiter(Evenement evenement) {
        Object source = evenement.getSource();
        ServeurBanque serveurBanque = (ServeurBanque)serveur;
        Banque banque;
        ConnexionBanque cnx;
        String msg, typeEvenement, argument, numCompteClient, nip;
        String[] t;

        if (source instanceof Connexion) {
            cnx = (ConnexionBanque) source;
            System.out.println("SERVEUR: Recu : " + evenement.getType() + " " + evenement.getArgument());
            typeEvenement = evenement.getType();
            cnx.setTempsDerniereOperation(System.currentTimeMillis());
            switch (typeEvenement) {
                /******************* COMMANDES GÉNÉRALES *******************/
                case "EXIT": //Ferme la connexion avec le client qui a envoyé "EXIT":
                    cnx.envoyer("END");
                    serveurBanque.enlever(cnx);
                    cnx.close();
                    break;
                case "LIST": //Envoie la liste des numéros de comptes-clients connectés :
                    cnx.envoyer("LIST " + serveurBanque.list());
                    break;
                /******************* COMMANDES DE GESTION DE COMPTES *******************/
                case "NOUVEAU": //Crée un nouveau compte-client :
                    if (cnx.getNumeroCompteClient()!=null) {
                        cnx.envoyer("NOUVEAU NO deja connecte");
                        break;
                    }
                    argument = evenement.getArgument();
                    t = argument.split(":");
                    if (t.length<2) {
                        cnx.envoyer("NOUVEAU NO");
                    }
                    else {
                        numCompteClient = t[0];
                        nip = t[1];
                        banque = serveurBanque.getBanque();
                        if (banque.ajouter(numCompteClient,nip)) {
                            cnx.setNumeroCompteClient(numCompteClient);
                            cnx.setNumeroCompteActuel(banque.getNumeroCompteParDefaut(numCompteClient));
                            cnx.envoyer("NOUVEAU OK " + t[0] + " cree");
                        }
                        else
                            cnx.envoyer("NOUVEAU NO "+t[0]+" existe");
                    }
                    break;
                /******************* COMMANDES DE CONNEXION *******************/
                case "CONNECT": // Connecter un client a son compte :
                    argument = evenement.getArgument();
                    t = argument.split(":");
                    numCompteClient = t[0];
                    nip = t[1];

                    String numerosComptesDejaConnecte = serveurBanque.list();

                    String[] listNumeroCompteConnecte = numerosComptesDejaConnecte.split("\\.");

                    for (String numeroCpt : listNumeroCompteConnecte) {
                        if (numeroCpt.equals(numCompteClient)) {
                            cnx.envoyer("CONNECT NO ");
                        }
                        else {
                            banque = serveurBanque.getBanque();
                            CompteClient cptClient = banque.getCompteClient(numCompteClient);
                            if( cptClient != null && nip.equals(cptClient.getNip())){

                                String numeroCompteCheque = banque.getNumeroCompteParDefaut(numCompteClient);


                                cnx.setNumeroCompteActuel(numeroCompteCheque);
                                cnx.setNumeroCompteClient(numCompteClient);

                                cnx.envoyer("CONNECT OK");
                            }

                            else {
                                cnx.envoyer("CONNECT NO");
                            }


                        }
                    }
                    break;

                /******************* CREATION COMPTE EPARGNE *******************/
                case "EPARGNE": // Creer un compte epargne, s'il n'en n'a pas deja un
                    numCompteClient= cnx.getNumeroCompteClient();
                    banque = serveurBanque.getBanque();
                   if(numCompteClient == null || numCompteClient.isEmpty()){
                       cnx.envoyer("EPARGNE NO");
                   }
                   if(banque.possedeUnCompteEpargne(numCompteClient)){
                       cnx.envoyer("EPARGNE NO");
                   }
                   else {
                       String nouveauNumero;
                       do {
                           nouveauNumero = CompteBancaire.genereNouveauNumero();
                       } while (banque.estNumeroDeCompteBancaireExistant(nouveauNumero));
                       CompteEpargne cptEpargne = new CompteEpargne(nouveauNumero, 5);
                       CompteClient cptClient = banque.getCompteClient(numCompteClient);
                       cptClient.ajouter(cptEpargne);
                       cnx.envoyer("EPARGNE OK");
                   }
                break;


                /******************* SELECT COMPTE *******************/
                case "SELECT": // Switcher entre les comptes
                    numCompteClient = cnx.getNumeroCompteClient();
                    banque = serveurBanque.getBanque();
                    if(numCompteClient == null || numCompteClient.isEmpty()){
                        cnx.envoyer("SELECT NO");
                    }
                    else {
                        argument = evenement.getArgument();
                        TypeCompte typeCompte = TypeCompte.fromString(argument);
                        if(typeCompte != null){
                            String numCptBancaire = banque.getNumeroCompteBancaire(numCompteClient, typeCompte);
                            if(numCptBancaire != null){
                                cnx.setNumeroCompteActuel(numCptBancaire);
                                cnx.envoyer("SELECT OK");
                            }
                            else cnx.envoyer("SELECT NO");

                        }
                        else  cnx.envoyer("SELECT NO");
                    }
                break;


                /******************* DEPOT *******************/
                case "DEPOT": // Crediter le compte
                    numCompteClient = cnx.getNumeroCompteClient();
                    banque = serveurBanque.getBanque();
                    argument = evenement.getArgument();
                    if(numCompteClient == null || numCompteClient.isEmpty()){
                        cnx.envoyer("DEPOT NO");
                    }
                    else {
                        double montant = Double.parseDouble(argument);
                        CompteClient cptClient = banque.getCompteClient(numCompteClient);
                        String numeroCompteBancaire = cnx.getNumeroCompteActuel();

                        if(numeroCompteBancaire != null && cptClient != null){
                            for(CompteBancaire cptBancaire : cptClient.getComptes()){
                                if(cptBancaire.getNumero().equals(numeroCompteBancaire)){
                                    if(cptBancaire.crediter(montant)){
                                        cnx.envoyer("DEPOT OK");
                                    }
                                    else cnx.envoyer("DEPOT NO");
                                }
                            }
                        }
                        else cnx.envoyer("DEPOT NO");

                    }
                    break;

                /******************* RETRAIT *******************/
                case "RETRAIT": // Debiter le compte
                    numCompteClient = cnx.getNumeroCompteClient();
                    banque = serveurBanque.getBanque();
                    argument = evenement.getArgument();

                    if(numCompteClient == null || numCompteClient.isEmpty()){
                        cnx.envoyer("RETRAIT NO");
                    }
                    else {
                        double montant = Double.parseDouble(argument);
                        CompteClient cptClient = banque.getCompteClient(numCompteClient);
                        String numeroCompteBancaire = cnx.getNumeroCompteActuel();
                        if(numeroCompteBancaire != null && cptClient != null){
                            for(CompteBancaire cptBancaire : cptClient.getComptes()){
                                if(cptBancaire.getNumero().equals(numeroCompteBancaire)){
                                    if(cptBancaire.debiter(montant)){
                                        cnx.envoyer("RETRAIT OK");
                                    }
                                    else cnx.envoyer("RETRAIT NO");

                                }
                            }
                        }
                        else cnx.envoyer("RETRAIT NO");

                    }
                    break;


                /******************* FACTURE *******************/
                case "FACTURE": // payer des factures
                    numCompteClient = cnx.getNumeroCompteClient();
                    banque = serveurBanque.getBanque();
                    argument = evenement.getArgument();

                    if(numCompteClient == null || numCompteClient.isEmpty()){
                        cnx.envoyer("FACTURE NO");
                    }
                    else {
                        String[] facture = argument.split(" ", 3);
                        double montant = Double.parseDouble(facture[0]);
                        String numFacture = facture[1];
                        String descriptionFacture = facture[2];
                        CompteClient cptClient = banque.getCompteClient(numCompteClient);
                        String numeroCompteBancaire = cnx.getNumeroCompteActuel();
                        if(numeroCompteBancaire != null && cptClient != null){
                            for(CompteBancaire cptBancaire : cptClient.getComptes()){
                                if(cptBancaire.getNumero().equals(numeroCompteBancaire)){
                                    if(cptBancaire.payerFacture(numFacture, montant, descriptionFacture)){
                                        cnx.envoyer("FACTURE OK");
                                    }
                                    else cnx.envoyer("FACTURE NO");

                                }
                            }
                        }
                        else cnx.envoyer("FACTURE NO");

                    }
                    break;


                /******************* TRANSFER *******************/
                case "TRANSFER":
                    numCompteClient = cnx.getNumeroCompteClient();
                    banque = serveurBanque.getBanque();
                    argument = evenement.getArgument();

                    if(numCompteClient == null || numCompteClient.isEmpty()){
                        cnx.envoyer("TRANSFER NO");
                    }
                    else {
                        String[] transfert = argument.split(" ");

                        double montant = Double.parseDouble(transfert[0]);
                        String noCompteBancaireExpediteur = transfert[1];
                        String noCompteBancaireDestinataire = transfert[2];
                        CompteClient cptClient = banque.getCompteClient(numCompteClient);
                        //String numeroCompteBancaire = cnx.getNumeroCompteActuel();
                        if(noCompteBancaireExpediteur != null && cptClient != null){
                            for(CompteBancaire cptBancaire : cptClient.getComptes()){
                                if(cptBancaire.getNumero().equals(noCompteBancaireExpediteur)){
                                    CompteBancaire cptBancaireDestinataire = banque.getCompteBancaire(noCompteBancaireDestinataire);
                                    if(cptBancaireDestinataire != null){
                                        if(cptBancaire.transferer(montant, noCompteBancaireExpediteur) && cptBancaireDestinataire.crediter(montant)){
                                            cnx.envoyer("TRANSFER OK");
                                        }
                                        else cnx.envoyer("TRANSFER NO");
                                    }

                                }
                            }
                        }
                        else cnx.envoyer("FACTURE NO");

                    }
                    break;

                /******************* HISTORIQUE *******************/
                case "HIST": // Consulter l'historique
                    numCompteClient = cnx.getNumeroCompteClient();
                    banque = serveurBanque.getBanque();
                    argument = evenement.getArgument();
                    if(numCompteClient == null || numCompteClient.isEmpty()){
                        cnx.envoyer("HIST NO");
                    }
                    else{
                        CompteClient cptClient = banque.getCompteClient(numCompteClient);
                        String numeroCompteBancaire = cnx.getNumeroCompteActuel();
                        if(numeroCompteBancaire != null && cptClient != null) {
                            for (CompteBancaire cptBancaire : cptClient.getComptes()) {
                                if (cptBancaire.getNumero().equals(numeroCompteBancaire)) {
                                    cnx.envoyer("HIST OK");
                                    cnx.envoyer(cptBancaire.afficherHistorique());
                                }
                            }
                        }
                        else cnx.envoyer("HIST NO");
                    }
                    break;


                /******************* TRAITEMENT PAR DÉFAUT *******************/
                default: //Renvoyer le texte recu convertit en majuscules :
                    msg = (evenement.getType() + " " + evenement.getArgument()).toUpperCase();
                    cnx.envoyer(msg);
            }
        }
    }
}