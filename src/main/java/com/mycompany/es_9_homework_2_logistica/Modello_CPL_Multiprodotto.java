/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.es_9_homework_2_logistica;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloObjective;
import ilog.concert.IloObjectiveSense;
import ilog.cplex.IloCplex;

/**
 *
 * @author diego
 */
public class Modello_CPL_Multiprodotto {

    protected IloCplex modello;
    protected double[][] c1ij;
    protected double[][] c2ij;
    protected double[] cf;// costi fissi giornalieri
    protected double[] capacità;// capacità giornaliere
    protected double[][] domande;// domande giornaliere
     int numero_siti_potenziali;
    int numero_siti_da_servire;
    int numero_prodotti;
    protected IloIntVar[] y;
 //NOTA: questo problema, poichè adopera un grafo completo A= V1 x V2,
//fa uso delle matrici per modellare le variabili associate agli archi (i,j) anzichè la classe jung graph, poichè non avremo elementi vuoti
    protected IloNumVar[][] x1;// variabili relative alla quantità di prodotto 1
    protected IloNumVar[][] x2;// variabili relative alla quantità di prodotto 2

    public Modello_CPL_Multiprodotto(double[][] C1ij, double[][] C2ij, double[] CF, double[] Q, double[][] dom, int n, int m, int p) throws IloException {
        this.modello = new IloCplex();// assegnamo il modello del costruttore ad un nuovo modello
        this.c1ij = C1ij;
        this.c2ij = C2ij;
        this.cf = CF;
        this.capacità = Q;
        this.domande = dom;
        this.numero_siti_potenziali = n;
        this.numero_siti_da_servire = m;
        this.numero_prodotti = p;
        this.y = new IloIntVar[n];
        this.x1 = new IloNumVar[n][m];
        this.x2 = new IloNumVar[n][m];

    }

    protected void addVariables() throws IloException {
        // COMINCIAMO CON DEFINIRE LE VARIABILI   
        for (int j = 0; j < numero_siti_potenziali; j++) { // definizione variabili y
            int pos_j = j + 1;
             y[j]= modello.boolVar("y[" + pos_j + "]");// definiamo campo esistenza variabili, variabile intere definita in [0,1], ovvero binaria
           // y[j] = modello.intVar(0, 1, "y[" + pos_j + "]");// definiamo campo esistenza variabili, variabile intere definita in [0,1], ovvero binaria
        }

        for (int i = 0; i < numero_siti_potenziali; i++) { // definizione variabili x1
            for (int j = 0; j < numero_siti_da_servire; j++) {
                int pos_i = i + 1;
                int pos_j = j + 1;
                x1[i][j] = modello.numVar(0, 1, IloNumVarType.Float, "x1[" + pos_i + "][" + pos_j + "]");// definiamo campo esistenza variabili
            }
        }

        for (int i = 0; i < numero_siti_potenziali; i++) { // definizione variabili x2
            for (int j = 0; j < numero_siti_da_servire; j++) {
                int pos_i = i + 1;
                int pos_j = j + 1;
                x2[i][j] = modello.numVar(0, 1, IloNumVarType.Float, "x2[" + pos_i + "][" + pos_j + "]");// definiamo campo esistenza variabili
            }
        }

    }

    protected void addObjective() throws IloException {
        IloLinearNumExpr obiettivo = modello.linearNumExpr();// creiamo un oggetto espressione che contenga la funzione obiettivo

        //AGGIUNGIAMO PARTE DEI COSTI FISSI
        for (int j = 0; j < numero_siti_potenziali; j++) {
        obiettivo.addTerm(y[j],cf[j]);// stiamo aggiungendo il termine f_i*y_i
        }

        // AGGIUGIAMO PARTE RELATIVA ALLE VARIABILI X1    
        for (int i = 0; i < numero_siti_potenziali; i++) {
            for (int j = 0; j < numero_siti_da_servire; j++) {
                obiettivo.addTerm(x1[i][j], c1ij[i][j]);// stiamo aggiungendo il termine c_1ij*x_1ij

            }
        }

        // AGGIUGIAMO PARTE RELATIVA ALLE VARIABILI X2   
        for (int i = 0; i < numero_siti_potenziali; i++) {
            for (int j = 0; j < numero_siti_da_servire; j++) {
                obiettivo.addTerm(x2[i][j], c2ij[i][j]);// stiamo aggiungendo il termine c_2ij*x_2ij

            }
        }
        IloObjective Obj = modello.addObjective(IloObjectiveSense.Minimize, obiettivo);
    }

