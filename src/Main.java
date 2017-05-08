import com.sun.javafx.scene.layout.region.Margins;
import com.sun.org.apache.regexp.internal.RE;
import java.io.*;
import java.io.CharArrayWriter;
import java.rmi.MarshalException;
import java.util.Scanner;

public class Main {

    private static String[] Functions_array={"ABS", "SQRT", "SQR", "EBP", "LN","ARCSIN","ARCCOS", "SIN", "COS", "ARCTAN","COTAN","TAN","LG"};
    private static char[] Operations_array = {'^', '/', '*', '+', '-'};

    private char MAS1[];

    public static void main(String[] args)
    {
        while (true)
        {
        Scanner scan=new Scanner(System.in);
        System.out.println("Введіть вираз");
        String expresssion = scan.next();
        if (expresssion=="exit")
            break;
        System.out.println("Введіть значення Х");
        Double value = scan.nextDouble();
        System.out.println(get_Y(expresssion, value));
        }
    }

    static double get_Y(String f, double X)
    {
        String str;
        int i=0, p;
        f = probil_del(f);
        str = substitute_value(f.toUpperCase(), X);
        str = pi_detected(str);
        //str = krapka(str);
        str = calculate_function(str, Functions_array, Operations_array);
        str = open_all_bracket(str, Operations_array);
        while (true)
        {
            str = take_action(str, Operations_array[i]);
            p = new String(str).indexOf(Operations_array[i]);
            if (p <= 0)
            {
                if (i==Operations_array.length-1 || p==0)
                    break;
                else i++;
            }

        }
        return Double.parseDouble(str);
    }
        static String pi_detected(String str)
        {
            int pi_pos,e_pos;

            pi_pos = str.indexOf("PI");
            while (pi_pos > 0)
            {
                str = new StringBuilder(str).replace(pi_pos,2,"3,14159").toString();
                pi_pos = str.indexOf("PI");
            }
            e_pos=str.indexOf("E");
            while (e_pos > 0)
            {
                str = new StringBuilder(str).replace(pi_pos,1,"2,718281").toString();
                e_pos = str.indexOf("E");
            }
            return str;
        }

    static String krapka(String str)
    {
        int krap;
        krap = str.indexOf(".");
        while (krap > 0)
        {
            str = new StringBuilder(str).replace(krap, 1,",").toString();
            krap = str.indexOf(".");
        }
        return str;
    }

    String koma(String str)
    {
        int krap;
        krap = str.indexOf(",");
        while (krap > 0)
        {
            str = new StringBuilder(str).replace(krap, 1,".").toString();
            krap = str.indexOf(",");
        }
        return str;
    }


    static String probil_del(String str)
    {
        int probil;
        probil = str.indexOf(" ");
        while (probil > 0)
        {
            str = new StringBuilder(str).replace(probil, probil+1,"").toString();
            probil = str.indexOf(" ");
        }
        return str;
    }

    static String Remove_symbols(String str)
    {
        int Str_lenght=str.length()-1;
        for (int i=0;i<Str_lenght-1;i++)
        {
            if (i==0)
            {
                if (str.toCharArray()[0]=='+' || str.toCharArray()[0]=='+')
                    str=new StringBuilder(str).replace(0,1,"").toString();
            }
            if (str.toCharArray()[i]=='+' && str.toCharArray()[i+1]=='-' ||
                    str.toCharArray()[i]=='-' && str.toCharArray()[i+1]=='+')
                str = new StringBuilder(str).replace(i,i+2,"-").toString();
            if (str.toCharArray()[i]=='+' && str.toCharArray()[i+1]=='+' ||
                    str.toCharArray()[i]=='-' && str.toCharArray()[i+1]=='-')
                str = new StringBuilder(str).replace(i,i+2,"+").toString();
        }
        return str;
    }

