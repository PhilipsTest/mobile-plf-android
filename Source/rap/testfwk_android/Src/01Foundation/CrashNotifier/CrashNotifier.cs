/*  ----------------------------------------------------------------------------
 *  MR Automation : Philips Healthcare Bangalore Center of Competence
 *  ----------------------------------------------------------------------------
 *  Console Runner
 *  ----------------------------------------------------------------------------
 *  File:       CrashNotifier.cs
 *  Author:     Pattabhirama  Pandit
 *  Creation Date: 2/13/2011
 *  ----------------------------------------------------------------------------
 */

using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Threading;
using System.Windows.Forms;
using System.Xml;
using Microsoft.VisualStudio.TestTools.UITesting;
//using MSAAWrapper;
using Philips.MRAutomation.Foundation.FrameworkCore;
using Philips.MRAutomation.Foundation.FrameworkCore.Common;
using Philips.MRAutomation.Foundation.LoggingUtility;
using MSAAWrapper;

namespace Philips.MRAutomation.Foundation.CrashNotifier
{
    internal class NativeMethods
    {
        /// <summary>
        /// 
        /// </summary>
        /// <param name="hWnd"></param>
        /// <param name="lpRect"></param>
        /// <returns></returns>
        [DllImport("user32.dll")]
        public static extern bool GetClientRect(int hWnd, out RECT lpRect);
        
        /// <summary>
        /// 
        /// </summary>
        public struct RECT
        {
            public int left;
            public int top;
            public int right;
            public int bottom;
        }
    }

    public partial class MRDialogNotifier : Form
    {
        public static WinEventHandler WinEvnt;
        private static List<windowsToWatch> windowList = new List<windowsToWatch>();
        public delegate void CrashHandler();
        public static event CrashHandler HandleCrash;
        private static string[] systemCrumblingErrorWindows = new string[] { "Application Error" };
        private const string crashWin = "Crash";
        private static bool isCaptureWindowsOn = false;
        public static bool storeCompareWindows = false;
        private static int sleepCount = 1000;
        private static List<string> windowsDiposed = new List<string>();
        private static string expectedWindowStore = @"\Data\WindowGoldenBitmaps";
        private static string actualWindowStore = @"\Reports\DisposedWindowsStore";
        public static bool isHooked = true;
        private struct windowsToWatch
        {
            public string windowName;
            public bool captureWindow;
            public bool captureScreen;
        };

        /// <summary>
        /// Constructor
        /// </summary>
        public MRDialogNotifier()
        {
            FileLogger.Log(FileLogger.Severity.Information, "Creating the crashNotifier form");
            InitializeComponent();
            isCaptureWindowsOn = IsCaptureWindowsOn();
            WinEvnt = new WinEventHandler(0);
            WinEvnt.Listen();
            FileLogger.Log(FileLogger.Severity.Information, "Listening to windows events");
            WinEvnt.ModalDialogPopup += new WinEventHandler.WinEventDelegate(WinEvnt_ModalDialogPopup);
            WinEvnt.WindowsDialogPopup += new WinEventHandler.WinEventDelegate(WinEvnt_WindowsDialogPopup);
        }

        public static void SetWindowWatcher()
        {
            GetWindowList("Window");
        }

        public static void ReSetWindowWatcher()
        {
            if (windowList.Count > 0)
            {
                windowList.Clear();
            }
        }

        public static string[] GetWindowsDisposed()
        {
            string[] windows = new string[windowsDiposed.Count];
            windowsDiposed.CopyTo(windows);
            windowsDiposed.Clear();
            return windows;
        }

        public static void AddWindowToWatch(string windowName, bool captureWindow, bool captureScreen)
        {
            bool added = false;
            foreach (windowsToWatch temp in windowList)
            {
                if (temp.windowName == windowName)
                {
                    added = true;
                    break;
                }
            }
            if (!added)
            {
                windowsToWatch newWindow;
                newWindow.windowName = windowName;
                newWindow.captureWindow = captureWindow;
                newWindow.captureScreen = captureScreen;
                windowList.Add(newWindow);
            }
        }

        public static void RemoveWindowFromBeingWatched(string windowName)
        {
            foreach (windowsToWatch temp in windowList)
            {
                if (temp.windowName == windowName)
                {
                    windowList.Remove(temp);
                    break;
                }
            }
        }

        public static bool IsWindowWatcherOn()
        {
            if (windowList.Count > 0)
            {
                return true;
            }
            return false;
        }

