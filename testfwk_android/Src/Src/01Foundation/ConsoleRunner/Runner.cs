using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.IO.Compression;
using System.Reflection;
using System.Threading;
using System.Xml;
using Philips.MRAutomation.Foundation.FrameworkCore;
using Philips.MRAutomation.Foundation.LoggingUtility;
using Philips.MRAutomation.Foundation.StatusIndicator;

namespace Philips.MRAutomation.Foundation.ConsoleRunner
{
    public class Runner
    {
        Assembly testAssembly = null;
        XmlDocument sequencedescriber = new XmlDocument();
        public Runner(string assemblyName)
        {
            testAssembly = Assembly.LoadFrom(assemblyName);
            sequencedescriber.Load("RunSequence.xml");
        }

        public void Run(string sequence, string TestName, string dataObjectId)
        {
            Type testCase = null;
            string isCopyLogRequired = ConfigurationAccessor.Utilities.GetGenericConfigurationValue
               (Foundation.FrameworkCore.Utilities.ConfigurationItems.CopyTestCaseLog);
            XmlNodeList sequenceList = sequencedescriber.GetElementsByTagName(sequence);
            if (sequenceList.Count > 0)
            {
                Type[] types = testAssembly.GetTypes();
                foreach (Type desiredtest in types)
                {
                    if (desiredtest.Name.Contains(TestName))
                    {
                        testCase = desiredtest;
                    }
                }
                Indicator.SetTestCase(testCase.Name);
                if (isCopyLogRequired.ToLower().Equals("yes"))
                {
                    CreateNewLog();
                }
                MethodInfo[] testsegments = testCase.GetMethods();
                List<MethodInfo> customMethods = new List<MethodInfo>();
                List<MethodInfo> finalMethodList = new List<MethodInfo>();
                foreach (MethodInfo mi in testsegments)
                {
                    if (mi.GetCustomAttributes(true).Length > 0)
                    {
                        customMethods.Add(mi);
                    }
                }
                XmlNodeList sequencing = sequenceList[0].ChildNodes;

                foreach (XmlNode n in sequencing)
                {
                    foreach (MethodInfo testsegment in customMethods)
                    {

                        if (testsegment.Name.Contains(n.InnerText))
                        {
                            finalMethodList.Add(testsegment);
                        }
                    }

                }

                List<string> messageBoxInfo = new List<string>();
                if (ConfigurationAccessor.Utilities.GetUserWarningMode())
                {
                    if (ConfigurationAccessor.Utilities.GetWarningMessage(testAssembly.GetName().Name + ".dll", TestName, out messageBoxInfo))
                    {
                        if (messageBoxInfo.Count > 2)
                        {
                            Indicator.MessageBoxButtons mButtons = (Indicator.MessageBoxButtons)
                                Enum.Parse(typeof(Indicator.MessageBoxButtons), messageBoxInfo[2]);
                            Indicator.ShowMessageBox(messageBoxInfo[0], messageBoxInfo[1], mButtons);
                        }
                    }
                }

                object instanceCreator;
                object[] dataObjectArray = new object[1];
                if (dataObjectId != null && dataObjectId != string.Empty)
                {
                    dataObjectArray[0] = (object)dataObjectId;
                    instanceCreator = Activator.CreateInstance(testCase, dataObjectArray);
                }
                else
                {
                    instanceCreator = Activator.CreateInstance(testCase);
                }

                try
                {

                    foreach (MethodInfo method in finalMethodList)
                    {
                        Indicator.Show();
                        object[] objArr = new object[1];
                        Indicator.SetTestStep("In " + method.Name);
                        method.Invoke(instanceCreator, objArr);
                    }
                }
                catch (Exception threadex)
                {
                    FileLogger.Log(FileLogger.Severity.Information,
                        "Gracefully terminating the test execution thread, interrupt exception caught. " +
                        threadex.ToString());
                }
                if (isCopyLogRequired.ToLower().Equals("yes"))
                {
                    string filename = testCase.Name + "_" + DateTime.Now.Ticks;
                    string logFilename = filename + ".log";
                    string zippedLogFilename = Path.GetDirectoryName
                        (Assembly.GetExecutingAssembly().Location) + "\\Reports\\MRApplicationLogs\\" + filename + ".gz";
                    string destinationLocation = Path.GetDirectoryName
                        (Assembly.GetExecutingAssembly().Location) + "\\Reports\\MRApplicationLogs\\" +
                        logFilename;
                    string sourceLocation = ConfigurationAccessor.Utilities.GetGenericConfigurationValue
                                      (Foundation.FrameworkCore.Utilities.ConfigurationItems.MRApplicationLogPath);
                    CopyApplicationLogFile(sourceLocation, destinationLocation);
                    CompressFile(destinationLocation, zippedLogFilename);
                }
            }
        }

