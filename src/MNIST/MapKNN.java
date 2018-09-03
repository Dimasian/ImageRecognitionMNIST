/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MNIST;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dimasian
 */
public class MapKNN extends Thread
{
    int size;// сколько всего изображений вычисляет программа
    int begin;
    int end;
    Mistakes mistakes;// Общеее число ошибок
    Percentage percentage;// общее число обработанных изображений
    int kNearest=0;// число ближайших соседей
    Object lockOutput;
    

    
    int[] testLabels;// 10000
    List<int[][]> testImages;
    int[] trainLabels;// 60000
    List<int[][]> trainImages;

    public MapKNN(
            Object lockOutput,
            int size,
            int begin,
            int end,
            Mistakes mistakes, 
            Percentage percentage,
            int kNearest,
            int[] testLabels, 
            List<int[][]> testImages, 
            int[] trainLabels, 
            List<int[][]> trainImages)
    {
        this.lockOutput=lockOutput;
        this.size=size;
        this.begin = begin;
        this.end=end;
        this.mistakes = mistakes;
        this.percentage=percentage;
        this.kNearest=kNearest;
        this.testLabels = testLabels;
        this.testImages = testImages;
        this.trainLabels = trainLabels;
        this.trainImages = trainImages;
    }
    

    @Override
    public void run()
    {
        long timer;
        // для каждого тестового изображения
        for (int i = begin; i < end; i++)
        {
            
            // Массив евклидовых метрик и значений лейблов для одного изображения
            Label[] euclMap = new Label[trainLabels.length];
  
//            timer=System.currentTimeMillis();
            // Вычисляем евклидовы метрики для текущего изображения и записываем результаты в массив Label[]
            // Label (Double euclidMetrics, int labelValue)
            mapKNN(testImages.get(i), euclMap);
//            timer=System.currentTimeMillis()-timer;
//            synchronized(lockOutput)
//            {
//                System.out.println("\nВремя выполнения 'mapKNN': "+ String.format("%.2f",timer*1.0/1000) +" сек.");
//            }
                
            
//            timer=System.currentTimeMillis();
            // отбираем k первых
            Label[] labels = mapKNearest(euclMap, kNearest); 
//            timer=System.currentTimeMillis()-timer;
//              synchronized(lockOutput)
//            {
//            System.out.println("\nВремя выполнения 'mapKNearest': "+ String.format("%.2f",timer*1.0/1000) +" сек.");
//            }  
            
//            timer=System.currentTimeMillis();
            // подсчитываем наиболее часто встречаемый элемент среди k первых
            int recognized = mostCommonLabel(labels);
//            timer=System.currentTimeMillis()-timer;
//              synchronized(lockOutput)
//            {
//            System.out.println("\nВремя выполнения 'mostCommonLabel': "+ String.format("%.2f",timer*1.0/1000) +" сек.");
//            }
              
            double prcnt;
            synchronized(percentage)
            {
                percentage.value++;
                // Процент выполнения. 
                prcnt = (percentage.value*1.0/size) * 100;
            }

            
            String s="Поток : "+Thread.currentThread().getName()+String.format(". Обработано: %.2f", prcnt)+"%. ";
            if (recognized == testLabels[i])
                {
                    s+="OK: " + recognized + " = " + testLabels[i];
                } else
                {
                    synchronized(mistakes)
                    {
                        mistakes.value++;
                    }
                    s+="Распознано неверно: " + recognized + " != " + testLabels[i];
                }
            synchronized(lockOutput)
            {
                System.out.print("\n"+s);// если тут указать возврат каретки, то строка плохо обновляется
                
            }
            
        }
    }
    
    
    // Вычисляем массив расстояний по Евклидовой метрике для одного переданного изображения
    void mapKNN(int[][] img, Label[] euclMap)
    {
        for (int i = 0; i < trainImages.size(); i++)
        {
            double euclMetric=EuclidMetrics(img, trainImages.get(i));

                euclMap[i]=new Label(euclMetric,trainLabels[i]);
        }
    }
   
    // Вычисление Евклидовой метрики для двух изображений       
    double EuclidMetrics(int[][] a, int[][]b)
    {
        int dist=0;// (a1-b1)^2+...+(an-bn)^2
        
        for (int i = 0; i < a.length; i++)
        {
            for (int j = 0; j < a[i].length; j++)//массив квадратный 28x28
            {
                dist+=Math.pow(a[i][j]-b[i][j],2);
            }
        }
        return Math.sqrt(dist);
    }
    
    // Подсчитываем, сколько каких лейблов имеют наименьшее значение по евклидовой метрике
    Label[] mapKNearest(Label[] euclMap, int kNearest)
    {
        Label[] labels=new Label[kNearest];
        for (int i = 0; i < kNearest; i++)
        {
            labels[i]=euclMap[0];// все минимальные элементы = нулевому
        }
        
            for (int j = 1; j < euclMap.length-1; j++)// перебираем весь массив
            {
                for (int k = 0; k < kNearest; k++)// для каждого элемента из результирующего массива
                {
                    // если новый элемент меньше текущего в результирующем массиве, обновляем последний.
                    if(euclMap[j].compareTo(labels[k])<0)
                    {
                        labels[k]=euclMap[j];
                        // смещаем все элементы массива вправо
                        for (int l = kNearest-1; l > k; l--)
                        {
                            labels[l]=labels[l-1];
                        }
                        break;
                    }
                }
                
            }
        return labels;
    }
    
    // Вернуть элемент с наибольшим значением вхождений 
    Integer mostCommonLabel(Label[] labels)
    {
        int lblValue=-1;
        int maxCount=-1;
        
        // SortedMap<LabelValue, CountLabels>
        SortedMap<Integer,Integer> sMap=new TreeMap<>();
        
        for (int i = 0; i < labels.length; i++)
        {
            sMap.put(labels[i].lblValue,1);// для одинаковых значений увеличиваем счетчик
        }
        
        // ищем максимальное значение в SortedMap<LabelValue, CountLabels>
        for (Map.Entry<Integer, Integer> entry : sMap.entrySet())
        {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            if(value>maxCount)
            {
                maxCount=value;
                lblValue=key;
            }
        }
        
        return lblValue;
    }
    
}