        private static void GetWindowList(string nodename)
        {
            string dir = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
            XmlDocument doc = new XmlDocument();
            XmlNodeList xnl;
            if (windowList.Count < 1)
            {
                doc.Load(GeneralConfiguration.crashNotifierConfigirationFileLocation);
                try
                {
                    xnl = doc.GetElementsByTagName(nodename);
                    foreach (XmlNode xn in xnl)
                    {
                        windowsToWatch ww;
                        ww.windowName = xn.InnerText;
                        ww.captureWindow = false;
                        ww.captureScreen = false;
                        windowList.Add(ww);
                    }
                }
                catch (Exception ex)
                {
                    Console.WriteLine(ex.Message);
                }
            }
        }

        private static bool IsCaptureWindowsOn()
        {
            string dir = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
            XmlDocument doc = new XmlDocument();
            XmlNodeList xnl;
            bool on = false;
            if (windowList.Count < 1)
            {
                doc.Load(GeneralConfiguration.crashNotifierConfigirationFileLocation);
                try
                {
                    xnl = doc.GetElementsByTagName("CaptureWindows");
                    foreach (XmlNode xn in xnl)
                    {
                        on = xn.InnerText.ToLower() == "true" ? true : false;
                        if (on)
                        {
                            sleepCount = 2000;
                        }
                        break;
                    }
                }
                catch (Exception ex)
                {
                    Console.WriteLine(ex.Message);
                }
            }
            return on;
        }

        private static void WinEvnt_ModalDialogPopup(WinEventArgs args)
        {
            bool flag = false;
            string dir = Path.GetDirectoryName(Assembly.GetExecutingAssembly().GetModules()[0].FullyQualifiedName) +
                @"\Reports\MRAutomationLogs\CrashAndTimeOut";
            for (int i = 0; i < systemCrumblingErrorWindows.Length; i++)
            {
                if (args.Name.Contains(systemCrumblingErrorWindows[i]))
                {
                    flag = true;
                    break;
                }
            }

            if (args.Name.Contains(crashWin) && isHooked)
            {
                FileLogger.Log(FileLogger.Severity.Error,
                    "Crash dialog has appeared - handling the crash");
                CapturErrorScreen(dir, "\\Crash" + DateTime.Now.Ticks.ToString() + ".bmp");
                HandleCrash();
            }
            else if (flag && isHooked)//args.Name.StartsWith(errorWin) || args.Name.StartsWith(reconstructor))
            {

                FileLogger.Log(FileLogger.Severity.Error,
                    "Crash dialog has appeared - handling the crash");
                CapturErrorScreen(dir, "\\Crash" + DateTime.Now.Ticks.ToString() + ".bmp");
                CloseDialog(args);
                HandleCrash();
            }
            else if (IsMessageIndicatingCrash(args.hWnd) && isHooked)
            {
                FileLogger.Log(FileLogger.Severity.Error,
                    "Dialog with message crash appeared - handling the crash");
                CapturErrorScreen(dir, "\\Crash" + DateTime.Now.Ticks.ToString() + ".bmp");
                CloseDialog(args);
                HandleCrash();
            }
            else
            {
                bool cont = false;
                bool captureWindow = false;
                bool captureScreen = false;
                foreach (windowsToWatch win in windowList)
                {
                    if (args.Name.Contains(win.windowName))
                    {
                        cont = true;
                        captureWindow = win.captureWindow;
                        captureScreen = win.captureScreen;
                        break;
                    }
                }
                if (cont)
                {
                    CloseDialog(args, captureWindow, captureScreen);
                }
            }
        }

