set terminal pngcairo enhanced font 'Verdana, 10'
set encoding utf8
set output "AllCasesHist.png"
set xrange [0:90]
set yrange [0:317080]
set xlabel "jours"
set ylabel "Infectés"

plot "premierCas" t "Sans empechement" with linesp lt 1 pt 1, "deuxiemeCas" t "Immunisation aléatoire" with linesp lt 2 pt 2, "troisiemeCas" t "Immunisation sélective" with linesp lt 3 pt 3