        #region CreateNewLog
        /// <summary>
        /// Function to Create a New Log for new testcase.
        /// </summary>
        public static void CreateNewLog()
        {
            Assembly assembly1 = Assembly.LoadFile(Path.Combine(GeneralConfiguration.executingAssemblyLocation, "Philips.MRAutomation.Utilities.TestAssemblyParser.dll"));
            Type type = assembly1.GetType("Philips.MRAutomation.Utilities.TestAssemblyParser.Helper");
            //object obj = Activator.CreateInstance(type);
            //MethodInfo prop = type.GetMethod("GenerateTestListExamcardScan");
            //PropertyInfo piValue = type.GetProperty("SetScanAutomation", BindingFlags.Static || BindingFlags.Instance);
            //PropertyInfo pInfo = type.GetProperty("SetScanAutomation", BindingFlags.Public |
            //    BindingFlags.NonPublic |
            //    BindingFlags.Instance |BindingFlags.Static);
            PropertyInfo pInfo = type.GetProperty("SetScanAutomation", BindingFlags.Static |BindingFlags.NonPublic);
            bool scanAutomation = (bool)pInfo.GetValue(null, null);
            try
            {
               
                if (!scanAutomation)
                {
                    ProcessStartInfo startInfo = new ProcessStartInfo("CMD.exe");
                    Process p = new Process();
                    startInfo.RedirectStandardInput = true;
                    startInfo.UseShellExecute = false;
                    startInfo.RedirectStandardOutput = true;
                    startInfo.RedirectStandardError = true;
                    p = Process.Start(startInfo);
                    Thread.Sleep(2000);
                    p.StandardInput.WriteLine
                        (@"P_logfile_nu createnewlogfile");
                    p.StandardInput.WriteLine
                    (@"p_logfile_nu logcentral "+System.Net.Dns.GetHostName());
                    p.StandardInput.WriteLine(@"EXIT");
                    string output = p.StandardOutput.ReadToEnd();
                    string error = p.StandardError.ReadToEnd();
                    p.WaitForExit();
                    Console.Write(output);
                    p.Close();
                }
               
            }
            catch (Exception e)
            {
                FileLogger.Log(FileLogger.Severity.Exception,
                        "Exception while Creating a new MR Application log file. " +
                        e.ToString());
            }
        }

        #endregion

        #region CopyApplicationLogFile
        /// <summary>
        /// Function to copy the log file after completion of the testcase.
        /// </summary>
        /// <param name="sourcefile">Source file name.</param>
        /// <param name="destfile">Destination file name.</param>
        public static void CopyApplicationLogFile(string sourcefile, string destfile)
        {
            try
            {
                FileInfo sourceFileInfo = new FileInfo(sourcefile);
                FileInfo destinationFileInfo = new FileInfo(destfile);
                int index = destfile.LastIndexOf("\\");
                string directory = destfile.Substring(0, index);
                if (!Directory.Exists(directory))
                {
                    Directory.CreateDirectory(directory);
                }
                sourceFileInfo.CopyTo(destfile);
            }
            catch (Exception e)
            {
                FileLogger.Log(FileLogger.Severity.Exception,
                        "Exception while copying a MRApplication log file. " +
                        e.ToString());
            }
        }
        #endregion

        #region CompressFile
        /// <summary>
        /// Function to Zip the file
        /// </summary>
        /// <param name="path">file to be ziped</param>
        /// <param name="zipFilename">zip file name</param>
        public static void CompressFile(string path, string zipFilename)
        {
            try
            {
                FileStream sourceFile = File.OpenRead(path);
                FileStream destinationFile = File.Create(path + ".gz");
                byte[] buffer = new byte[sourceFile.Length];
                sourceFile.Read(buffer, 0, buffer.Length);
                using (GZipStream output = new GZipStream(destinationFile,
                    CompressionMode.Compress))
                {
                    Console.WriteLine("Compressing {0} to {1}.", sourceFile.Name,
                        destinationFile.Name, false);
                    output.Write(buffer, 0, buffer.Length);
                }
                sourceFile.Close();
                destinationFile.Close();
                File.Delete(path);
            }
            catch (Exception e)
            {
                FileLogger.Log
                    (FileLogger.Severity.Exception, "Exception while zipping the MRApplication file. "
                    + e.ToString());
            }
        }
        #endregion CompressFile

    }
}