        private static void CloseDialog(WinEventArgs args, bool captureWindow = false, bool captureScreen = false)
        {
            string windowTitle = args.Name;

            Thread.Sleep(50);
            try
            {
                NativeMethods.RECT rect = new NativeMethods.RECT();
                bool b = NativeMethods.GetClientRect((int)args.hWnd.ToInt32(), out rect);
                FileLogger.Log(FileLogger.Severity.Information,
                    "Modal Window: " + windowTitle + " appeared");

                List<IntPtr> children = NativeAPI.GetChildWindows(args.hWnd);
                string[] buttons = { "Ok", "OK", "Yes", "Proceed", "Exit", "Close Field Service", "Close", "Confirm and Start", "Hide" };
                if (!Playback.IsInitialized)
                {
                    Playback.Initialize();
                    Microsoft.VisualStudio.TestTools.UITesting.Mouse.MouseMoveSpeed = 4000;
                    Microsoft.VisualStudio.TestTools.UITesting.Mouse.MouseDragSpeed = 4000;
                }
                IntPtr iHaveReadCheckBox = IntPtr.Zero;
                iHaveReadCheckBox = children.Find(delegate(IntPtr x)
                {
                    return NativeAPI.GetText(x).ToLower().Contains("i have read");
                });
                if (iHaveReadCheckBox != IntPtr.Zero)
                {
                    FileLogger.Log(FileLogger.Severity.Information, "iHaveReadCheckBox Found!");
                    Microsoft.VisualStudio.TestTools.UITesting.Mouse.Click(NativeAPI.GetCenter(iHaveReadCheckBox));
                    Thread.Sleep(3500);
                }
                if (children.Count > 0)
                {
                    FileLogger.Log(FileLogger.Severity.Information, "No. of child ptrs: " + children.Count);
                    foreach (string button in buttons)
                    {
                        List<IntPtr> buttonPtr = children.FindAll(delegate(IntPtr x)
                        {
                            return NativeAPI.GetText(x).Contains(button) && NativeAPI.IsWindowVisible(x) &&
                                NativeAPI.GetText(x).Trim().Length == button.Length;
                        });
                        if (buttonPtr.Count > 0)
                        {
                            FileLogger.Log(FileLogger.Severity.Information, "Clicking " + NativeAPI.GetText(buttonPtr[0]) + buttonPtr);
                            Thread.Sleep(sleepCount);
                            if (isCaptureWindowsOn)
                            {
                                try
                                {
                                    if (!Directory.Exists(GeneralConfiguration.executingAssemblyLocation + expectedWindowStore))
                                    {
                                        Directory.CreateDirectory(GeneralConfiguration.executingAssemblyLocation + expectedWindowStore);
                                    }
                                    string[] knownWindowFiles = Directory.GetFiles(GeneralConfiguration.executingAssemblyLocation
                                        + expectedWindowStore);

                                    Microsoft.VisualStudio.TestTools.UITesting.Mouse.Click(NativeAPI.GetLocation(args.hWnd));

                                    Bitmap actualImageBitmap = new Bitmap(UITestControlFactory.FromWindowHandle(args.hWnd).CaptureImage());
                                    bool matched = false;
                                    for (int i = 0; i < knownWindowFiles.Length; i++)
                                    {
                                        if (ImageCapture.PixelCompare(actualImageBitmap,
                                            new Bitmap(knownWindowFiles[i])))
                                        {
                                            FileLogger.Log(FileLogger.Severity.Information, "Found window: " + windowTitle +
                                                " same as " + knownWindowFiles[i]);
                                            matched = true;
                                            break;
                                        }
                                    }
                                    if (!matched)
                                    {
                                        string fileName = GeneralConfiguration.executingAssemblyLocation + expectedWindowStore + "\\" +
                                            windowTitle + DateTime.Now.Ticks + ".PNG";
                                        FileLogger.Log(FileLogger.Severity.Information, "Window filename: " + fileName);
                                        actualImageBitmap.Save(fileName, ImageFormat.Png);
                                        actualImageBitmap.Dispose();
                                    }
                                }
                                catch (Exception e)
                                {
                                    FileLogger.Log(FileLogger.Severity.Exception,
                                        "Exception while capturing windows in crashnotifier: " + e.ToString());
                                }
                            }
                            else if (storeCompareWindows)
                            {
                                try
                                {
                                    string[] knownWindowFiles = Directory.GetFiles(Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location)
                                        + expectedWindowStore);
                                    Microsoft.VisualStudio.TestTools.UITesting.Mouse.Click(NativeAPI.GetLocation(args.hWnd));
                                    Bitmap actualImageBitmap = new Bitmap(UITestControlFactory.FromWindowHandle(args.hWnd).CaptureImage());
                                    bool matched = false;
                                    string matchedFile = string.Empty;
                                    for (int i = 0; i < knownWindowFiles.Length; i++)
                                    {
                                        if (ImageCapture.PixelCompare(actualImageBitmap,
                                            new Bitmap(knownWindowFiles[i])))
                                        {
                                            FileLogger.Log(FileLogger.Severity.Information, "Found window: " + windowTitle +
                                                " same as " + knownWindowFiles[i]);
                                            matched = true;
                                            matchedFile = knownWindowFiles[i];
                                            break;
                                        }
                                    }
                                    string savedFileName = string.Empty;
                                    if (!matched)
                                    {
                                        if (!Directory.Exists(GeneralConfiguration.executingAssemblyLocation + actualWindowStore))
                                        {
                                            Directory.CreateDirectory(GeneralConfiguration.executingAssemblyLocation + actualWindowStore);
                                        }
                                        savedFileName = GeneralConfiguration.executingAssemblyLocation + actualWindowStore + "\\" +
                                            windowTitle + DateTime.Now.Ticks + ".bmp";
                                        actualImageBitmap.Save(savedFileName);
                                    }
                                    windowsDiposed.Add(windowTitle + "," + matched.ToString() + "," + matchedFile + "," + savedFileName);
                                    actualImageBitmap.Dispose();
                                }
                                catch (Exception e)
                                {
                                    FileLogger.Log(FileLogger.Severity.Exception,
                                        "Exception while store and comparing windows in crashnotifier: " + e.ToString());
                                }
                            }
                            if (captureScreen)
                            {
                                string dir = Path.GetDirectoryName(Assembly.GetExecutingAssembly().GetModules()[0].FullyQualifiedName) +
                                    actualWindowStore;
                                CapturErrorScreen(dir, "\\" + windowTitle + DateTime.Now.Ticks.ToString() + ".bmp");
                            }
                            Microsoft.VisualStudio.TestTools.UITesting.Mouse.Click(NativeAPI.GetLocation(args.hWnd));
                            Thread.Sleep(100);
                            Microsoft.VisualStudio.TestTools.UITesting.Mouse.Click(NativeAPI.GetCenter(buttonPtr[0]));
                            break;
                        }
                    }
                }
            }

