package MNIST;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Dimasian
 */
public class Main
{
 public static void main(String s[])
    {
        try
        {
//            TestMnistReader.test(); // печать изображений чисел на консоль
            MnistRecognizer recognizer=new MnistRecognizer();
            long timer=System.currentTimeMillis();
            try
            {
                recognizer.RunMnist(5, 1000);// (5- k Ближайших, 1000 -  количество изображений для теста. Маск. 10.000. Но на 1000 изобр. уже 94 сек.)
            } catch (InterruptedException ex)
            {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);// сгененрировано NEtBeans IDE
            }
            timer=System.currentTimeMillis()-timer;
            
            System.out.println("\nВремя выполнения: "+ timer/1000 +" сек.");
        } catch (RuntimeException e)
        {
            System.out.print(e);
        }
    }
    
}


