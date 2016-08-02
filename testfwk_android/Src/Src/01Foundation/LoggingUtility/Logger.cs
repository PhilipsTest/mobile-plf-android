/*  ----------------------------------------------------------------------------
 *  MR Automation : Philips Healthcare Bangalore Center of Competence
 *  ----------------------------------------------------------------------------
 *  Logging Utility
 *  ----------------------------------------------------------------------------
 *  File:       Logger.cs
 *  Author:     Pattabhirama  Pandit
 *  Creation Date: 
 *  ----------------------------------------------------------------------------
 */


using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Philips.MRAutomation.Foundation.FrameworkCore;
using Philips.MRAutomation.Foundation.FrameworkCore.Common;
using Philips.MRAutomation.Foundation.StatusIndicator;
using Philips.CDPAutomation.Foundation.TestCaseDefinition;

namespace Philips.MRAutomation.Foundation.LoggingUtility
{
    internal enum TestStatus
    {
        Pass, Fail, Message, Exception, Unexpected_Error, Manual_Verification, None, TestDetails
    }

    /// <summary>
    /// 
    /// </summary>
    public class Report
    {
        static Reporter TCrpt = new Reporter();
        static Reporter WFrpt = new Reporter();        
        static string directoryPath = Path.GetDirectoryName(Assembly.GetExecutingAssembly().GetModules()[0].FullyQualifiedName);        
        static string reportPath = directoryPath + "\\Reports";
        static string stackTrace = string.Empty;
        static bool isTestReportDeserialized = false;
        static bool isWorkflowReportDeserialized = false;
        static ReporterSystemConfiguration reporterSystemConfiguration = new ReporterSystemConfiguration();
        string tcIdFromConstructor;
        string tcNameFromConstructor;
        List<String> descriptions;
        List<string> messages;
        List<ReporterTestCaseStepDetails> testDetails;
        List<ReporterTestCasePerformanceOperationsOperation> tempPerformanceOperations;
        List<ReporterTestCasePerformanceReadingsReading> tempPerformanceReadings;
        
