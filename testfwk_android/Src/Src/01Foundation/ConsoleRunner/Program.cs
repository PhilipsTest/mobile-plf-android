/*  ----------------------------------------------------------------------------
 *  MR Automation : Philips Healthcare Bangalore Center of Competence
 *  ----------------------------------------------------------------------------
 *  Console Runner
 *  ----------------------------------------------------------------------------
 *  File:       Program.cs
 *  Author:     Pattabhirama  Pandit
 *  Creation Date: 
 *  ----------------------------------------------------------------------------
 */

using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Net;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Threading;
using System.Xml;
using Microsoft.VisualStudio.TestTools.UITesting;
using Philips.MRAutomation.Foundation.CrashNotifier;
using Philips.MRAutomation.Foundation.FrameworkCore;
using Philips.MRAutomation.Foundation.FrameworkCore.Common;
using Philips.MRAutomation.Foundation.LoggingUtility;
using Philips.MRAutomation.Foundation.StatusIndicator;
using Philips.MRAutomation.Foundation.TimerNotifier;

namespace Philips.MRAutomation.Foundation.ConsoleRunner
{
    class Program
    {
        [DllImport("user32.dll")]
        private static extern bool ShowWindow(IntPtr hWnd, WindowShowStyle nCmdShow);

        [DllImport("user32.dll", EntryPoint = "FindWindow", SetLastError = true)]
        static extern IntPtr FindWindowByCaption(IntPtr ZeroOnly, string lpWindowName);

        /// <summary>Enumeration of the different ways of showing a window using
        /// ShowWindow</summary>
        private enum WindowShowStyle : uint
        {
            /// <summary>Hides the window and activates another window.</summary>
            /// <remarks>See SW_HIDE</remarks>
            Hide = 0,
            /// <summary>Activates and displays a window. If the window is minimized
            /// or maximized, the system restores it to its original size and
            /// position. An application should specify this flag when displaying
            /// the window for the first time.</summary>
            /// <remarks>See SW_SHOWNORMAL</remarks>
            ShowNormal = 1,
            /// <summary>Activates the window and displays it as a minimized window.</summary>
            /// <remarks>See SW_SHOWMINIMIZED</remarks>
            ShowMinimized = 2,
            /// <summary>Activates the window and displays it as a maximized window.</summary>
            /// <remarks>See SW_SHOWMAXIMIZED</remarks>
            ShowMaximized = 3,
            /// <summary>Maximizes the specified window.</summary>
            /// <remarks>See SW_MAXIMIZE</remarks>
            Maximize = 3,
            /// <summary>Displays a window in its most recent size and position.
            /// This value is similar to "ShowNormal", except the window is not
            /// actived.</summary>
            /// <remarks>See SW_SHOWNOACTIVATE</remarks>
            ShowNormalNoActivate = 4,
            /// <summary>Activates the window and displays it in its current size
            /// and position.</summary>
            /// <remarks>See SW_SHOW</remarks>
            Show = 5,
            /// <summary>Minimizes the specified window and activates the next
            /// top-level window in the Z order.</summary>
            /// <remarks>See SW_MINIMIZE</remarks>
            Minimize = 6,
            /// <summary>Displays the window as a minimized window. This value is
            /// similar to "ShowMinimized", except the window is not activated.</summary>
            /// <remarks>See SW_SHOWMINNOACTIVE</remarks>
            ShowMinNoActivate = 7,
            /// <summary>Displays the window in its current size and position. This
            /// value is similar to "Show", except the window is not activated.</summary>
            /// <remarks>See SW_SHOWNA</remarks>
            ShowNoActivate = 8,
            /// <summary>Activates and displays the window. If the window is
            /// minimized or maximized, the system restores it to its original size
            /// and position. An application should specify this flag when restoring
            /// a minimized window.</summary>
            /// <remarks>See SW_RESTORE</remarks>
            Restore = 9,
            /// <summary>Sets the show state based on the SW_ value specified in the
            /// STARTUPINFO structure passed to the CreateProcess function by the
            /// program that started the application.</summary>
            /// <remarks>See SW_SHOWDEFAULT</remarks>
            ShowDefault = 10,
            /// <summary>Windows 2000/XP: Minimizes a window, even if the thread
            /// that owns the window is hung. This flag should only be used when
            /// minimizing windows from a different thread.</summary>
            /// <remarks>See SW_FORCEMINIMIZE</remarks>
            ForceMinimized = 11
        }

        public static void MinimizeWindow()
        {
            Console.Title = "CDP Automation";
            ShowWindow(FindWindowByCaption(IntPtr.Zero, Console.Title), WindowShowStyle.ShowMinimized);
        }

        public static void Main(string[] args)
        {
            

            ProcessStartInfo proc = new ProcessStartInfo("cmd", "/c " + "regsvr32 " + "/s" + " " + Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location) + "\\" + "Microsoft.VisualStudio.TestTools.UITest.Playback.Engine.dll");
            proc.RedirectStandardOutput = true;
            proc.UseShellExecute = false;
            proc.CreateNoWindow = true;
            Process pro = new Process();
            pro.StartInfo = proc;
            pro.Start();
            string result = pro.StandardOutput.ReadToEnd();
            MinimizeWindow();
            try
            {
                FileLogger.Log(FileLogger.Severity.Information, "Warming up things now, about to kickstart prerequisites");
                if (IsContinuingFromRestart())
                {
                    Thread.Sleep(120000);
                    if (!Playback.IsInitialized)
                    {
                        Playback.Initialize();
                    }
                    Helper.InvokeRecoverMethod("StartFromMRBootConfig", null);
                }
                else
                {
                    if (File.Exists(GeneralConfiguration.transientTestPickerFileLocation))
                    {
                        File.Delete(GeneralConfiguration.transientTestPickerFileLocation);
                    }
                }
                Executor testExecutor = new Executor();
                testExecutor.ExecuteTest();
               
            }
            catch (Exception e)
            {
                FileLogger.Log(FileLogger.Severity.Exception, "Something's really wrong!!" + e.ToString());
            }
        }