    protected void addConstraints() throws IloException {
// Vincolo soddisfacimento clienti per prodotto 1
for (int j = 0; j < numero_siti_da_servire; j++) {
        IloLinearNumExpr vincolo_servizio_clienti1 = modello.linearNumExpr();
            for (int i = 0; i < numero_siti_potenziali; i++) {
                vincolo_servizio_clienti1.addTerm(x1[i][j], 1); // creiamo il vincolo nella parte destra somma delle x1ij
            }
            modello.addEq(vincolo_servizio_clienti1, 1);// aggiungiamo il vincolo 
        }

// Vincolo soddisfacimento clienti per prodotto 2
       for (int j = 0; j < numero_siti_da_servire; j++) {
            IloLinearNumExpr vincolo_servizio_clienti2 = modello.linearNumExpr();
            for (int i = 0; i < numero_siti_potenziali; i++) {
                vincolo_servizio_clienti2.addTerm(x2[i][j], 1); // creiamo il vincolo nella parte destra somma delle x2ij
            }
            modello.addEq(vincolo_servizio_clienti2, 1);// aggiungiamo il vincolo 
        }

        // Vincoli capacità e logico per i siti potenziali
        // (\sum_p \sum_j d_pj*x_pij ) - y_i*f_i<=0
        for (int i = 0; i < numero_siti_potenziali; i++) {
            IloLinearNumExpr vincolo_capacità = modello.linearNumExpr();
            vincolo_capacità.addTerm(y[i],-capacità[i]);// aggiungiamo termine - y_i*f_i
            for (int p = 0; p < numero_prodotti - 1; p++) {// NOTA: p=0 e stop
                for (int j = 0; j < numero_siti_da_servire; j++) {
                   vincolo_capacità.addTerm(domande[p][j], x1[i][j]);
                   vincolo_capacità.addTerm(domande[p+1][j], x2[i][j]);
                    // aggiungiamo termine x_1ij*d_pj
                    
                }
              modello.addLe(vincolo_capacità, 0);
            }
        }
    }

    public void risolviModello() throws IloException {
        boolean condizione = Verifica_Condizione_Ammissibilità();
        if (condizione == true) {
            addVariables();
            addObjective();
            addConstraints();
            modello.exportModel("Multicommodity_Capacitated_Plant_Location_Problem.lp");

            modello.solve();// questo metodo risolve il problema
            System.out.println();
        System.out.println("Solution status = "+ modello.getStatus());
        System.out.println();
            double domanda_totale = 0;
            double capacità_totale = 0;
            for (int j = 0; j < numero_siti_potenziali; j++) {
                capacità_totale += capacità[j];
            }
            for (int p = 0; p < numero_prodotti; p++) {
                for (int j = 0; j < numero_siti_da_servire; j++) {
                    domanda_totale += domande[p][j];
                }
            }
            System.out.println("La domanda totale e'=" + domanda_totale);
            System.out.println("La capacita' totale e'=" + capacità_totale);

            System.out.println("Il valore di funzione obiettivo e':" + modello.getObjValue());
            // System.out.println("Lo stato del modello è" + modello.getStatus())
            System.out.println("Il valore delle variabili y e':");

            for (int i = 0; i < y.length; i++) {
                int pos_i = i + 1;
                System.out.print("y[" + pos_i + "]=" + modello.getValue(y[i]));
                System.out.println();
            }

            System.out.println("Il valore delle variabili x1 e':");

            for (int i = 0; i < x1.length; i++) {
                for (int j = 0; j < x1[0].length; j++) {
                    System.out.println(x1[i][j].getName() +"="+ modello.getValue(x1[i][j]));
                }
            }
            System.out.println("Il valore delle variabili x2 e':");
            for (int i = 0; i < x2.length; i++) {
                for (int j = 0; j < x2[0].length; j++) {
                    System.out.println(x2[i][j].getName()+ "=" + modello.getValue(x2[i][j]));
                }
            }
        } 
        else {
            System.out.println("Condizione ammissibilità preliminare non soddisfatta");
        }
    }

   public boolean Verifica_Condizione_Ammissibilità() {
        double domanda_totale = 0;
        double capacità_totale = 0;
        for (int j = 0; j < numero_siti_potenziali; j++) {
            capacità_totale += capacità[j];
        }
        for (int p = 0; p < numero_prodotti; p++) {
            for (int j = 0; j < numero_siti_da_servire; j++) {
                domanda_totale += domande[p][j];

            }
        }
        if (domanda_totale <= capacità_totale) {
            return true;
        } 
        else {
            return false;
        }
    }

}
