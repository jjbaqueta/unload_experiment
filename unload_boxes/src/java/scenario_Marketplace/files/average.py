import os 
import numpy as np
import matplotlib.pyplot as plt

from collections import defaultdict
from decimal import Decimal

dir_reports = os.path.dirname(os.path.realpath(__file__)) + '/reports'
dir_charts = os.path.dirname(os.path.realpath(__file__)) + '/charts'

sellers = defaultdict(list)
trust = defaultdict(list)
reputation = defaultdict(list)
image = defaultdict(list)
knowhow = defaultdict(list)
availability = defaultdict(list)

canceledSales = 0
abortedSales = 0

# Functions

def showDict(dict):
    '''
        This function shows the dictionary on the screen
    '''
    for key in dict.keys():
        for element in dict[key]:
            print(element)

def mergeByTime(values, k):
    '''
        This function is used to merge the values of the series based on time.
    '''    
    times = defaultdict(list)

    for data in values[k]:
        times[data[0]].append(round(data[1], 2))

    tList = []
    vList = []

    for time in times.keys():
        tList.append(time)
        vList.append(sum(times[time])/len(times[time]))
    
    return (tList, vList)

def trustChart():
    '''
        This function creates a line chart that shows the thust that the buyers has in the sellers
    '''
    for buyer in trust.keys():
        
        sellersOf = defaultdict(list)
        plt.subplots()

        for data in trust[buyer]:
            seller = data[0]
            product = data[1]
            time = data[2]
            value = data[3]

            key = seller + "-" + product
            sellersOf[key].append((time, value))
        
        for seller in sellersOf.keys():
            vMerge = mergeByTime(sellersOf, seller)

            idx = np.argsort(vMerge[0])
            tList = np.array(vMerge[0])[idx]
            vList = np.array(vMerge[1])[idx]   

            plt.plot(tList, vList, label=seller)
            plt.xticks(range(tList[0], tList[len(tList) - 1] + 1, 2))
    
        plt.title('Trust variation')
        plt.xlabel('Interactions')
        plt.ylabel('Trust values')
        plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left')
        plt.subplots_adjust(left=0.14, right=0.686)
        plt.savefig(dir_charts + '/' + buyer + '_trust.png')
    
def impressionChart(chartType):
    '''
        This function creates a line chart that shows the reputation, image or knowhow of the sellers
        @param chartType: type of chart to be shown
    '''
    fileName = None
    chartTitle = None
    yLabel = None
    impressions = None

    if chartType == 0:
        fileName = '_reputation.png'
        chartTitle = 'Reputation variation'
        yLabel = 'Reputation values'
        impressions = reputation
    elif chartType == 1:
        fileName = '_image.png'
        chartTitle = 'Image variation'
        yLabel = 'Image values'
        impressions = image
    else:
        fileName = '_knowhow.png'
        chartTitle = 'Knowhow variation'
        yLabel = 'Knowhow values'
        impressions = knowhow

    for buyer in impressions.keys():
        
        sellersOf = defaultdict(list)

        for data in impressions[buyer]:
            seller = data[0]
            product = data[1]
            time = data[2]
            
            for criterion in data[3]:
                cName = criterion[0]
                cValue = criterion[1]

                key = seller + "-" + product + "-" + cName
                sellersOf[key].append((time, cValue))
        
        products = defaultdict(list)

        for seller in sellersOf.keys():
            key = seller.split("-")[0] + "-" + seller.split("-")[1]
            criterion = seller.split("-")[2]
            products[key].append((criterion, sellersOf[seller]))

        for product in products.keys():
            plt.subplots()
            times = defaultdict(list)

            for data in products[product]:
                criterion = data[0]
                series = data[1]

                for data in series:
                    times[data[0]].append(round(data[1], 2))

                tList = []
                vList = []

                for time in times.keys():
                    if time > 1:
                        tList.append(time)
                        vList.append(sum(times[time])/len(times[time]))

                idx = np.argsort(tList)
                tList = np.array(tList)[idx]
                vList = np.array(vList)[idx]                

                plt.plot(tList, vList, label=criterion)
                plt.xticks(range(tList[0], tList[len(tList) - 1] + 1, 2))
    
            plt.title(chartTitle + ": " + product)
            plt.xlabel('Interactions')
            plt.ylabel(yLabel)
            plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left')
            plt.subplots_adjust(left=0.114, right=0.755)
            plt.savefig(dir_charts + '/' + buyer + '_' + product + fileName)

