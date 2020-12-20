import org.graphstream.algorithm.Toolkit;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.graph.BreadthFirstIterator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.text.DecimalFormat;
import java.util.List;

public class graphGenerator {

    public static void main(String[] args) {

        // Consigne 6 - Réseau aléatoire

        Graph graph = new SingleGraph("Random");

        Generator gen = new RandomGenerator(6.6221, false, false, "", "poids");

        gen.addSink(graph);

        gen.begin();

        for (int i = 0; i < 317073; i++) gen.nextEvents();

        gen.end();

        DecimalFormat df = new DecimalFormat("0");

        for (Edge e : graph.getEachEdge()) {

            e.setAttribute("poids", df.format((double) e.getAttribute("poids") * 100));

            e.addAttribute("ui.label", "" + e.getAttribute("poids"));

        }

        System.out.println("\nRESEAU ALEATOIRE");
        System.out.println("Nombre de noeuds: " + graph.getNodeCount());
        System.out.println("Nombre de liens: " + graph.getEdgeCount());
        System.out.println("Degré moyen: " + Toolkit.averageDegree(graph));
        System.out.println("Coef de Clustering: " + Toolkit.averageClusteringCoefficient(graph));
        System.out.println("Connexe: " + Toolkit.isConnected(graph));

        int[] liste2 = Toolkit.degreeDistribution(graph);

        try {
            for (int i = 0; i < liste2.length; i++) {

                //System.out.printf(Locale.US, "%6d%20.8f%n", i, (double) liste2[i] / graph.getNodeCount());
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        List<Node> noeuds2 = Toolkit.randomNodeSet(graph, 1000);

        double somme2 = 0.0;

        for (Node source : noeuds2) {

            BreadthFirstIterator bfi2 = new BreadthFirstIterator(source);

            while (bfi2.hasNext()) {

                Node node = bfi2.next();

                somme2 += bfi2.getDepthOf(node);

            }

        }
        double moyenne2 = somme2 / graph.getNodeCount() / noeuds2.size();
        System.out.println("Moyenne - échantillon de 1000 noeuds: " + moyenne2);


        // Consigne 6 - Barabasi-Albert

        Graph graph3 = new SingleGraph("Barabasi-Albert");

        Generator gen3 = new RandomGenerator(6.6221, false, false, "", "poids");

        gen3.addSink(graph3);

        gen3.begin();

        for (int i = 0; i < 317073; i++) gen3.nextEvents();

        gen3.end();

        DecimalFormat df3 = new DecimalFormat("0");

        for (Edge e : graph3.getEachEdge()) {

            e.setAttribute("poids", df.format((double) e.getAttribute("poids") * 100));

            e.addAttribute("ui.label", "" + e.getAttribute("poids"));

        }

        System.out.println("\nRESEAU BARABASI-ALBERT");
        System.out.println("Nombre de noeuds: " + graph3.getNodeCount());
        System.out.println("Nombre de liens: " + graph3.getEdgeCount());
        System.out.println("Degré moyen: " + Toolkit.averageDegree(graph3));
        System.out.println("Coef de Clustering: " + Toolkit.averageClusteringCoefficient(graph3));
        System.out.println("Connexe: " + Toolkit.isConnected(graph3));

        int[] liste3 = Toolkit.degreeDistribution(graph3);

        try {

            for (int i = 0; i < liste3.length; i++) {

                // System.out.printf(Locale.US, "%6d%20.8f%n", i, (double) liste3[i] / graph3.getNodeCount());

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        List<Node> noeuds3 = Toolkit.randomNodeSet(graph3, 1000);

        double somme3 = 0.0;

        for (Node source : noeuds3) {

            BreadthFirstIterator bfi3 = new BreadthFirstIterator(source);

            while (bfi3.hasNext()) {

                Node node = bfi3.next();

                somme3 += bfi3.getDepthOf(node);

            }

        }

        double moyenne3 = somme3 / graph3.getNodeCount() / noeuds3.size();

        System.out.println("Moyenne - échantillon de 1000 noeuds: " + moyenne3);

    }
}
