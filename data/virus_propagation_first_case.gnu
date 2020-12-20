set terminal png 15
set encoding utf8
set output "histogrammePremierCas.png"
set xrange [0:90]
set yrange [0:317080]
set xlabel "Jours"

plot "premierCas" t "cas 1" with linesp lt 1 pt 1
