using System;
using System.Collections.Generic;
using System.Text;
using MtomClient.localhost;

namespace MtomClient
{
    class Program
    {
        static void Main(string[] args)
        {
            MtomServiceClient client = new MtomServiceClient();
            //sending a silly small data, this is not why you use MTOM but this is 
            //just to show interop of .NET 3.0 with Metro
            byte [] resp = client.echoBinary(Encoding.UTF8.GetBytes("Hello World!"));
            Console.WriteLine("Received: " + Encoding.UTF8.GetString(resp));
        }
    }
}