    static String take_action(String str, char action)
    {
        str = Remove_symbols(str);
        int Str_lenght=str.length()-1;
        for (int i=Str_lenght; i>=0; i--)
        {
            int Symbol_pos=Integer.MIN_VALUE;
            int Value_one=Integer.MIN_VALUE;
            int Value_two=Integer.MIN_VALUE;
            Double First_value=Double.MIN_VALUE;
            Double Second_value=Double.MIN_VALUE;
            Double Third_value=Double.MIN_VALUE;
            String Result="";
            if (str.toCharArray()[i]==action &&
                    i>0)
            {
                Symbol_pos = i;
                Value_one=Symbol_pos+1;
                Second_value=Double.parseDouble(str.substring(Value_one,str.length()));
                for (int j=Symbol_pos-1;j>=0;j--)
                {
                    String All_operations=new String(Operations_array);
                    String Operations=new String(new char[]{'+','-'}).toString();
                    if (All_operations.contains(""+str.toCharArray()[j]))
                    {
                        if (Operations.contains(""+str.toCharArray()[j]))
                         Value_two=j;
                        else Value_two=j+1;
                         break;
                    }
                }
                if (Value_two==Integer.MIN_VALUE)
                    Value_two=0;
                First_value=Double.parseDouble(str.substring(Value_two,Symbol_pos));
                switch (action)
                {
                    case '^' : try
                    {
                        if (Math.abs(First_value) == 0)
                            Result =  Float.toString(0);
                        if (Math.abs(Second_value) == 0)
                            Result =  Float.toString(1);
                        if ((First_value > 0) && (Second_value > 0))
                        {
                            Third_value = Math.exp(Second_value * Math.log(First_value));
                            Result = Double.toString(Third_value);
                        }
                        if ((First_value < 0) && (Second_value < 0))
                        {
                            Third_value = Math.exp(Second_value* Math.log(Math.abs(First_value)));
                            Result = '-' +  Double.toString(Third_value);
                        }
                        if (((First_value > 0) && (Second_value < 0)) || ((First_value < 0) && (Second_value > 0)))
                        {
                            Third_value = Math.exp(Second_value * Math.log(Math.abs(First_value)));
                            if (((Second_value.intValue()) % 2 > 0) || (Third_value<0))
                                Result = '-' + Double.toString(Third_value);
                            else
                                Result =Double.toString(Third_value);
                        }
                    }
                    catch(Exception e)
                    {
                        System.out.print("ERROR");
                        System.exit(0);
                    }
                        break;
                    case '*' :
                        Third_value = First_value * Second_value;
                        Result =Double.toString(Third_value);
                        break;
                    case '/' :
                        if (Second_value!=0)
                        {
                            Third_value = First_value / Second_value;
                            Result = Double.toString(Third_value);
                        }else  {
                            System.exit(0); }
                        break;
                    case '+' :

                        Third_value = First_value + Second_value;
                        Result =Double.toString(Third_value);
                    break;
                    case '-' :

                        Third_value = First_value - Second_value;
                        Result = Double.toString(Third_value);
                        break;
                }
                if (Third_value>=0) Result="+"+ Result;
                str=new StringBuilder(str).replace(Value_two,str.length(),Result).toString();
                Str_lenght=str.length()-1;
                if (i>=Str_lenght) break;
            }
        }
        return str;
    }


