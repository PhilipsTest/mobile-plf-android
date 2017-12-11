using System.Management;
using System.Diagnostics;
using System;
using System.Net;
using System.Xml;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Net.NetworkInformation;


namespace Philips.MRAutomation.Foundation.FrameworkCore.Common
{
    public class MachineInfo
    {
        ManagementObjectSearcher query;
        ManagementObjectCollection result;
        string responseString;
        int responseInt;

        public string GetMachineName()
        {
            return Environment.MachineName.ToUpper();
        }

        public string GetOSVersion()
        {
            return Environment.OSVersion.VersionString;
        }

        public string GetProcessorCount()
        {
            return Environment.ProcessorCount.ToString();
        }

        public string GetIPAddress()
        {
            IPHostEntry ipEntry = Dns.GetHostEntry(Dns.GetHostName());
            IPAddress[] ipAddress = ipEntry.AddressList;
            return ipAddress[0].ToString();
        }

        public string GetTotalPhysicalMemory()
        {
            query = new ManagementObjectSearcher("SELECT * FROM Win32_LogicalMemoryConfiguration");
            result = query.Get();
            foreach (ManagementObject managementObject in result)
            {
                responseInt = Convert.ToInt32(managementObject["TotalPhysicalMemory"].ToString());
            }
            responseInt = (responseInt / 1024);
            responseString = responseInt.ToString() + " MB";
            return responseString;
        }

        public string GetAvailablePhysicalMemory()
        {
            PerformanceCounter counter = new PerformanceCounter("Memory", "Available Bytes");
            responseInt = ((int)Convert.ToInt64(counter.NextValue())) / (1024 * 1024);
            responseString = responseInt.ToString() + " MB";
            return responseString;
        }

        public string GetTotalVirtualMemory()
        {
            query = new ManagementObjectSearcher("SELECT * FROM Win32_LogicalMemoryConfiguration");
            result = query.Get();
            foreach (ManagementObject managementObject in result)
            {
                responseInt = Convert.ToInt32(managementObject["TotalVirtualMemory"].ToString());
            }
            responseInt = (responseInt / 1024);
            responseString = responseInt.ToString() + " MB";
            return responseString;
        }

        public string GetAvailableVirtualMemory()
        {
            query = new ManagementObjectSearcher("SELECT * FROM Win32_LogicalMemoryConfiguration");
            result = query.Get();
            foreach (ManagementObject managementObject in result)
            {
                responseInt = Convert.ToInt32(managementObject["AvailableVirtualMemory"].ToString());
            }
            responseInt = (responseInt / 1024);
            responseString = responseInt.ToString() + " MB";
            return responseString;
        }

        public string GetCpuFrequency()
        {
            query = new ManagementObjectSearcher("SELECT * FROM Win32_Processor");
            result = query.Get();
            foreach (ManagementObject managementObject in result)
            {
                responseInt = Convert.ToInt32(managementObject["CurrentClockSpeed"].ToString());
            }
            responseString = responseInt.ToString() + " MHz";
            return responseString;
        }

         /// <summary>
        ///  Configuration field
        /// </summary>
        public enum Fields
        {
            ProductModel,//Ingenia
            MainSystemType,//WA15
            SystemType,//SCANNER
            MagnetType,//WA_15T
            AcquisitionSystemType,//CDAS
            Stream,
            Swid,
            Release, //5
            Version,//1
            Level,//1
            SystemName,
            IPAdress,
            ProductVersion,
            MRVersionID,//15.01       
            NumberOfReceivers,//8 or 16
            NrOf1HBoardReceivers//8 or 16
        };


        /// <summary>
        /// Variable configFilePath
        /// </summary>
        static string configFilePath = @"C:\nmr\systemcheck";

        /// <summary>
        /// setupRegistry Path
        /// </summary>
        static string setupRegistryPath = @"G:\Site";


        /// <summary>
        ///  Configuration list collection
        /// </summary>
        public static Dictionary<string, string> configList = new Dictionary<string, string>();
        static string filename = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location)+"\\Reports";

