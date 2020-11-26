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
            times = []
            values = []

            for data in sellersOf[seller]:
                times.append(data[0])
                values.append(round(data[1], 2))

            plt.plot(times, values, label=seller)
            plt.xticks(range(times[0], times[len(times) - 1] + 1, 1)) 
    
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

            for data in products[product]:
                criterion = data[0]
                series = data[1]

                times = []
                values = []

                for serie in series:
                    times.append(serie[0])
                    values.append(round(serie[1],2))

                plt.plot(times, values, label=criterion)
                plt.xticks(range(times[0], times[len(times) - 1] + 1, 1))                
    
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
            times = []
            values = []

            for data in sellersOf[seller]:
                times.append(data[0])
                values.append(round(data[1], 2))

            plt.plot(times, values, label=seller)
            plt.xticks(range(times[0], times[len(times) - 1] + 1, 1)) 
    
        plt.title('Availability variation')
        plt.xlabel('Interactions')
        plt.ylabel('Availability values')
        plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left')
        plt.subplots_adjust(left=0.14, right=0.686)
        plt.savefig(dir_charts + '/' + buyer + '_availability.png')

def salesChart():
    '''
        This function creates a stacked chart about the amount of made sales
    '''
    seriesNames = []
    seriesValues = []
    p = []
    N = 1
    y = 0
    ind = np.arange(N)
    width = 0.35

    for seller in sellers.keys():
        seriesNames.append(seller)
        seriesValues.append(sellers[seller][0])
    
    seriesNames.append('Canceled')
    seriesValues.append(canceledSales)

    seriesNames.append('Aborted')
    seriesValues.append(abortedSales)

    for value in seriesValues:
        if y == 0:
            p.append(plt.bar(ind, value, width))
            y = y + value
        else:
            p.append(plt.bar(ind, value, width, bottom = y))
            y = y + value

    plt.ylabel('Number of sales')
    plt.xlabel('Kind of sales')
    plt.title('Sales Report')
    plt.xticks([])
    plt.yticks(np.arange(0, y, 1))
    plt.legend(p, seriesNames, bbox_to_anchor=(1.05, 1), loc='upper left')
    plt.subplots_adjust(left=0.4, right=0.61)
    plt.savefig(dir_charts + '/sales.png')

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
                trust[agent].append((seller, product, time, value))

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
                    reputation[agent].append((seller, product, time, [(c1, v1), (c2, v2), (c3, v3)]))
                elif suffix == 'image.txt':
                    image[agent].append((seller, product, time, [(c1, v1), (c2, v2), (c3, v3)]))
                else:
                    knowhow[agent].append((seller, product, time, [(c1, v1), (c2, v2), (c3, v3)]))

            elif suffix == 'availability.txt':
                seller = content.split(",")[0][-7:]
                product = content.split(",")[1]
                request = int(content.split(",")[2])
                helping = int(content.split(",")[3][:-2])
                value = helping / request

                availability[agent].append((seller, product, time, value))
                
        inputFile.close()

salesChart()
trustChart()
availabilityChart()
impressionChart(0)
impressionChart(1)
impressionChart(2)