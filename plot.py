import sys
import matplotlib.pyplot as plt

import csv
with open(sys.argv[1], 'rb') as f:
    reader = csv.reader(f)

    for row in reader:
      plt.plot(row, 'o')
      plt.show()