        #region GetSystemConfig
        /// <summary>
        /// Function to get the system configuartion and the Application Information from the current machine and MR Application.
        /// </summary>
        /// <example>List<string> systemInfo =  SystemConfiguration.GetSystemConfig();</example>
        /// <returns>list of string (contains a system information)</returns>
        public static Dictionary<string, string> GetSystemConfig ()
        {
            try
            {
                for (int i = 0; i < Enum.GetValues(typeof(Fields)).Length; i++)
                {
                    configList[((Fields)i).ToString()] = string.Empty;
                }
                //To get the stream name of the MR Application
                string streamFile = configFilePath + "\\stream.txt";
                if (File.Exists(streamFile))
                {
                    StreamReader sr = new StreamReader(streamFile);
                    configList[Fields.Stream.ToString()] = sr.ReadLine();
                    sr.Close();
                }

                //To get the swid number of the MR Application
                string swidFile = configFilePath + "\\buildnr.txt";
                if (File.Exists(swidFile))
                {
                    StreamReader sr = new StreamReader(swidFile);
                    configList[Fields.Swid.ToString()] = sr.ReadLine();
                    sr.Close();

                }

                //To get the Product Version of the MR Application
                string productVersionFile = configFilePath + "\\releasenr.txt";
                if (File.Exists(productVersionFile))
                {
                    StreamReader sr = new StreamReader(productVersionFile);
                    configList[Fields.ProductVersion.ToString()] = sr.ReadLine();
                    sr.Close();
                }

                //To get the HostName of the current machine
                string hostname = Dns.GetHostName();
                configList[Fields.SystemName.ToString()] = hostname;

                //To get the IP Address of the Current Machine
                NetworkInterface[] nis = NetworkInterface.GetAllNetworkInterfaces();
                string ipAddress = string.Empty;
                foreach (NetworkInterface ni in nis)
                {
                    if (ni.Name.ToLower().Contains("hospital"))
                    {
                        UnicastIPAddressInformationCollection unicastAddressInfoCollection = ni.GetIPProperties().UnicastAddresses;
                        foreach (UnicastIPAddressInformation ipaddressInfo in unicastAddressInfoCollection)
                        {
                            if (ipaddressInfo.Address.AddressFamily == System.Net.Sockets.AddressFamily.InterNetwork)
                            {
                                ipAddress = ipaddressInfo.Address.ToString();
                                configList[Fields.IPAdress.ToString()] = ipAddress;
                                break;
                            }
                        }
                    }
                }
                filename = filename + "\\SystemConfig.xml";
                XmlDocument xmlDoc = new XmlDocument();
                FileStream fs = new FileStream(filename, FileMode.Create, FileAccess.ReadWrite);
                XmlTextWriter xmlWriter = new XmlTextWriter(fs, System.Text.Encoding.UTF8);
                xmlWriter.Formatting = Formatting.Indented;
                xmlWriter.WriteProcessingInstruction("xml", "version='1.0' encoding='UTF-8'");
                xmlWriter.WriteStartElement("SystemConfiguration");
                for (int i = 0; i < Enum.GetValues(typeof(Fields)).Length; i++)
                {
                    xmlWriter.WriteElementString(((Fields)i).ToString(), configList[((Fields)i).ToString()]);
                }
                xmlWriter.WriteEndElement();
                xmlWriter.Flush();
                xmlWriter.Close();
                fs.Close();
                filename = string.Empty;
            }
            catch
            {
            }

            XmlDocument doc = new XmlDocument();

            string path = setupRegistryPath;
            if (path != string.Empty && Directory.Exists(path))
            {
                string setupregistry = Path.Combine(path, "p_evmm_setupregistry.xml");
                doc.Load(setupregistry);
                XmlNodeList nlist = doc.GetElementsByTagName("Attribute");

                List<String> systemInfoVariables = new List<string>() { 
                "Release", //5
                "Version",//1
                "Level",//1
                "ProductModel",//Ingenia
                "MRVersionID",//15.01
                "MainSystemType",//WA15
                "SystemType",//SCANNER
                "MagnetType",//WA_15T
                "AcquisitionSystemType",//CDAS
                "NrOf1HBoardReceivers",//8 or 16
                "NumberOfReceivers"//8 or 16
                };

                foreach (string s in systemInfoVariables)
                {
                    foreach (XmlNode n1 in nlist)
                    {
                        if (n1.Attributes["Name"].Value == s)
                            configList[s] = n1.InnerText.Replace("\"", "");
                    }
                }
                doc = null;
            }
            return configList;
        }
        #endregion GetSystemConfig

        /// <summary>
        /// Get the total size of a drive
        /// </summary>
        /// <param name="driveName">Drive Name</param>
        /// <returns>Total size</returns>
        public static double GetTotalDriveSize (string driveName)
        {
            DriveInfo[] driveInfo = DriveInfo.GetDrives();
            foreach (DriveInfo drive in driveInfo)
            {
                if (drive.IsReady && drive.Name.ToLower().Contains(driveName.ToLower()))
                {
                    return (drive.TotalSize / Math.Pow(1024, 3));
                }
            }
            return -1;
        }

        /// <summary>
        /// Get the free space of a drive
        /// </summary>
        /// <param name="driveName">Drive name</param>
        /// <returns>Free space</returns>
        public static double GetFreeDriveSpace (string driveName)
        {
            DriveInfo[] driveInfo = DriveInfo.GetDrives();
            foreach (DriveInfo drive in driveInfo)
            {
                if (drive.IsReady && drive.Name.ToLower().Contains(driveName.ToLower()))
                {
                    return drive.TotalFreeSpace / Math.Pow(1024, 3);
                }
            }
            return -1;
        }
    }

}
