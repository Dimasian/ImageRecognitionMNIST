/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MNIST;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dimasian
 */
public class MnistRecognizer
{
    String currentDirectory;
    private String TEST_LABELS;
    private String TEST_IMAGES;
    private String TRAIN_LABELS;
    private String TRAIN_IMAGES;
    
    int[] testLabels;// 10000
    List<int[][]> testImages;
    int[] trainLabels;// 60000
    List<int[][]> trainImages;
    int kNearest=0;// число ближайших соседей
    

    Object lockOutput;// monitor для вывода сообщений в консоль
    int cores;// количество ядер доступных для программы
    // делим массив анализируемых изображений на N потоков, по числу доступных ядер.
    int limit;

    public MnistRecognizer()
    {
        currentDirectory = System.getProperty("user.dir")+"\\resources\\";
        
        TEST_LABELS = currentDirectory+"t10k-labels.idx1-ubyte";
        TEST_IMAGES = currentDirectory+"t10k-images.idx3-ubyte";
        TRAIN_LABELS = currentDirectory+"train-labels.idx1-ubyte";
        TRAIN_IMAGES = currentDirectory+"train-images.idx3-ubyte";
        
        LoadData(); // загрузить данные по изображениям в соотвествующие массивы
        // проверить правильность загрузки исходя из размерности массивов
        assertLabelsAndImages(testLabels, testImages);
        assertLabelsAndImages(trainLabels, trainImages);
    }
    
    // загрузить данные по изображениям в соотвествующие массивы
    void LoadData()
    {
        testLabels = MnistReader.getLabels(TEST_LABELS);
        testImages = MnistReader.getImages(TEST_IMAGES);
        trainLabels = MnistReader.getLabels(TRAIN_LABELS);
        trainImages = MnistReader.getImages(TRAIN_IMAGES);
        
    }
    
    // kNearest- сколько ближайших, size - сколько тестовых изображений распознавать (от 0..10000)
    void RunMnist(int kNearest, int size) throws InterruptedException
    {
        lockOutput=new Object();

        // количество ядер доступных для программы
        cores = Runtime.getRuntime().availableProcessors();

        
        System.out.println("Изображений к распознаванию: " + size+" .Потоков: "+cores);
        Mistakes mistakes = new Mistakes(); // кол-во ошибок
        mistakes.value=0;
        Percentage percentage=new Percentage();// общий процент выполнения
        percentage.value=0;

        
        Thread[] threads=new Thread[cores];
        // разделяем общее количество тестируемых изображений по количеству потоков
        for (int i = 0; i < threads.length; i++)
        {
            int begin=i*(size/threads.length);// стартовый индекс подмассива
            int end=(i+1)*(size/threads.length);// конечный индекс подмассива
            threads[i]= new Thread(
                    new MapKNN(
                            lockOutput, // чтобы выводить в консоль
                            size, // общее количество изображений (не использутся)
                            begin, // индекс начала для подмассива
                            end, // индекс конца для подмассива
                            mistakes, // общее кол-во ошибок
                            percentage,// общее кол-во обработанных изображений
                            kNearest, // для сравнения методом К ближайших соседей
                            testLabels, // 10.000 лейблов
                            testImages, // 10.000 изображений для тестирования
                            trainLabels, // 60.000 лейблов
                            trainImages) // 60.000 изображений для обучения
            ) {};
            threads[i].setName("№ "+i);
            threads[i].start();
        }
        
        // основной поток ждет пока закончатся все вспомогательные потоки
         for (int i = 0; i < threads.length; i++)
        {
            threads[i].join();
        }
        
        System.out.print("\nОбработано " + size + " изображений. Ошибок: " + (mistakes.value * 1.0 / size) * 100 + "%.");


    }
    
      
    // проверка, что считанные массивы изображений равны считанным массивам меток по размеру
    // и имеют размер 28 на 28 пикселей.
    public void assertLabelsAndImages(int[] labels, List<int[][]> images) throws RuntimeException
    {
        if(labels.length!=images.size())
        {
            throw new RuntimeException("Размер массива с метками не равен размеру массива с изображениями!");
        }
       if (28!=images.get(0).length)
        {
            throw new RuntimeException("Размер массива с изображениями по строкам не равен 28!");
        }
       if (28!=images.get(0)[0].length)
        {
            throw new RuntimeException("Размер массива с изображениями по столбцам не равен 28!");
        }
    }
}