        private static bool IsContinuingFromRestart()
        {
            if (File.Exists(GeneralConfiguration.restartIndicator))
            {
                File.Delete(GeneralConfiguration.restartIndicator);
                return true;
            }
            return false;
        }
    }
    public class Executor
    {
        private Thread testExecutor;
        private Thread timerNotifier;
        private Thread crashNotifier;
        private Thread statusIndicator;
        private string assemblyLocation = null;
        private string testcase = null;
        private string dataObjectID = string.Empty;
        private int timeOutValue;
        private Runner runner = null;
        private MyTimer tn;
        private NotifyCrash nc;
        private bool isTimeOut = false;
        private bool isApplicationCrashed = false;
        private bool isTestExecutionComplete = false;
        private bool isExecutionAborted = false;
        private bool stopTestExecution = false;
        private static ManualResetEvent workerWait = new ManualResetEvent(false);
        private enum ActionOnCrash
        {
            None,
            restartApplication,
            restartWindows,
            stopExecution
        };
        private enum ActionOnTimeOut
        {
            None,
            restartforegroundApplication,
            restartWindows,
            stopExecution
        };

        public Executor()
        {
            timeOutValue = 25;
        }

        /// <summary>
        /// Whether window watcher should be turned on
        /// </summary>
        /// <returns></returns>
        private bool isWindowWatcherOn()
        {
            return (ConfigurationAccessor.Utilities.GetGenericConfigurationValue(
                Foundation.FrameworkCore.Utilities.ConfigurationItems.UseWindowWatcher).ToLower() == "true");

        }

        /// <summary>
        /// Execute the test and starts timer notifier thread in a separate thread
        /// </summary>
        public void 
            
            ExecuteTest()
        {
           
            crashNotifier = new Thread(new ThreadStart(CrashNotifier));
            crashNotifier.Start();
            statusIndicator = new Thread(new ThreadStart(Launch));
            statusIndicator.SetApartmentState(ApartmentState.STA);
            statusIndicator.Start();
            Thread.Sleep(500);
           // Runner.CreateNewLog();
            while (GetNextTestToRun())
            {
                FileLogger.Log(FileLogger.Severity.Information, "Before collection: " + GC.GetTotalMemory(false));
                GC.Collect();
                FileLogger.Log(FileLogger.Severity.Information, "After collection: " + GC.GetTotalMemory(true));

                if (ConfigurationAccessor.Utilities.GetPerformanceMonitoringMode())
                {
                    PerformanceMonitor.StartMonitoring();
                }
                workerWait.Reset();
                ResetExecutionFlags();
                GeneralConfiguration.testExecutionStartTime = DateTime.Now;
                timerNotifier = new Thread(new ThreadStart(startTimerNotifier));
                timerNotifier.Start();
                runner = new Runner(assemblyLocation);
                testExecutor = new Thread(new ThreadStart(run));

                if (isWindowWatcherOn())
                {
                    NotifyCrash.TurnOnWindowWatcher();
                }
                else
                {
                    NotifyCrash.TurnOnWindowWatcher();
                }
                NotifyCrash.Hook();
                testExecutor.Start();
                workerWait.WaitOne();
                GeneralConfiguration.testExecutionEndTime = DateTime.Now;
                bool isTimeOutLoc = isTimeOut;
                bool isApplicationCrashedLoc = isApplicationCrashed;
                bool isTestExecutionCompleteLoc = isTestExecutionComplete;

                ResetExecutionFlags();

                if (!WakeupAndReact(isTimeOutLoc, isApplicationCrashedLoc, isTestExecutionCompleteLoc))
                {
                    //NotifyCrash.UnHook();
                    break;
                }
                if (File.Exists(GeneralConfiguration.restartIndicator))
                {
                    //NotifyCrash.UnHook();
                    break;
                }
                // Uncomment these 3 lines if you need a console runner to die and come up again
                //NotifyCrash.UnHook();
                //ScheduleConsoleRunner();
                //Process.GetCurrentProcess().Kill();
            }
            MTBCLog.WrapUp();
            PerformanceMonitor.StopMonitoring();
            string isCopyReportRequired = ConfigurationAccessor.Utilities.GetGenericConfigurationValue
                (Foundation.FrameworkCore.Utilities.ConfigurationItems.CopyReportToServer);
            if (isCopyReportRequired.ToLower().Equals("yes"))
            {
                CopyReports();
            }
            string isCopyLogRequired = ConfigurationAccessor.Utilities.GetGenericConfigurationValue
              (Foundation.FrameworkCore.Utilities.ConfigurationItems.CopyTestSuiteLog);
            if (isCopyLogRequired.ToLower().Equals("yes"))
            {
                string testType = GetTestType();
                string filename = testType + "_" + DateTime.Now.Ticks;
                string logFilename = filename + ".log";
                string zippedLogFilename = Path.GetDirectoryName
                    (Assembly.GetExecutingAssembly().Location) + "\\Reports\\MRApplicationLogs\\" + filename + ".gz";
                string destinationLocation = Path.GetDirectoryName
                    (Assembly.GetExecutingAssembly().Location) + "\\Reports\\MRApplicationLogs\\" +
                    logFilename;
                string sourceLocation = ConfigurationAccessor.Utilities.GetGenericConfigurationValue
                                  (Foundation.FrameworkCore.Utilities.ConfigurationItems.MRApplicationLogPath);
                Runner.CopyApplicationLogFile(sourceLocation, destinationLocation);
                Runner.CompressFile(destinationLocation, zippedLogFilename);
            }
            if (GetTestType() == "ExamCardScan")
            {
                CopyReportstoMEBEFServer();
            }
            string path = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
            if (!Directory.Exists(path + "\\" + "RPFFolder"))
            {
                Directory.CreateDirectory(path + "\\" + "RPFFolder");
                
            }
            Process proc1 = new Process();
            proc1.StartInfo.FileName = "xcopy";
         
            proc1.StartInfo.Arguments = "  /I /F /R /Y  " + "\"" +path+"\\"+"RPF*.png" + "\"" + " " + "\"" + path + "\\" + "RPFFolder" + "\"";
            proc1.Start();
            proc1.WaitForExit();
            proc1.Close();



            Process delProc = new Process();
            delProc.StartInfo.FileName = @path + "\\" + "del.bat";

           
            delProc.Start();
            delProc.WaitForExit();
            delProc.Close();

            //ProcessStartInfo processInfo;
            //Process process;
            //string command = @path + "\\" + "del.bat";
            //processInfo = new ProcessStartInfo("cmd.exe", "/c " + command);
            ////processInfo.CreateNoWindow = true; 
            //processInfo.UseShellExecute = false;
            //// *** Redirect the output *** 
            //processInfo.RedirectStandardError = true;
            //processInfo.RedirectStandardOutput = true;

            //process = Process.Start(processInfo);
            //process.WaitForExit();


            DirectoryInfo d = new DirectoryInfo(Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location)+"\\"+"Reports");
            FileInfo[] infos = d.GetFiles("*.bmp", SearchOption.TopDirectoryOnly);
            foreach (FileInfo f in infos)
            {
                File.Move(f.FullName, f.FullName.ToString().Replace("bmp", "png"));
                Image img = Image.FromFile(f.FullName);
                img.Save(f.FullName, System.Drawing.Imaging.ImageFormat.Png);
            }
            Process.GetCurrentProcess().Kill();
            Process.GetCurrentProcess().Exited += new EventHandler(Executor_Exited);
        }
        private void CopyReportstoMEBEFServer()
        {
            string configFile = "\\Data\\Configuration.xml";
            Dictionary<string, string> configData = (Dictionary<string, string>)TestDataUtility.TestcaseInitializer("Data", configFile);

            Dictionary<string, string> systemConfig = new Dictionary<string, string>();

            try
            {
                string destinationPath = string.Empty;
                string sourcePath = "G:\\log\\logcurrent.log";
                systemConfig = MachineInfo.GetSystemConfig();
                string swid = systemConfig[MachineInfo.Fields.Swid.ToString()];


                destinationPath = @configData["Ipaddress"] + "\\MEBEFLogs" + "\\" + "SWID" + swid + "\\" + Dns.GetHostName() + "\\" +
                     DateTime.Now.ToString("M_d_yyyy_h_mm_ss_tt");
                //if (Dns.GetHostName().Contains("BST"))
                //{

                //    destinationPath = @"\\130.144.246.175\share\automation" + "\\MEBEFLogs" + "\\" + "SWID" + swid + "\\" + Dns.GetHostName() + "\\" +
                //      DateTime.Now.ToString("M_d_yyyy_h_mm_ss_tt");
                //}
                //if (Dns.GetHostName().Contains("BAN"))
                //{
                //    destinationPath = @"\\SPARSERVER" + "\\MEBEFLogs" + "\\" + "SWID" + swid + "\\" + Dns.GetHostName() + "\\" +
                //     DateTime.Now.ToString("M_d_yyyy_h_mm_ss_tt");
                //}
                //else
                //{
                //    destinationPath = @"\\SPARSERVER" + "\\MEBEFLogs" + "\\" + "SWID" + swid + "\\" + Dns.GetHostName() + "\\" +
                //     DateTime.Now.ToString("M_d_yyyy_h_mm_ss_tt");
                //}

                FileLogger.Log(FileLogger.Severity.Information, "Destination Path: " + destinationPath);
                if (!Directory.Exists(destinationPath))
                {
                    Directory.CreateDirectory(destinationPath);
                }
                Process proc1 = new Process();
                proc1.StartInfo.FileName = "xcopy";
                proc1.StartInfo.Arguments = "  /D /S /I /F /R /Y  " + "\"" + sourcePath + "\"" + " " + "\"" + destinationPath + "\"";
                proc1.Start();
                proc1.WaitForExit();
                proc1.Close();
            }
            catch (Exception e)
            {
                FileLogger.Log(FileLogger.Severity.Exception, "Exception while copying reports: " + e.ToString());
            }
        }
        private void CopyReports()
        {
            Dictionary<string, string> systemConfig = new Dictionary<string, string>();
            try
            {
                string mapDrive =
                    ConfigurationAccessor.Utilities.GetGenericConfigurationValue(Foundation.FrameworkCore.Utilities.ConfigurationItems.TestServer_MapDrive);

                DirectoryInfo temp = new DirectoryInfo(mapDrive);
                if (!temp.Exists)
                {
                    Process proc1 = new Process();
                    proc1.StartInfo.FileName = "MapNetworkDrive.vbs";
                    proc1.Start();
                    proc1.WaitForExit();
                    proc1.Close();
                    Thread.Sleep(5000);
                }
                temp = new DirectoryInfo(mapDrive);
                if (temp.Exists)
                {
                    systemConfig = MachineInfo.GetSystemConfig();
                    string swid = systemConfig[MachineInfo.Fields.Swid.ToString()];
                    string automationDataLocationOnServer = Path.Combine(mapDrive, "Automation");
                    string destinationPath = Path.Combine(automationDataLocationOnServer, "TestReports",
                        "SWID" + swid, GeneralConfiguration.testCaseType, DateTime.Now.ToString("M_d_yyyy_h_mm_ss_tt"));
                    FileLogger.Log(FileLogger.Severity.Information, "Destination Path: " + destinationPath);
                    if (!Directory.Exists(destinationPath))
                    {
                        Directory.CreateDirectory(destinationPath);
                    }
                    GeneralConfiguration.reportSources.Add("Reports");
                    string filePath = GetLatestReportPath();
                    for (int i = 0; i < GeneralConfiguration.reportSources.Count; i++)
                    {
                        string sourcePath = GeneralConfiguration.reportSources[i];
                        DirectoryInfo di = new DirectoryInfo(sourcePath);
                        FileLogger.Log(FileLogger.Severity.Information, "Source Path: " + sourcePath);
                        Process proc1 = new Process();
                        proc1.StartInfo.FileName = "xcopy";
                        proc1.StartInfo.Arguments = "  /D /S /I /F /R /Y  " + "\"" + sourcePath + "\"" + " " + "\"" + Path.Combine(destinationPath, di.Name) + "\"";
                        proc1.Start();
                        proc1.WaitForExit();
                        proc1.Close();
                        //proc1.StartInfo.FileName = "xcopy";
                        //proc1.StartInfo.Arguments = filePath + " " +
                        //    "Y:\\SendMail";
                        //proc1.Start();
                        //proc1.WaitForExit();
                        //proc1.Close();
                    }
                    //Prashanth H S: To upload data to Database and Generate automail.
                    //try
                    //{
                    //    DirectoryInfo dirInfo = new DirectoryInfo("Y:\\SendMail");
                    //    FileInfo[] fileInfo = dirInfo.GetFiles("*.xml");
                    //    string[] desiredFile = filePath.Split('\\');
                    //    for (int i = 0; i < fileInfo.Length; i++)
                    //    {
                    //        if (fileInfo[i].Name == desiredFile[desiredFile.Length - 1])
                    //        {
                    //            uploadAndMailSend = true;
                    //        }
                    //    }
                    //    if (uploadAndMailSend)
                    //    {
                    //        Process process = new Process();
                    //        process.StartInfo.FileName = Path.GetDirectoryName
                    //            (Assembly.GetExecutingAssembly().Location) + "\\RemoteExecution.exe";
                    //        process.Start();
                    //        process.WaitForExit();
                    //        process.Close();
                    //    }
                    //}
                    //catch
                    //{

                    //}


                }
                else
                {
                    FileLogger.Log(FileLogger.Severity.Error, "Error while copying reports: " + mapDrive + " does note exist.");
                }
            }
            catch (Exception e)
            {
                FileLogger.Log(FileLogger.Severity.Exception, "Exception while copying reports: " + e.ToString());
            }
        }

        #region GetLatestReportPath
        /// <summary>
        /// Function to Get a Latest Report from Executing folder.
        /// </summary>
        /// <returns>string (report path)</returns>
        private static string GetLatestReportPath()
        {
            List<string> xmlFile = new List<string>();
            List<string> reportFiles = new List<string>();
            try
            {
                string reportLocation = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location) + "\\Reports";
                DirectoryInfo dir = new DirectoryInfo(reportLocation);
                DateTime lastHigh = new DateTime(1900, 1, 1);
                if (dir != null)
                {
                    FileInfo[] fileInfo = dir.GetFiles("*.xml");

                    for (int i = 0; i < fileInfo.Length; i++)
                    {
                        if (fileInfo[i].Name != "SystemConfig.xml" && fileInfo[i].Name != "TestConfig.xml"
                            && fileInfo[i].Name != "ReportConfiguration.xml" && fileInfo[i].Name != "TestReport.xml" && fileInfo[i].Name != "TestXMLToExcel.xml")
                        {
                            DateTime created = fileInfo[i].LastWriteTime;
                            if (created > lastHigh)
                            {
                                lastHigh = created;
                            }
                        }
                    }
                    foreach (FileInfo file in fileInfo)
                    {
                        string s = file.LastWriteTime.ToString();
                        if (s == lastHigh.ToString())
                        {
                            xmlFile.Add(file.Name);
                            break;
                        }
                    }
                }
            }
            catch
            {

            }
            return Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location) + "\\Reports\\" + xmlFile[0];
        }
        #endregion GetLatestReportPath

        private void ScheduleConsoleRunner()
        {
            try
            {
                List<string> commands = new List<string>();
                string schedulerFile = Directory.GetCurrentDirectory() + "\\schedule.bat";
                FileLogger.Log(FileLogger.Severity.Information,
                    "Scheduling tasks");
                Process proc = new Process();
                DateTime dt = DateTime.Now.AddMinutes(2);
                string arg1;
                if (dt.Hour.ToString().Length == 1)
                {
                    arg1 = "0" + dt.Hour.ToString();
                }
                else
                {
                    arg1 = dt.Hour.ToString();
                }

                if (dt.Minute.ToString().Length == 1)
                {
                    arg1 += ":" + "0" + dt.Minute.ToString();
                }
                else
                {
                    arg1 += ":" + dt.Minute.ToString();
                }
                proc.StartInfo.Arguments = "\"" + arg1 + "\"" +
                    " " + "\"" + Assembly.GetExecutingAssembly().Location + "\"";
                proc.StartInfo.FileName = schedulerFile;
                proc.Start();
                proc.Close();
                Thread.Sleep(30000);
            }
            catch
            {

            }
        }

        void Executor_Exited(object sender, EventArgs e)
        {
            //NotifyCrash.UnHook();
        }

        /// <summary>
        /// Run the test case
        /// </summary>
        private void run()
        {
            //if (!Playback.IsInitialized)
            //{
            //    Playback.Initialize();
            //    Microsoft.VisualStudio.TestTools.UITesting.Mouse.MouseMoveSpeed = 3000;
            //    Microsoft.VisualStudio.TestTools.UITesting.Mouse.MouseDragSpeed = 3000;
            //}
            runner = new Runner(assemblyLocation);
            runner.Run("Default", testcase, dataObjectID);
            isTestExecutionComplete = true;
            workerWait.Set();
        }

        /// <summary>
        /// Start the timer notifier thread
        /// </summary>
        private void startTimerNotifier()
        {
            FileLogger.Log(FileLogger.Severity.Information, "Starting timer notifier thread");
            tn = new MyTimer(timeOutValue);
            tn.timeOver += new MyTimer.timeOutHandler(tn_timeOver);
            tn.StartTimerNotifier();
        }

        /// <summary>
        /// event handler callback function for timer notifier
        /// </summary>
        void tn_timeOver()
        {
            isTimeOut = true;
            workerWait.Set();
        }

        /// <summary>
        /// Crash Notifier thread
        /// </summary>
        public void CrashNotifier()
        {
            FileLogger.Log(FileLogger.Severity.Information, "Starting Crash notifier thread");
            MTBCLog.Create();
            nc = new NotifyCrash();
            nc.ForwardHandleCrash += new NotifyCrash.ForwardCrashHandler(nc_HandleCrash);
            nc.Start();
        }


        public void Launch()
        {
            FileLogger.Log(FileLogger.Severity.Information, "Starting status indicator thread");
            Indicator statusIndicator = new Indicator();
            statusIndicator.abortExecution += new Indicator.stopExecutionHandler(statusIndicator_abortExecution);
            statusIndicator.Launch();
        }

        void statusIndicator_abortExecution()
        {
            isExecutionAborted = true;
            if (GetTestType() == "ExamCardScan")
            {
                CopyReportstoMEBEFServer();
            }
            workerWait.Set();
        }

        /// <summary>
        /// Event handler callback function for crash notifier thread
        /// </summary>
        void nc_HandleCrash()
        {
            isApplicationCrashed = true;
            workerWait.Set();
        }

        /// <summary>
        /// Reset the execution flags
        /// </summary>
        private void ResetExecutionFlags()
        {
            isTimeOut = false;
            isApplicationCrashed = false;
            isTestExecutionComplete = false;
        }

        /// <summary>
        /// If crash or timeout occurs, returns the action to be performed
        /// </summary>
        /// <param name="aoc">action on crash</param>
        /// <param name="aot">action on timeout</param>
        private void GuideMeForRecovery(out ActionOnCrash aoc, out ActionOnTimeOut aot)
        {
            string dir = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
            XmlDocument doc = new XmlDocument();
            XmlNodeList xnl;
            string[] actions = { "OnCrash", "OnTimeOut" };
            doc.Load(GeneralConfiguration.testPickerFileLocation);
            aoc = ActionOnCrash.None;
            aot = ActionOnTimeOut.None;

            foreach (string action in actions)
            {
                try
                {
                    xnl = doc.GetElementsByTagName(action);

                    if (xnl[0].InnerText.ToLower() ==
                        ActionOnCrash.restartApplication.ToString().ToLower() && action == "OnCrash")
                    {
                        aoc = ActionOnCrash.restartApplication;
                    }

                    else if (xnl[0].InnerText.ToLower() ==
                        ActionOnCrash.restartWindows.ToString().ToLower() && action == "OnCrash")
                    {
                        aoc = ActionOnCrash.restartWindows;
                    }
                    else if (xnl[0].InnerText.ToLower() ==
                        ActionOnCrash.stopExecution.ToString().ToLower() && action == "OnCrash")
                    {
                        aoc = ActionOnCrash.stopExecution;
                    }
                    else if (xnl[0].InnerText.ToLower() ==
                        ActionOnTimeOut.restartforegroundApplication.ToString().ToLower() && action == "OnTimeOut")
                    {
                        aot = ActionOnTimeOut.restartforegroundApplication;
                    }
                    else if (xnl[0].InnerText.ToLower() ==
                        ActionOnTimeOut.restartWindows.ToString().ToLower() && action == "OnTimeOut")
                    {
                        aot = ActionOnTimeOut.restartWindows;
                    }
                    else if (xnl[0].InnerText.ToLower() ==
                        ActionOnTimeOut.stopExecution.ToString().ToLower() && action == "OnTimeOut")
                    {
                        aot = ActionOnTimeOut.stopExecution;
                    }
                }
                catch (Exception ex)
                {
                    Console.WriteLine(ex.Message);
                }
            }
        }

        /// <summary>
        /// If main is woken up, 
        /// 1. it will continue execution of next test case OR
        /// 2. restart application
        /// 3. restart windows
        /// </summary>
        /// <param name="isTimeOutLoc"></param>
        /// <param name="isApplicationCrashedLoc"></param>
        /// <param name="isTestExecutionCompleteLoc"></param>
        private bool WakeupAndReact(bool isTimeOutLoc,
            bool isApplicationCrashedLoc,
            bool isTestExecutionCompleteLoc)
        {
            bool success = true;
            FileLogger.Log(FileLogger.Severity.Information,
                "In WakeupAndReact, Timeout:, ApplnCrash:, TestExecComplete:, ExecutionAborted:" +
                isTimeOutLoc.ToString() + " " + isApplicationCrashedLoc.ToString() + " "
                + isTestExecutionCompleteLoc.ToString() + " " + isExecutionAborted.ToString());
            if (isExecutionAborted)
            {
                success = false;
                return success;
            }
            else if (isTestExecutionCompleteLoc == true)
            {
                FileLogger.Log(FileLogger.Severity.Information,
                    "Interrupting the timer notification thread as current test case execution is complete");
                timerNotifier.Interrupt();
                timerNotifier.Join();
                return success;
            }

            //Now check for timeout or crash
            ActionOnCrash aoc;
            ActionOnTimeOut aot;
            GuideMeForRecovery(out aoc, out aot);

            if (isTimeOutLoc == true)
            {
                MyTimer.SetTimeoutFlag(true);
                Report.AppendMessage("Test Timeout has reached!");
                switch (aot)
                {
                    case ActionOnTimeOut.restartforegroundApplication:
                        RestartForeGroundApplication();
                        break;
                    case ActionOnTimeOut.restartWindows:
                        RestartWindows();
                        break;
                    case ActionOnTimeOut.stopExecution:
                        stopTestExecution = true;
                        break;
                    default:
                        StopTestExecution();
                        break;

                }
                MyTimer.SetTimeoutFlag(false);
            }

            if (isApplicationCrashedLoc == true)
            {
                NotifyCrash.SetApplicationCrashedFlag(true);
                Report.AppendMessage("Application Crashed!");
                MTBCLog.Log(DateTime.Now, GetRunNumber(), GetTotalRunCount());
                Thread.Sleep(5000);
                switch (aoc)
                {
                    case ActionOnCrash.restartApplication:
                        RestartApplication();
                        break;
                    case ActionOnCrash.restartWindows:
                        RestartWindows();
                        break;
                    case ActionOnCrash.stopExecution:
                        stopTestExecution = true;
                        break;
                    default:
                        StopTestExecution();
                        break;
                }

                NotifyCrash.SetApplicationCrashedFlag(false);
            }
            return success;

        }

        /// <summary>
        /// Restart Application
        /// </summary>
        private void RestartApplication()
        {
            PerformanceMonitor.StopMonitoring();
            if (!Playback.IsInitialized)
            {
                Playback.Initialize();
                Microsoft.VisualStudio.TestTools.UITesting.Mouse.MouseMoveSpeed = 2000;
                Microsoft.VisualStudio.TestTools.UITesting.Mouse.MouseDragSpeed = 2000;
            }
            if (timerNotifier.IsAlive)
            {
                FileLogger.Log(FileLogger.Severity.Information,
                    "Interrupting the timer notification thread");
                timerNotifier.Interrupt();
                timerNotifier.Join();
            }
            StopTestExecution();

            Helper.InvokeRecoverMethod("RestartApplication", null);
            // Checking it again, just in case the application crashed while restarting. 
            // If crash occurs, falling back to restart windows option
            //if (isApplicationCrashed == true)
            //{
            //    RestartWindows();
            //}
        }

        /// <summary>
        /// Restart Application
        /// </summary>
        private void RestartForeGroundApplication()
        {
            PerformanceMonitor.StopMonitoring();
            if (!Playback.IsInitialized)
            {
                Playback.Initialize();
                Microsoft.VisualStudio.TestTools.UITesting.Mouse.MouseMoveSpeed = 2000;
                Microsoft.VisualStudio.TestTools.UITesting.Mouse.MouseDragSpeed = 2000;
            }
            if (timerNotifier.IsAlive)
            {
                FileLogger.Log(FileLogger.Severity.Information,
                    "Interrupting the timer notification thread");
                timerNotifier.Interrupt();
                timerNotifier.Join();
            }
            StopTestExecution();
            Helper.InvokeRecoverMethod("RestartForegroundApplication", null);
        }

        /// <summary>
        /// Stop test execution
        /// </summary>
        private void StopTestExecution()
        {
            if (testExecutor.IsAlive)
            {
                FileLogger.Log(FileLogger.Severity.Information,
                    "Interrupting the test execution thread");
                try
                {
                    testExecutor.Abort();
                    testExecutor.Join();
                }
                catch
                {
                }
            }
        }

        /// <summary>
        /// Restart Windows
        /// </summary>
        private void RestartWindows()
        {
            PerformanceMonitor.StopMonitoring();
            File.Create(GeneralConfiguration.restartIndicator);
            Helper.InvokeRecoverMethod("RestartWindows", new object[] { Assembly.GetExecutingAssembly().Location });
            ;
            if (timerNotifier.IsAlive)
            {
                FileLogger.Log(FileLogger.Severity.Information,
                    "Interrupting the timer notification thread");
                timerNotifier.Interrupt();
                timerNotifier.Join();
            }
            StopTestExecution();
        }

        /// <summary>
        /// Gets the total run. i.e. no of loops * num of tests
        /// </summary>
        /// <returns>Total run count</returns>
        public static int GetTotalRunCount()
        {
            return GetLoopCount() * GetTestCount();
        }

        /// <summary>
        /// Gets the current run number
        /// </summary>
        /// <returns>Current run number</returns>
        public static int GetRunNumber()
        {
            return (GetTestCount() * GetCompletedLoopCount()) + GetCompletedTestsInCurrenLoop();
        }

        /// <summary>
        /// Gets the type of tests selected for run
        /// </summary>
        /// <returns>Type of Tests selected for run</returns>
        private static string GetTestType()
        {
            string file = GeneralConfiguration.testPickerFileLocation;
            XmlDocument xmlDoc = new XmlDocument();
            try
            {
                xmlDoc.Load(file);
            }
            catch
            {
                xmlDoc.Load(GeneralConfiguration.testPickerFileLocation);
            }
            string[] splitedString = xmlDoc.GetElementsByTagName("AssemblyLocation")[0].InnerText.Split('.');
            string testType = splitedString[splitedString.Length - 2];
            return testType;
        }
        /// Gets the number of tests selected for run
        /// </summary>
        /// <returns>Count of Tests selected for run</returns>
        private static int GetTestCount()
        {
            string file = GeneralConfiguration.transientTestPickerFileLocation;
            XmlDocument xmlDoc = new XmlDocument();
            try
            {
                xmlDoc.Load(file);
            }
            catch
            {
                xmlDoc.Load(GeneralConfiguration.testPickerFileLocation);
            }
            return xmlDoc.GetElementsByTagName("Tests").Count;
        }

        /// <summary>
        /// Gets the number of loops selected for the run
        /// </summary>
        /// <returns>Number of loops</returns>
        private static int GetLoopCount()
        {
            string file = GeneralConfiguration.transientTestPickerFileLocation;
            XmlDocument xmlDoc = new XmlDocument();
            try
            {
                xmlDoc.Load(file);
            }
            catch
            {
                xmlDoc.Load(GeneralConfiguration.testPickerFileLocation);
            }
            return Convert.ToInt32(xmlDoc.GetElementsByTagName("TestList")[0].Attributes[0].Value);
        }

        /// <summary>
        /// Gets the number of loops completed
        /// </summary>
        /// <returns>Number of loops completed</returns>
        private static int GetCompletedLoopCount()
        {
            int returnValue = 0;
            string file = GeneralConfiguration.transientTestPickerFileLocation;
            XmlDocument xmlDoc = new XmlDocument();
            try
            {
                xmlDoc.Load(file);
            }
            catch
            {
                xmlDoc.Load(GeneralConfiguration.testPickerFileLocation);
            }
            if (xmlDoc.GetElementsByTagName("CompletedCount").Count > 0)
            {
                try
                {
                    returnValue = Convert.ToInt32(xmlDoc.GetElementsByTagName("CompletedCount")[0].InnerText);
                }
                catch
                {
                }
            }
            return returnValue;
        }

        /// <summary>
        /// Gets the number of tests completed in current loop
        /// </summary>
        /// <returns>Number of tests completed in current loop</returns>
        private static int GetCompletedTestsInCurrenLoop()
        {
            int returnValue = 0;
            string file = GeneralConfiguration.transientTestPickerFileLocation;
            XmlDocument xmlDoc = new XmlDocument();
            try
            {
                xmlDoc.Load(file);
            }
            catch
            {
                xmlDoc.Load(GeneralConfiguration.testPickerFileLocation);
            }
            if (xmlDoc.GetElementsByTagName("NextTestCase").Count > 0)
            {
                try
                {
                    returnValue = Convert.ToInt32(xmlDoc.GetElementsByTagName("NextTestCase")[0].InnerText);
                }
                catch
                {
                }
            }
            return returnValue;
        }

        /// <summary>
        /// Gets the next test case to run
        /// </summary>
        /// <returns>false if there are no test cases to execute, else true</returns>
        private bool GetNextTestToRun()
        {
            string file = GeneralConfiguration.transientTestPickerFileLocation;
            XmlDocument xmlDoc = new XmlDocument();
            string testPickerFile =
                GeneralConfiguration.testPickerFileLocation;
            XmlDocument tempxmlDoc = new XmlDocument();
            tempxmlDoc.Load(testPickerFile);

            if (File.Exists(file) && ConfigurationAccessor.Utilities.GetGenericConfigurationValue
                        (Foundation.FrameworkCore.Utilities.ConfigurationItems.SaveStateInformation).ToLower() ==
                        "true")// tempxmlDoc.GetElementsByTagName("SaveStateInformation")[0].InnerText.ToLower() == "true")
            {
                xmlDoc.Load(file);
                assemblyLocation = xmlDoc.GetElementsByTagName(
                    "AssemblyLocation")[0].InnerText;
                int count = xmlDoc.GetElementsByTagName("Tests").Count;

                int curTestCaseIndex = Convert.ToInt32(xmlDoc.GetElementsByTagName("NextTestCase")[0].InnerText);
                string curTestCase = xmlDoc.GetElementsByTagName("Tests")[curTestCaseIndex].InnerText.ToString();
                string curDataObjectID = string.Empty;
                try
                {
                    curDataObjectID = xmlDoc.GetElementsByTagName("Tests")[curTestCaseIndex].Attributes["DOId"].Value.ToString();
                }
                catch
                {
                }
                if (curTestCaseIndex == 0)
                {
                    if (Convert.ToInt32(xmlDoc.GetElementsByTagName("TestList")[0].Attributes[0].Value) <=
                        Convert.ToInt32(xmlDoc.GetElementsByTagName("CompletedCount")[0].InnerText))
                    {
                        File.Delete(file);
                        return false;
                    }
                }
                else if (stopTestExecution == true)
                {
                    File.Delete(file);
                    return false;
                }
                if (curTestCaseIndex == count - 1)
                {
                    xmlDoc.GetElementsByTagName("CompletedCount")[0].InnerText =
                            (Convert.ToInt32(xmlDoc.GetElementsByTagName("CompletedCount")[0].InnerText) + 1).ToString();
                }

                xmlDoc.GetElementsByTagName("NextTestCase")[0].InnerText = Convert.ToString((curTestCaseIndex + 1) % count);

                if (xmlDoc.GetElementsByTagName("TimeOut" + curTestCase).Count > 0)
                {
                    timeOutValue = Convert.ToInt32(xmlDoc.GetElementsByTagName("TimeOut" + curTestCase)[0].InnerText);
                }
                else if (xmlDoc.GetElementsByTagName("TimeOut").Count > 0)
                {
                    timeOutValue = Convert.ToInt32(xmlDoc.GetElementsByTagName("TimeOut")[0].InnerText);
                }
                else
                {
                    timeOutValue = 25;
                }
                testcase = curTestCase;
                dataObjectID = curDataObjectID;
                File.Delete(file);
                xmlDoc.Save(file);
                return true;
            }
            else
            {
                xmlDoc.Load(testPickerFile);
                assemblyLocation = xmlDoc.GetElementsByTagName("AssemblyLocation")[0].InnerText;
                int count = xmlDoc.GetElementsByTagName("Tests").Count;
                if (xmlDoc.GetElementsByTagName("Tests").Count == 0)
                {
                    return false;
                }
                testcase = xmlDoc.GetElementsByTagName("Tests")[0].InnerText.ToString();
                try
                {
                    dataObjectID = xmlDoc.GetElementsByTagName("Tests")[0].Attributes["DOId"].Value.ToString();
                }
                catch
                {
                }
                int nextTestCaseIndex = 1 % count;

                string nexttestcase = xmlDoc.GetElementsByTagName("Tests")[1 % count].InnerText.ToString();

                if (xmlDoc.GetElementsByTagName("TimeOut" + testcase).Count > 0)
                {
                    timeOutValue = Convert.ToInt32(xmlDoc.GetElementsByTagName("TimeOut" + testcase)[0].InnerText);
                }
                else if (xmlDoc.GetElementsByTagName("TimeOut").Count > 0)
                {
                    timeOutValue = Convert.ToInt32(xmlDoc.GetElementsByTagName("TimeOut")[0].InnerText.ToString());
                }
                else
                {
                    timeOutValue = 25;
                }

                if (ConfigurationAccessor.Utilities.GetGenericConfigurationValue
                        (Foundation.FrameworkCore.Utilities.ConfigurationItems.SaveStateInformation).ToLower() ==
                        "true")//xmlDoc.GetElementsByTagName("SaveStateInformation")[0].InnerText.ToLower() == "true")
                {
                    XmlNode root = xmlDoc.DocumentElement;
                    if (xmlDoc.GetElementsByTagName("NextTestCase").Count == 0)
                    {
                        XmlElement nextTC = xmlDoc.CreateElement("NextTestCase");
                        nextTC.InnerText = Convert.ToString(nextTestCaseIndex);
                        root.InsertAfter(nextTC, root.SelectSingleNode("Data"));
                    }
                    else
                    {
                        xmlDoc.GetElementsByTagName("NextTestCase")[0].InnerText = Convert.ToString(nextTestCaseIndex);
                    }

                    string initialCount = "0";
                    if (count == 1)
                    {
                        initialCount = "1";
                    }
                    if (xmlDoc.GetElementsByTagName("CompletedCount").Count == 0)
                    {
                        XmlElement completedLoopCount = xmlDoc.CreateElement("CompletedCount");
                        completedLoopCount.InnerText = initialCount;
                        root.InsertAfter(completedLoopCount, root.SelectSingleNode("Data"));
                    }
                    else
                    {
                        xmlDoc.GetElementsByTagName("CompletedCount")[0].InnerText = initialCount;
                    }

                    if (xmlDoc.GetElementsByTagName("TestCaseReportName").Count == 0)
                    {
                        XmlElement completedLoopCount = xmlDoc.CreateElement("TestCaseReportName");
                        root.AppendChild(completedLoopCount);
                    }
                    if (xmlDoc.GetElementsByTagName("WorkFlowReportName").Count == 0)
                    {
                        XmlElement completedLoopCount = xmlDoc.CreateElement("WorkFlowReportName");
                        root.AppendChild(completedLoopCount);
                    }

                    if (File.Exists(file))
                    {
                        try
                        {
                            File.Delete(file);
                            xmlDoc.Save(file);
                        }
                        catch (Exception e)
                        {
                            FileLogger.Log(FileLogger.Severity.Exception,
                                "Exception while deleting file " + file + " :" + e.Message);
                        }
                    }
                    else
                    {
                        xmlDoc.Save(file);
                    }

                }
                return true;
            }
        }
    }

    /// <summary>
    /// Helper class
    /// </summary>
    public class Helper
    {
        /// <summary>
        /// Invoke a method in Recoverer with method name
        /// </summary>
        /// <param name="methodName">methodName</param>
        /// <param name="parametersArray">parametersArray</param>
        public static void InvokeRecoverMethod(string methodName, object[] parametersArray)
        {
            Assembly assembly = Assembly.LoadFile(Path.Combine(GeneralConfiguration.executingAssemblyLocation, "Philips.MRAutomation.Utilities.Recoverer.dll"));
            Type type = assembly.GetType("Philips.MRAutomation.Utilities.Recoverer.Recover");
            if (type != null)
            {
                MethodInfo methodInfo = type.GetMethod(methodName);
                if (methodInfo != null)
                {
                    object result = null;
                    ParameterInfo[] parameters = methodInfo.GetParameters();
                    object classInstance = Activator.CreateInstance(type, null);
                    if (parameters.Length == 0)
                    {
                        result = methodInfo.Invoke(classInstance, null);
                    }
                    else
                    {
                        result = methodInfo.Invoke(methodInfo, parametersArray);
                    }
                }
            }
        }
    }
}

#region Revision History
/*
 * 02-13-2011  : Modifed by Girish for Crash Recovery and timeout support
 * 02-21-2011  : Modifed by Pattabhi, Girish for Reliability and Integrator tests
 */
#endregion