            catch (Exception e)
            {
                FileLogger.Log(FileLogger.Severity.Exception, e.Message);
            }
        }

        private static void WinEvnt_WindowsDialogPopup(WinEventArgs args)
        {
            try
            {
                Thread.Sleep(50);
                string dir = Path.GetDirectoryName(Assembly.GetExecutingAssembly().GetModules()[0].FullyQualifiedName) +
                @"\Reports\MRAutomationLogs\CrashAndTimeOut";
                if (args.Name.Contains(crashWin))
                {
                    FileLogger.Log(FileLogger.Severity.Error,
                        "Crash dialog has appeared - handling the Crash now");
                    CapturErrorScreen(dir, "\\Crash" + DateTime.Now.Ticks.ToString() + ".bmp");
                    HandleCrash();
                }
            }
            catch (Exception e)
            {
                FileLogger.Log(FileLogger.Severity.Exception, e.Message);
            }

        }

        /// <summary>
        /// Checks if message indicates a crash
        /// </summary>
        /// <param name="ptr">window handle</param>
        /// <returns>true if message has keyword crash, false otherwise</returns>
        private static bool IsMessageIndicatingCrash(IntPtr ptr)
        {
            bool flag = false;
            List<IntPtr> children = NativeAPI.GetChildWindows(ptr);
            List<IntPtr> crashedTextChild = children.FindAll(delegate(IntPtr x)
            {
                return NativeAPI.GetText(x).Contains("crashed");
            });

            List<IntPtr> abortedTextChild = children.FindAll(delegate(IntPtr x)
            {
                return NativeAPI.GetText(x).Contains("aborted");
            });

            if (crashedTextChild.Count > 0 || abortedTextChild.Count > 0)
            {
                flag = true;
            }
            return flag;
        }


        private static void CapturErrorScreen(string dir, string filename)
        {
            using (Bitmap bmp = new Bitmap(Screen.PrimaryScreen.Bounds.Width,
                Screen.PrimaryScreen.Bounds.Height,
                PixelFormat.Format32bppArgb))
            {
                using (Graphics graph = Graphics.FromImage(bmp))
                {
                    graph.CopyFromScreen(Screen.PrimaryScreen.Bounds.X,
                        Screen.PrimaryScreen.Bounds.Y,
                        0,
                        0,
                        Screen.PrimaryScreen.Bounds.Size,
                        CopyPixelOperation.SourceCopy);
                }
                if (!Directory.Exists(dir))
                {
                    Directory.CreateDirectory(dir);
                }
                bmp.Save(dir + filename, ImageFormat.Png);
            }
        }

        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            CleanupForExit();
        }

        private void CleanupForExit()
        {
            if (Playback.IsInitialized)
            {
                Playback.Cleanup();
            }
            WinEvnt.Detach();
        }

        /// <summary>
        /// Cleanup before closing the form
        /// </summary>
        public void Cleanup()
        {
            CleanupForExit();
        }

        private void CrashNotifier_Load(object sender, EventArgs e)
        {
            this.Hide();
        }

        private void MRDialogNotifier_Shown(object sender, EventArgs e)
        {
            this.Hide();
        }
    }
}

#region Revision History
/*
 * 2-13-2011  : Added by Girish for Crash notifier component
 */
#endregion

