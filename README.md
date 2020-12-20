# Mesures de réseaux d'interaction

-->Nous allons analyser un réseau de collaboration scientifique en informatique. Le réseau est extrait de DBLP et disponible sur [SNAP](https://snap.stanford.edu/data/com-DBLP.html).
GraphStream permet de mesurer de nombreuses caractéristiques d'un réseau. La plupart de ces mesures sont implantées comme des méthodes statiques dans la classe [`Toolkit`]
(https://data.graphstream-project.org/api/gs-algo/current/org/graphstream/algorithm/Toolkit.html). Elles vous seront très utiles par la suite.

#### Mesures de base
Après calcul par appel des méthodes de Toolkit, on a obtenu les mesures suivants:

| Mesures  | Valeur  | Signification  |
|---|---|---|
|N   | 317080  | Nombre de noeuds  |
|L   | 1049866  | Nombre de liens  |
| < k >  | 6.622  | Degré moyen  |
| < C >  | 0.632  | Coefficient de clustering  |

Pour un réseau aléatoire de la même taille et du même degré moyen, la coefficient de clustering est d'après la formule:
Ci = < k > / N  qui est : **2.08 E-5**.

La probabilité pour un noeud prix au hasard pour un réseau aléatoire soit connécté à son voisinage est minime par rapport à notre réseau DBLP.

La méthode **Toolkit.isConnected(graph)** nous retourne Vrai, cela nous indique la connexité de notre réseau.  
  
Par contre un réseau aléatoire de la même taille et degré moyen ne sera connexe qu'à partir d'un certain degré moyen **< k >** supérieur à **ln N = 12,66**

  
#### Distribution des degrés  
En utilisant gnuplot pour afficher nos résultat dans le fichier **dd_dblp.dat**, on obtient l'image suivante une échelle linéaire:
![image info](data/dd_dblp_lineaire.png)  
  
En échelle la distribution suivante en échelle Log-Log on pour mieux décrire notre réseau.  
![image info](data/dd_dblp_log_log.png)

*La distribution des degrées indique la probabilité qu'un noeud ait k liens*   


On peut observe ainsi une droite sur cette dernière échelle. Cette droite est en effet la représentation de la **Loi de Puissance** 
qui correspond au mieux à notre distribution de degré.   

```math
    p_k = C k^{-\gamma}
```

On peut en soutirer que notre réseau est un réseau de terrain.

* En estimant la distance moyenne par échantillonnage par un parcours en largeur à partir de 1000 sommets choisis au hasard, on obtient une valeur de 6.832.  
* Ainsi l'hypothèse des **six degrés de séparation** est confirmé. Notre réseau est un petit monde.
* Dans un réseau aléatoire de même caractéristiques, la distance moyenne sera de **6.78**.

##### Distribution des distances 
La distribution des distances indique la probabilité qu'un 
   
![image info](data/plot_distances.png)


### Génération d'un réseau aléatoire avec une méthode d'attachement préférentiel (Barabasi-Albert)
Avec une telle méthode on obtient les mêmes valeurs sauf la connexité et le coefficient de Clustering

## Question 7
 - Le generateur WattsStrogatzGenerator nous reproduit un coefficient de clustering plus petite avec un réseau connexe.
 - Le nombre d'arrêt dépend de la probabilité.

## Propagation dans un réseau
 
 - Le rapport entre beta et mu nous donne le taux de propagation dans le réseau.  
 **beta = 1/7** car un individu envoie un mail par semaine en moyenne  
 **mu = 1/14** car la mise à jour de l'anti-virus est faite deux fois par mois donc 1 fois toutes les deux semaines.  
Ce taux est égale à 2.0.

 - Le seuil épidémique du réseau de collaboration = 0.045
 - Le seuil épidémique du réseau aléatoire = 0.131
 
  - Le seuil épidémique du réseau avec stratégies d'immunisation aleatoire = 0.088
  - Le seuil épidémique du réseau avec stratégies d'immunisation seclective = 0.166

En utilisant gnuplot pour afficher nos résultat dans le fichier **epidemic_first_case.dat**, on obtient l'image suivante une échelle linéaire:
* la première simulation
![image info](data/FirstCaseHist.png)  

* la simulation avec tout les cas
![image info](data/AllCasesHist.png)  