using System;
using System.Text.RegularExpressions;

public class Example
{
    static int Foo(int n)
    {
        if (n == 0)
        {
            return 0;
        }
        return Foo(n - 1);
    }
    static int Bar(int n)
    {
        if (n == 0)
        {
            return 1;
        }
        else return n * n + Foo(n);
    }
    public static void Main()
    {
        Console.WriteLine(Bar(9));
    }
}