        /// <summary>
        /// 
        /// </summary>
        public static string fileName = String.Empty;        
        TestStatus testStatus;
        bool addedTorpt;
        static Report localReport;
        string timeFormat = "M/d/yyyy h:mm:ss tt";
        // static StackFrame[] staticFrames;
        //static List<StackFrame> staticStackTraceFrames;
        static Type staticDeclaringType;
        /// <summary>
        /// 
        /// </summary>
        /// <param name="tcid"></param>
        /// <param name="tcName"></param>
        public Report(string tcid = "" , string tcName = "")
        {
            descriptions = new List<string>();
            messages = new List<string>();
            testDetails = new List<ReporterTestCaseStepDetails>();
            testStatus = TestStatus.Unexpected_Error;
            addedTorpt = false;                        
            tempPerformanceOperations = 
                new List<ReporterTestCasePerformanceOperationsOperation>();
            tempPerformanceReadings = new List<ReporterTestCasePerformanceReadingsReading>();
            if (!Directory.Exists(reportPath))
            {
                Directory.CreateDirectory(reportPath);
            }
            tcIdFromConstructor = tcid;
            tcNameFromConstructor = tcName;            
            ReportTestCase rt = new ReportTestCase();
            rt.Name = tcNameFromConstructor;
            localReport = this;
        }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="comment"></param>
        public void Pass(string comment)
        {
            LogMessage(comment, TestStatus.Pass);
        }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="comment"></param>
        public void Fail(string comment)
        {
            LogMessage(comment, TestStatus.Fail);

        }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="comment"></param>
        public void Message(string comment)
        {
            LogMessage(comment, TestStatus.Message);
        }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="stepNumber"></param>
        /// <param name="stepDescription"></param>
        /// <param name="expectedResults"></param>
        /// <param name="actualResults"></param>
        /// <param name="evidenceInfo"></param>
        public void EnterTestDetails(string stepNumber, string stepDescription, string expectedResults, string actualResults, string evidenceInfo)
        {
            string testDetails = stepNumber + '|' + stepDescription + '|' + expectedResults + '|' + actualResults + '|' + evidenceInfo;
            LogMessage(testDetails, TestStatus.TestDetails);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="comment"></param>
        public void Exception(string comment)
        {
            LogMessage(comment, TestStatus.Exception);
        }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="comment"></param>
        public void ManualVerification (string comment)
        {
            LogMessage(comment, TestStatus.Manual_Verification);
        }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="id"></param>
        /// <param name="startTime"></param>
        /// <param name="endTime"></param>
        public void AddPerformanceOperation(string id, string startTime, string endTime)
        {
            ReporterTestCasePerformanceOperationsOperation p = new ReporterTestCasePerformanceOperationsOperation();
            p.OperationID = id;
            p.StartTime = startTime;
            p.EndTime = endTime;
            LogMessage(string.Empty, TestStatus.None, p);
        }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="id"></param>
        /// <param name="value"></param>
        /// <param name="units"></param>
        public void AddPerformanceReading(string id, string value, string units)
        {
            ReporterTestCasePerformanceReadingsReading p = new ReporterTestCasePerformanceReadingsReading();
            p.OperationID = id;
            p.Unit = units;
            p.Value = value;
            LogMessage(string.Empty, TestStatus.None,null,p);
        }

        /// <summary>
        /// Appends a message to the latest report blob
        /// </summary>
        /// <param name="comment">string message</param>
        public static void AppendMessage (string comment)
        {
            if (localReport != null)
            {
                localReport.Message(comment);
            }
        }
     
        private static void GenerateTestLog(string testID, string msg, TestStatus status)
        {
            if (!Directory.Exists(directoryPath))
            { Directory.CreateDirectory(directoryPath); }

            try
            {
                StringBuilder sbTestLog = new StringBuilder();
                sbTestLog.AppendFormat("[{0}] [{1}] [{3}] [{2}]", DateTime.Now.ToString(), testID, msg, status.ToString());
                sbTestLog.AppendLine();
                File.AppendAllText(directoryPath + @"\TestExecutionLog", sbTestLog.ToString());
            }
            catch (ArgumentException)
            { Console.WriteLine("Invalid argumets for the log file. Cannot generate the test log."); }
            catch
            { Console.WriteLine("Cannot generate the test log."); }            
        }

        private string GetContinuedReportName(string possibleFileName)
        {
            string file = GeneralConfiguration.transientTestPickerFileLocation;
            XmlDocument xmlDoc = new XmlDocument();
            string dir = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
            
            string reportName = null;
            try
            {
                if (possibleFileName.Contains("Workflows"))
                {
                    if (File.Exists(file) && ConfigurationAccessor.Utilities.GetGenericConfigurationValue 
                        (Foundation.FrameworkCore.Utilities.ConfigurationItems.SaveStateInformation).ToLower () == 
                        "true")                        
                    {
                        xmlDoc.Load(file);
                        if (xmlDoc.GetElementsByTagName("WorkFlowReportName").Count > 0)
                        {
                            reportName = xmlDoc.GetElementsByTagName("WorkFlowReportName")[0].InnerText.ToString();
                            if (reportName == possibleFileName)
                            {
                                reportName = null;
                            }
                            else if (isWorkflowReportDeserialized == false && (reportName != ""))
                            {
                                XmlSerializer serializer = new XmlSerializer(typeof(Reporter));
                                XmlReader xr = XmlReader.Create(reportPath + "\\" + reportName);
                                WFrpt = serializer.Deserialize(xr) as Reporter;
                                xr.Close();
                                isWorkflowReportDeserialized = true;
                            }
                            else if (reportName == "")
                            {
                                xmlDoc.GetElementsByTagName("WorkFlowReportName")[0].InnerText = possibleFileName;
                                File.Delete(file);
                                xmlDoc.Save(file);
                            }
                        }
                    }
                }
                else
                {
                    if (File.Exists(file) &&  ConfigurationAccessor.Utilities.GetGenericConfigurationValue 
                        (Foundation.FrameworkCore.Utilities.ConfigurationItems.SaveStateInformation).ToLower () == 
                        "true")
                    {
                        xmlDoc.Load(file);
                        if (xmlDoc.GetElementsByTagName("TestCaseReportName").Count > 0)
                        {
                            reportName = xmlDoc.GetElementsByTagName("TestCaseReportName")[0].InnerText.ToString();
                            if (reportName == possibleFileName)
                            {
                                reportName = null;
                            }
                            else if (isTestReportDeserialized == false && (reportName != ""))
                            {
                                XmlSerializer serializer = new XmlSerializer(typeof(Reporter));
                                XmlReader xr = XmlReader.Create(reportPath + "\\" + reportName);
                                TCrpt = serializer.Deserialize(xr) as Reporter;
                                xr.Close();
                                isTestReportDeserialized = true;
                            }
                            else if (reportName == "")
                            {
                                xmlDoc.GetElementsByTagName("TestCaseReportName")[0].InnerText = possibleFileName;
                                File.Delete(file);
                                xmlDoc.Save(file);
                            }
                        }
                    }
                }
            }
            catch
            {
            }
            return reportName;
        }        

        private void LogMessage(string comment, TestStatus status=TestStatus.None, 
            ReporterTestCasePerformanceOperationsOperation performanceOperation=null, ReporterTestCasePerformanceReadingsReading performanceReading= null)
        {       
            VerificationTypeAttribute verificationtype = null;
            TestCaseInfoAttribute tcInfo = null;
            TestcasePropertiesAttribute tcProperties = null;
            TestcaseAttribute tc = null;
            ProductAttribute tcProduct = null;
            ReportTestCase testcase = new ReportTestCase ();
            StackTrace methodAttribTrace = new StackTrace();
            StackFrame[] frames = methodAttribTrace.GetFrames();
            List<StackFrame> stackTraceFrames = new List<StackFrame>(methodAttribTrace.GetFrames());
            Type declaringType = stackTraceFrames[2].GetMethod().DeclaringType;
            ReporterTestCaseStepDetails stepDetails = null ; 

            object[] objArray = declaringType.GetCustomAttributes(true);
            if (objArray.Length != 0 && !objArray[0].GetType ().Name.Contains ("ReUsable"))
            {
                staticDeclaringType = declaringType;
            }
            else
            {
                declaringType = staticDeclaringType;
                objArray = declaringType.GetCustomAttributes(true);
            }
            if (status == TestStatus.None)
            {
                status = testStatus;
            }

            string possibleFileName = declaringType.Module + "_" + Process.GetCurrentProcess().Id.ToString() + "_TestReport.xml";
            fileName = GetContinuedReportName(possibleFileName );
            if (fileName == null || fileName == "")
            {
                fileName = possibleFileName;
            }

            foreach (object obj in objArray)
            {
                try
                {
                    switch (obj.GetType().Name)
                    {
                        case "VerificationTypeAttribute":
                            verificationtype = obj as VerificationTypeAttribute;
                            break;
                        case "TestCaseInfoAttribute":
                            tcInfo = obj as TestCaseInfoAttribute;
                            break;
                        case "TestcasePropertiesAttribute":
                            tcProperties = obj as TestcasePropertiesAttribute;
                            break;
                        case "TestcaseAttribute":
                            tc = obj as TestcaseAttribute;
                            break;
                        case "ProductAttribute":
                            tcProduct = obj as ProductAttribute;
                            break;
                    }
                }
                catch (Exception e)
                {
                    FileLogger.Log(FileLogger.Severity.Exception, e.ToString());
                }            
            }
           
            if (status == TestStatus.Exception)
            {
                stackTrace = comment;
            }
            else if (status == TestStatus.TestDetails)
            {
                if (comment != null && comment != string.Empty)
                {
                    stepDetails = new ReporterTestCaseStepDetails();

                    var details = comment.Split('|');
                    stepDetails.StepNumber = details[0];
                    stepDetails.StepDescription = details[1];
                    stepDetails.ExpectedResult = details[2];
                    stepDetails.ActualResult = details[3];
                    stepDetails.EvidenceInfo = details[4];

                   
                }
            }
            else if (status != TestStatus.None)
            {
                if (comment != null && comment != string.Empty)
                {
                    messages.Add(comment);
                }
            }
            testcase.StackTrace = stackTrace;

            if (status == TestStatus.Fail || status == TestStatus.Pass || status==TestStatus.Manual_Verification)
            {
                descriptions.Clear();
                testStatus = status;
                string[] desc = tcInfo.Description.Split(Environment.NewLine.ToCharArray());

                for (int i = 0; i < desc.Length; i++)
                {
                    if (desc[i] != "")
                    {
                        string temp = desc[i].TrimStart(' ');
                        descriptions.Add(temp);
                    }
                }           
            }

            testcase.Status = testStatus.ToString().Replace('_', ' ');
            string[] tempDesc = new string[descriptions.Count];
            descriptions.CopyTo(tempDesc, 0);
            testcase.Descriptions = tempDesc;

            testcase.FunctionalArea = tcInfo.MainArea;
            
            if (tcNameFromConstructor == null || tcNameFromConstructor == string.Empty)
            {
                testcase.Name = tcInfo.Name;
            }
            else
            {
                testcase.Name = tcNameFromConstructor;
            }

            if (stepDetails != null)
            {
                testDetails.Add(stepDetails);
                
            }
            testcase.TestStepDetails = testDetails.ToArray();

            testcase.Product = tcProduct.Product.ToString();
            testcase.ProductVersion = tcProduct.VersionIntroduced;
            testcase.RunStatus = "RUN";
            
            testcase.VerificationType = verificationtype.VerificationType;
            testcase.TestCaseType = tc.Type.ToString();
            testcase.TestCaseState = tc.State.ToString();
            testcase.ReqID = null;
            testcase.TCID = tcInfo.Id; 
            testcase.Messages = messages.ToArray();
            testcase.HasTimedOut = GeneralConfiguration.applicationTimeout.ToString ();
            testcase.HasCrashed = GeneralConfiguration.applicationCrashed.ToString();
            testcase.StartTime = GeneralConfiguration.testExecutionStartTime.ToString(timeFormat);
            testcase.EndTime = DateTime.Now.ToString(timeFormat);

            GeneralConfiguration.testCaseType = tc.Type.ToString();

            if (performanceOperation != null)
            {
                tempPerformanceOperations.Add(performanceOperation);                
            }
            testcase.PerformanceOperations = tempPerformanceOperations.ToArray();

            if (performanceReading != null)
            {
                tempPerformanceReadings.Add(performanceReading);                
            }
            testcase.PerformanceReadings = tempPerformanceReadings.ToArray();

            Dictionary<string, string> systemConfig = MachineInfo.GetSystemConfig();

            reporterSystemConfiguration.AcquisitionSystemType = systemConfig[MachineInfo.Fields.AcquisitionSystemType.ToString()];
            reporterSystemConfiguration.IPAddress = systemConfig[MachineInfo.Fields.IPAdress.ToString()];
            reporterSystemConfiguration.Level = systemConfig[MachineInfo.Fields.Level.ToString()];
            reporterSystemConfiguration.MagnetType = systemConfig[MachineInfo.Fields.MagnetType.ToString()];
            reporterSystemConfiguration.MainSystemType = systemConfig[MachineInfo.Fields.MainSystemType.ToString()];
            reporterSystemConfiguration.ProductModel = systemConfig[MachineInfo.Fields.ProductModel.ToString()];
            reporterSystemConfiguration.Release = systemConfig[MachineInfo.Fields.Release.ToString()];
            reporterSystemConfiguration.Stream = systemConfig[MachineInfo.Fields.Stream.ToString()];
            reporterSystemConfiguration.Swid = systemConfig[MachineInfo.Fields.Swid.ToString()];
            reporterSystemConfiguration.SystemName = systemConfig[MachineInfo.Fields.SystemName.ToString()];
            reporterSystemConfiguration.SystemType = systemConfig[MachineInfo.Fields.SystemType.ToString()];
            reporterSystemConfiguration.Version = systemConfig[MachineInfo.Fields.Version.ToString()];
            reporterSystemConfiguration.PerformanceMonitoringFolder = PerformanceMonitor.outputFolder;
            if (fileName.Contains("Workflows"))
            {
                if (addedTorpt == false)
                {
                    if (WFrpt.TestCase.Count > 0)
                    {
                        WFrpt.TestCase[WFrpt.TestCase.Count - 1].EndTime =
                            GeneralConfiguration.testExecutionEndTime.ToString();
                    }
                    WFrpt.TestCase.Add(testcase);
                    addedTorpt = true;
                }
                else
                {
                    WFrpt.TestCase[WFrpt.TestCase.Count - 1] = testcase;
                }
                WFrpt.SystemConfiguration = reporterSystemConfiguration;
            }
            else
            {
                if (addedTorpt == false)
                {
                    if (TCrpt.TestCase.Count > 0)
                    {
                        TCrpt.TestCase[TCrpt.TestCase.Count - 1].EndTime =
                            GeneralConfiguration.testExecutionEndTime.ToString();
                    }
                    TCrpt.TestCase.Add(testcase);
                    addedTorpt = true;
                }
                else
                {
                    TCrpt.TestCase[TCrpt.TestCase.Count - 1] = testcase;
                }
                TCrpt.SystemConfiguration = reporterSystemConfiguration;
            }
            
            XmlSerializer serializer = new XmlSerializer(typeof(Reporter));
            FileStream fs = null;

            if (!Directory.Exists(reportPath))
            {
                Directory.CreateDirectory(reportPath);
            }
            if (fileName.Contains("Workflows"))
            {                
                fs = new FileStream(Path.Combine(reportPath, fileName), FileMode.Create, FileAccess.Write);
                serializer.Serialize(fs, WFrpt);

                int passCount = 0;
                int failCount = 0;
                int unexpectedErrorCount = 0;
                for (int i = 0; i < WFrpt.TestCase.Count - 1; i++)
                {
                    ReportTestCase t = WFrpt.TestCase[i];
                    switch (t.Status.ToLower())
                    {
                        case "pass":
                            passCount++;
                            break;
                        case "fail":
                            failCount++;
                            break;
                        case "unexpected error":
                            unexpectedErrorCount++;
                            break;
                    }
                }
                Indicator.UpdateCount(passCount, failCount, unexpectedErrorCount);
            }
            else
            {
                fs = new FileStream(Path.Combine(reportPath, fileName), FileMode.Create, FileAccess.Write);
                serializer.Serialize(fs, TCrpt);

                int passCount = 0;
                int failCount = 0;
                int unexpectedErrorCount = 0;
                for (int i = 0; i < TCrpt.TestCase.Count-1; i++)
                {
                    ReportTestCase t = TCrpt.TestCase[i];
                    switch (t.Status.ToLower())
                    {
                        case "pass":
                            passCount++;
                            break;
                        case "fail":
                            failCount++;
                            break;
                        case "unexpected error":
                            unexpectedErrorCount++;
                            break;
                    }

                }
                Indicator.UpdateCount(passCount, failCount, unexpectedErrorCount);
            }
            if (fs != null)
            {
                fs.Close();
            }              
        }

        /// <summary>
        /// Gets all the test results from a report file
        /// </summary>
        /// <param name="filename">test report filename</param>
        /// <returns>Reporter object</returns>
        public static Reporter GetTestResults(string filename)
        {
            XmlSerializer serializer = new XmlSerializer(typeof(Reporter));
            FileStream fs = null;
            Reporter tempdata = null;            
            if (File.Exists(filename))
            {
                fs = new FileStream(filename, FileMode.Open, FileAccess.Read);
                try
                {                    
                    tempdata = (Reporter)serializer.Deserialize(fs);
                }
                catch
                {
                }
                fs.Close();
            }
            return tempdata;
        }
    }
}

#region Revision History
/* 
 * 25-01-2011  : Modifed by Girish for creating a single node for each test 
 * 31-01-2011  : Modifed by Girish - made functions non-static, removed report.end
 * 21-02-2011  : Added un-expected error as enum for crash recovery
 */

#endregion
