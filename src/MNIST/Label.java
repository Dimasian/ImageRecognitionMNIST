/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MNIST;

/**
 *
 * @author Dimasian
 */
// класс для сопоставления Евклидовых метрик и значений лейбла числа
public class Label implements Comparable<Label>
{
    double metrics;// евклидова метрика
    int lblValue;// значение числа

    public Label(double metrics, int lblValue)
    {
        this.metrics = metrics;
        this.lblValue = lblValue;
    }

    // реализация метода compareTo(Label lbl)
    @Override
    public int compareTo(Label lbl)
    {
        if(this.metrics<lbl.metrics)
        {
            return -1;
        }
        else if(this.metrics==lbl.metrics)
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }
    
}
