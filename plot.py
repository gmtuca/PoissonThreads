import sys
import matplotlib.pyplot as plt
import csv

with open(sys.argv[1], 'rb') as f:
    reader = csv.reader(f)

    i = 0
    for row in reader:
      plt.xlim(0,4097)
      plt.plot(row, 'o')
      plt.savefig('{}.png'.format(i))
      i += 1
      plt.show()