    static String take_action_old(String str, char action)
    {
        int i, k1, k2=0, count1, count2, p,hh,jj=0,fl_pos;
        double val1, val2, val;
        char operat[];
        String res="",temp_fl,Temp_mn;
        for (i = str.length()-1; i>1; i--)
        {
            if ((str.toCharArray()[i] =='+') && (str.toCharArray()[i-1] =='-'))
                str=new StringBuilder(str).replace(i-1, i-1+2, "-").toString();
            if ((str.toCharArray()[i] =='-') && (str.toCharArray()[i-1] =='-'))
                str=new StringBuilder(str).replace(i-1,  i-1+2, "+").toString();
            if ((str.toCharArray()[i] =='+') && (str.toCharArray()[i-1] =='+'))
                str=new StringBuilder(str).replace(i-1,  i-1+2, "+").toString();
            if ((str.toCharArray()[i] =='-') && (str.toCharArray()[i-1] =='+'))
                str=new StringBuilder(str).replace(i-1,  i-1+2, "+").toString();
        }
        char string[]=str.toCharArray();
        count1 = 0;
        for (i = str.length()-1; i>0; i--)
        {
            count1 = count1 + 1;
            if ((str.toCharArray()[i] == action) && (i > 0))
            {
                count1--;
                k1 = i;
                char chr[]={'*', '/', '+', '-', '^'};
                if ( string[k1-1] == '-')
                {
                    k1 = k1 - 1;
                }
                val1 = Float.parseFloat(new StringBuilder(str).substring( i+1, str.length()));
                count2 = 0;
                k2=i-1;
                operat = operat = new char[]{'*', '/', '+','-','^'};
                string=str.toCharArray();
                while (true)
                {
                    count2 = count2 + 1;
                    if (k2 <0 || new String(operat).contains(Character.toString(string[k2])))
                    {
                        if (string[k2]=='-')
                            k2--;
                        break;
                    }
                    k2--;
                }
                val2 = Float.parseFloat(new StringBuilder(str).substring(k2+1,i));
                if (str.indexOf("+3+3")>0)
                    jj++;
                switch (action)
                {
                    case '^' : try
                    {
                        if (Math.abs(val1) == 0)
                            res =  Float.toString(0);
                        if (Math.abs(val2) == 0)
                            res =  Float.toString(1);
                        if ((val1 > 0) && (val2 > 0))
                        {
                            val = Math.exp(val2 * Math.log(val1));
                            res = Double.toString(val);
                        }
                        if ((val1 < 0) && (val2 < 0))
                        {
                            val = Math.exp(val2* Math.log(Math.abs(val1)));
                            res = '-' +  Double.toString(val);
                        }
                        if (((val1 > 0) && (val2 < 0)) || ((val1 < 0) && (val2 > 0)))
                        {
                            val = Math.exp(val2 * Math.log(Math.abs(val1)));
                            if ((((int)val2) % 2 > 0) || (val<0))
                            res = '-' + Double.toString(val);
                           else
                            res =Double.toString(val);
                        }
                    }
                    catch(Exception e)
                    {
                        System.out.print("ERROR");
                        System.exit(0);
                    }
                        break;
                    case '*' :
                        val = val1 * val2;
                        res =Double.toString(val);
                    break;
                    case '/' :
                        if (val2!=0)
                        {
                            val = val1 / val2;
                            res = Double.toString(val);
                        }else  {
                            System.exit(0); }
                        break;
                    case '+' :

                        val = val1 + val2;
                        char chars[]={'0','1','2','3','4','5','6','7','8','9'};
                    {if (new String(chars).contains(Character.toString(string[k1])))

                        res ='+'+Double.toString(val);
                    else
                        res =Double.toString(val);
                    }
                    break;
                    case '-' :

                        val = val1 - val2;
                        res = Double.toString(val);
                        break;
                }
                str =  new StringBuilder(str).replace(k2+1, str.length(), res).toString();
                i=str.length()-1;
                count1=count2=k1=k2=0;
            }
        }
        return str;
    }

    static String open_bracket(String str, char[] operation)
    {
        int i, j, pos1=0, pos2=0, count, k,Brackets=2;
        String str1;
        for (i = str.length()-1; i> 1;i--)
            if (str.toCharArray()[i] == ')')
                pos1 = i;
        count = 0;
        for ( j = pos1; j>1;j--)
        {
            if (str.toCharArray()[j] == '(')
            {
                pos2 = j;
                break;
            }
            count = count + 1;
        }
        //str1 =   new StringBuilder(str).substring(pos1-count, count+1);
        if (pos2==0 && pos1==str.length()-1)
        {
            str1 =   new StringBuilder(str).substring(1,str.length()-1);
            pos2=0;
            Brackets=2;
        }
        else
        str1 =   new StringBuilder(str).substring(pos2+1,pos1);
        String operators=new String(Operations_array);
        int Replace_length=str1.length();
        String  Operations=new String(Operations_array);
            for (k = 0; k< operation.length-1;k++)
                str1 = take_action(str1, operation[k]);
            str =  new StringBuilder(str).replace(pos2, pos2+Replace_length+Brackets, str1).toString();
            str=Remove_symbols(str);
        for (char symbol:str.toCharArray())
        {
            if (Operations.contains(String.valueOf(symbol)))
            {
                if (str.toCharArray()[0]!='(' && str.toCharArray()[str.length()-1]!=')')
                str=open_all_bracket("("+str+")",Operations_array);
                else str=open_all_bracket(str,Operations_array);
                break;
            }
        }
        return str;
    }

    static String open_all_bracket(String str, char[] operation)
    {
        int i=0;
        while (true)
        {
            if (i >= str.length()-1) break;
            if (new String(new char[]{')', '('}).contains(Character.toString(str.toCharArray()[i])))
            {
                str = open_bracket(str, operation);
                i=1;
            }
            i = i + 1;
        }
        return str;
    }

