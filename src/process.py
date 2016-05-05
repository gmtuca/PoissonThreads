import sys
import matplotlib.pyplot as plt
import statistics

def plotandsave(graph):
    plt.errorbar(graph['x'], graph['y'], yerr=graph['yerr'], ecolor='r')
    plt.xlabel('Number of threads')
    plt.ylabel('Performance (op/sec)')
    plt.xticks(range(33)[::2])
    plt.xlim(0,32)
    plt.title('Poisson Equation with Threads')
    plt.savefig('performance.png')
    plt.show()

import csv
with open(sys.argv[1], 'rb') as f:
    reader = csv.reader(f)

    stats = {}
    l = 0
    graph = {'x':[], 'y':[], 'yerr':[]}

    for row in reader:
    	if not row:
            graph['x'].append(l)
            graph['y'].append(statistics.mean(stats[l]))
            graph['yerr'].append(statistics.stdev(stats[l]))
    	else:
            p = int(row[0])
            l = p

            if not stats.get(p):
                stats[p] = []

            stats[p].append(float(row[1]))

    plotandsave(graph)

