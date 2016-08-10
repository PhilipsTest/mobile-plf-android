using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;


namespace Philips.MRAutomation.Utilities.ConsoleRunner
{
    public class Serializer
    {

        public void SerializeNow()
        {
            DataTransfer c=new DataTransfer();
            
            File f=new File("temp.dat");
            Stream s=f.Open(FileMode.Create);
            BinaryFormatter b=new BinaryFormatter();
            b.Serialize(s, c);
            s.Close();
        }
        public void DeSerializeNow()
        {
            DataTransfer c=new DataTransfer();
            File f=new File("temp.dat");
            Stream s=f.Open(FileMode.Open);
            BinaryFormatter b=new BinaryFormatter();
            c=(DataTransfer)b.Deserialize(s);
            Console.WriteLine(c.name);
            s.Close();
        }


    }
    public class DataTransfer
    {
        private int age;
        private string name;
        static string companyname;
        SupportClass supp=new SupportClass();
        public DataTransfer()
        {
            supp.SupportClassString="In support class";
        }
        public int Age
        {
            get
            {
                return age;
            }
            set
            {
                age=value;
            }
        }
        public string Name
        {
            get
            {
                return name;
            }
            set
            {
                name=value;
            }
        }
        public static string CompanyName
        {
            get
            {
                return companyname;
            }
            set
            {
                companyname=value;
            }
        }
        public string GetSupportClassString()
        {
            return supp.SupportClassString;
        }
    }
    public class SupportClass
    {
        public string SupportClassString;
    }

}

