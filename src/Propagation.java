import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class Propagation {
    public static int date = 90;
    public static HashSet<Node> unInfected = new HashSet<>();
    public static HashSet<Node> infected = new HashSet<>();
    public String value = "";

    //La variance
    public static double degVariance(Graph g) {
        int sum = 0;
        for (Node x : g) sum += Math.pow(x.getDegree(), 2);
        return sum / (double) g.getNodeCount();
    }
    //Infection
    public static void infecter(Node n) {
        n.setAttribute("etat_virus", "infecté");
        infected.add(n);
    }
    //Désinfection
    public static void desinfecter(Node n) {
        n.setAttribute("état_virus", "initialisé");
        unInfected.add(n);

    }
    //Réinitialisation
    public static void reset() {
        infected.clear();
        unInfected.clear();
    }

    //Immunisation au hasard
    public static void randomImmunition(Graph graph) {
        unInfected = new HashSet<>(Toolkit.randomNodeSet(graph, graph.getNodeCount() / 2));
        for (Node n : unInfected) {
            unInfecteNode(n);
        }
    }
    //Noeuds ininfecté
    public static void unInfecteNode(Node node) {
        node.setAttribute("état_virus", "initialisé");
        unInfected.add(node);
    }

    //Supprimer les Noeuds infectés
    public static Graph removeInfected(Graph graph) {
        for (Node n : infected) {
            graph.removeNode(n);
        }
        return graph;
    }
    //Immunisation sélective
    public static void selectiveImmunition(Graph graph) {
        ArrayList<Node> unInfectedList = (ArrayList<Node>) Toolkit.randomNodeSet(graph, graph.getNodeCount() / 2);
        double groupe0 = 0, groupe1 = 0;
        double degMoy0 = 0;
        double degMoy1 = 0;
        Random random = new Random();
        for (Node n : unInfectedList) {
            Iterator<Node> iterator = n.getNeighborNodeIterator();
            ArrayList<Node> neighbours = new ArrayList<>();
            while (iterator.hasNext()) neighbours.add(iterator.next());
            int randomNeighboor = (int) Math.floor(Math.random() * neighbours.size());
            desinfecter(neighbours.get(randomNeighboor));
            degMoy0 += n.getDegree();
            groupe0++;
            int selectiveNodeIndex = random.nextInt(n.getDegree());
            Node neighbour = n.getEdge(selectiveNodeIndex).getOpposite(n);
            unInfected.add(neighbour);
            neighbour.addAttribute("etat", "immunisé");
            degMoy1 += neighbour.getDegree();
        }
        degMoy0 = degMoy0 / (graph.getNodeCount() / 2);
        degMoy1 = degMoy1 / (graph.getNodeCount() / 2);
        System.out.println("\ndegré moyen du groupe 0 = " + degMoy0);
        System.out.println("\ndegré moyen du groupe 1 = " + degMoy1);

    }

    public String getValue() {
        return value;
    }

    public void setValeur(String valeur) {
        this.value = valeur;
    }
    //Propagation de l'épidémie
    public Graph spread(Graph g, double beta, double mu) {
        ArrayList<Node> desin = new ArrayList<>();
        ArrayList<Node> inf = new ArrayList<>();
        setValeur("");
        int Size = g.getNodeCount();
        int immuneNumber = unInfected.size();
        for (int i = 0; i < date; i++) {
            for (Node n : infected) {
                Iterator<Node> it = n.getNeighborNodeIterator();
                while (it.hasNext()) {
                    Node voisin = it.next();
                    if (voisin.getAttribute("etat_virus") == "initialisé") {
                        if (Math.random() < beta)
                            inf.add(voisin);
                    }
                }
                if (Math.random() < mu)
                    desin.add(n);
            }
            for (Node n : desin) desinfecter(n);
            desin.clear();
            for (Node n : inf) infecter(n);
            System.out.printf("\njour %d/%d : %d/%d infectés\n ", i, date, infected.size(), (Size - immuneNumber));
            setValeur(getValue() + "\n" + i + "  " + ((double) infected.size()));
        }
        return g;
    }
    //Noeud aléatoire
    public Node randomNode(Graph graph) {
        return Toolkit.randomNode(graph);
    }
    //Infection d'un Noeud aléatoire
    public void infectRandomNode(Graph g) {
        infecter(randomNode(g));
    }
    //Graphe sans infection
    public void withoutInfection(Graph graph) {
        for (Node n : graph.getNodeSet()) {
            n.addAttribute("état_virus", "initialisé");
        }
    }
    //Génération des données
    public void generateData(String file_binomial) {
        try {
            PrintWriter printWriter = new PrintWriter(file_binomial, "UTF-8");
            printWriter.write(getValue());
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}