#!/usr/bin/python

import matplotlib.pyplot as plt
import numpy as np
import sys


fl = "error.txt"
data = np.loadtxt(fl)
sorted_data = np.sort(data)
yvals = np.arange(len(sorted_data))/float(len(sorted_data))
plt.plot(sorted_data,yvals,label= "train:1 | test:1")

#plt.axis([0,5000,0,1])
plt.xlabel('Error in meters')
plt.ylabel('Cumulative Probability')
plt.legend(loc=4)
plt.title("Cumulitive Distribution Function")
#plt.show()
plt.savefig("Graph_Loc.png", bbox_inches='tight')