def availabilityChart():
    '''
        This function creates a line chart that shows the availability of the sellers
    '''
    for buyer in availability.keys():
        
        sellersOf = defaultdict(list)
        plt.subplots()

        for data in availability[buyer]:
            seller = data[0]
            product = data[1]
            time = data[2]
            value = data[3]

            key = seller + "-" + product
            sellersOf[key].append((time, value))

        for seller in sellersOf.keys():
            vMerge = mergeByTime(sellersOf, seller)

            idx = np.argsort(vMerge[0])
            tList = np.array(vMerge[0])[idx]
            vList = np.array(vMerge[1])[idx]   

            plt.plot(tList, vList, label=seller)
            plt.xticks(range(tList[0], tList[len(tList) - 1] + 1, 2))
    
        plt.title('Availability variation')
        plt.xlabel('Interactions')
        plt.ylabel('Availability values')
        plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left')
        plt.subplots_adjust(left=0.14, right=0.686)
        plt.savefig(dir_charts + '/' + buyer + '_availability.png')

# Main

for entry in os.listdir(dir_reports):
    if os.path.isfile(os.path.join(dir_reports, entry)):
        agent = entry.split("_")[0]
        suffix = entry.split("_")[1]

        inputFile = open(dir_reports + "/" + entry, 'r')

        for line in inputFile:
            time = int(line.split(";")[0])
            content = line.split(";")[1]

            if suffix == 'base.txt':
                if agent[:-1] == 'buyer':
                    if content.split(":")[0][1:] == 'canceled_sales':
                        canceledSales = canceledSales + int(content.split(":")[1][:-2])
                    elif content.split(":")[0][1:] == 'aborted_sales':
                        abortedSales = abortedSales + int(content.split(":")[1][:-2])
                else:
                    if content.split(":")[0][1:] == 'made_sales':
                        sellers[agent].append(int(content.split(":")[1][:-2]))

            elif suffix == 'trust.txt':
                seller = content.split(",")[0][6:]
                product = content.split(",")[1]
                value = Decimal(content.split(",")[2][:-2])
                trust["average"].append((seller, product, time, value))

            elif suffix == 'reputation.txt' or suffix == 'image.txt' or suffix == 'knowhow.txt':
                seller = content.split(",")[1]
                product = content.split(",")[3]
                c1 = content.split(",")[4][2:-1]
                c2 = content.split(",")[5][1:-1]
                c3 = content.split(",")[6][1:-2]
                v1 = Decimal(content.split(",")[7][1:])
                v2 = Decimal(content.split(",")[8])
                v3 = Decimal(content.split(",")[9][:-3])
                
                if suffix == 'reputation.txt':
                    reputation["average"].append((seller, product, time, [(c1, v1), (c2, v2), (c3, v3)]))
                elif suffix == 'image.txt':
                    image["average"].append((seller, product, time, [(c1, v1), (c2, v2), (c3, v3)]))
                else:
                    knowhow["average"].append((seller, product, time, [(c1, v1), (c2, v2), (c3, v3)]))

            elif suffix == 'availability.txt':
                seller = content.split(",")[0][-8:]
                product = content.split(",")[1]
                request = int(content.split(",")[2])
                helping = int(content.split(",")[3][:-2])
                value = helping / request

                availability["average"].append((seller, product, time, value))
                
        inputFile.close()

trustChart()
availabilityChart()
impressionChart(0)
impressionChart(1)
impressionChart(2)