    static String calculate_function(String str, String[] func, char[] operation) {
        int position0, position1, position2, i, count, p, k, z1, z2, j;
        String str1, temp_fl, temp_mn;
        double value=0;
        int fl_pos, hh;
        char operat[];
        for (k = 0; k < func.length-1; k++) {
            position0 = new StringBuilder(str).indexOf(func[k]);
            if (position0 >= 0) {
                count = 0;
                position1 = 0;
                z1 = 0;
                position2 = 0;
                z2 = 0;
                for (i = position0 + func[k].length(); i < str.length(); i++) {
                    if (str.toCharArray()[i] == '(')
                    {
                        if (position1 == 0) position1 = i;
                        z1 = z1 + 1;
                        count = count + 1;
                    }
                        if (str.toCharArray()[i] == ')') {
                            position2 = i;
                            z2 = z2 + 1;
                            if (z2 == z1) break;
                        }
                    }
                    str1 =new StringBuilder(str).substring(position1, position2-position1+position1+1);

                    for (j = 0; j < func.length-1; j++) {
                        p = new StringBuilder(str).indexOf(func[j]);
                        if (p >= 0)
                            str1 = calculate_function(str1, func, operation);
                    }
                    str1 = open_all_bracket(str1, operation);
                    try {
                        switch (k) {
                            case 0:
                                value = Math.abs(Float.parseFloat(str1));
                                break;
                            case 1:
                                value = Math.sqrt(Float.parseFloat(str1));
                                break;
                            case 2:
                                value = Math.pow(Float.parseFloat(str1),2);
                                break;
                            case 3:
                                value = Math.exp(Float.parseFloat(str1));
                                break;
                            case 4:
                                value = Math.log(Float.parseFloat(str1));
                                break;
                            case 5:
                                value = Math.asin(Float.parseFloat(str1));
                                break;
                            case 6:
                                value = Math.acos(Float.parseFloat(str1));
                                break;
                            case 7:
                                value = Math.sin(Float.parseFloat(str1));
                                break;
                            case 8:
                                value = Math.cos(Float.parseFloat(str1));
                                break;
                            case 9:
                                value = Math.tan(Float.parseFloat(str1));
                                break;
                            case 10:
                                if (Float.parseFloat(str1) != 0)
                                    value = Math.atan(Float.parseFloat(str1));
                                break;
                            case 11:
                                if (Float.parseFloat(str1) != 0)
                                    value = Math.tan(Float.parseFloat(str1));
                                break;
                            case 12:
                                value = Math.log10(Float.parseFloat(str1));
                                break;
                        }
                    } catch (Exception e) {
                    }
                    for (i = 1; i < String.valueOf(value).length()-1; i++) {
                        fl_pos = 0;
                        fl_pos = new StringBuilder(String.valueOf(value)).indexOf("E-");
                        if (fl_pos > 0) {
                            temp_fl = String.valueOf(value);
                            temp_mn = (new StringBuilder(temp_fl).substring(fl_pos + 2, temp_fl.length()-1 - fl_pos + 1));
                            temp_fl = new StringBuilder(temp_fl).delete(fl_pos, temp_fl.length()-1 - fl_pos).toString();
                            for (hh = 1; hh < (int)(Float.parseFloat(temp_mn)); hh++)
                                value = value / 10;
                        }
                    }
                    value = Float.parseFloat(String.valueOf(value));
                    if (position0 != 0) {
                        if (str.toCharArray()[position0 - 1] == '-' && value < 0)
                            str = new StringBuilder(str).replace(position0 - 1, position2 + 1, "+" + String.valueOf(Math.abs(value))).toString();
                        else if (str.toCharArray()[position0 - 1] == '-' && value > 0)
                            str = new StringBuilder(str).replace(position0 - 1, position2 + 1, "-" + String.valueOf(value)).toString();
                        else str = new StringBuilder(str).replace(position0, position2 + 1, String.valueOf(value)).toString();
                    }
                    else str = new StringBuilder(str).replace(position0, position2 + 1, String.valueOf(value)).toString();
                }
            }
            for (k = 0; k < func.length; k++)
            {
                p = new StringBuilder(str).indexOf(func[k]);
                if (p >= 0)
                    str = calculate_function(str, func, operation);
            }
        return str;
        }


    static String substitute_value(String str, double value)
    {
        int X;
        while ((X=str.indexOf("X"))>-1)
        str=new StringBuilder(str).replace(X,X+1,String.valueOf(value)).toString();
        return str;
    }
}
