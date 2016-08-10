/*  ----------------------------------------------------------------------------
 *  MR Automation : Philips Healthcare Bangalore Center of Competence
 *  ----------------------------------------------------------------------------
 *  Console Runner
 *  ----------------------------------------------------------------------------
 *  File:       Program.cs
 *  Author:     Pattabhirama  Pandit
 *  Creation Date: 2/13/2011
 *  ----------------------------------------------------------------------------
 */

using System;
using System.Windows.Forms;
using Philips.MRAutomation.Foundation.FrameworkCore;
using Philips.MRAutomation.Foundation.LoggingUtility;

namespace Philips.MRAutomation.Foundation.CrashNotifier
{
    public class NotifyCrash
    {
        public delegate void ForwardCrashHandler();
        public event ForwardCrashHandler ForwardHandleCrash;
        //public static bool applicationCrashed = false;
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        public void Start()
        {
            
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            MRDialogNotifier cn = new MRDialogNotifier();

            MRDialogNotifier.HandleCrash += new MRDialogNotifier.CrashHandler(cn_HandleCrash);
            try
            {   
                Application.Run(cn);    
            }
            catch (Exception threadex)
            {   
                cn.Close();
                cn.Cleanup();           
                FileLogger.Log(FileLogger.Severity.Information, 
                    "Gracefully terminating crash notifier thread. " + threadex.Message);                
            }
        }

        /// <summary>
        /// Turns on Window watcher, but populating the window list
        /// </summary>
        public static void TurnOnWindowWatcher()
        {            
            MRDialogNotifier.SetWindowWatcher();
        }

        public static void UnHook()
        {
            try
            {
                FileLogger.Log(FileLogger.Severity.Information,
                    "Unhooking the crash notifier");
                MRDialogNotifier.isHooked = false;
            }
            catch (Exception e)
            {
                FileLogger.Log(FileLogger.Severity.Exception,
                    "Exception while unhooking crash notifier " + e.ToString());
            }
        }

        public static void Hook()
        {
            try
            {
                FileLogger.Log(FileLogger.Severity.Information,
                    "Hooking the crash notifier");
                MRDialogNotifier.isHooked = true;
            }
            catch (Exception e)
            {
                FileLogger.Log(FileLogger.Severity.Exception,
                    "Exception while hooking crash notifier " + e.ToString());
            }

        }
        /// <summary>
        /// Turns off Window watcher, but clearing the window list
        /// </summary>
        public static void TurnOffWindowWatcher()
        {            
            MRDialogNotifier.ReSetWindowWatcher();
        }

        /// <summary>
        /// Is window watcher on
        /// </summary>
        /// <returns>true if on, else false</returns>
        public static bool IsWindowWatcherOn()
        {
            return MRDialogNotifier.IsWindowWatcherOn();
        }
        /// <summary>
        /// Event handler callback for crash, it also  forwards the crash
        /// </summary>
        void cn_HandleCrash()
        {
            FileLogger.Log(FileLogger.Severity.Trace, "Forwarding the crash handler event");
            ForwardHandleCrash();
        }
              

        /// <summary>
        /// Sets the application crashed flag, flag is set from Console Runner
        /// </summary>
        /// <param name="value">value to set</param>
        public static void SetApplicationCrashedFlag(bool value)
        {
            GeneralConfiguration.applicationCrashed = value;
            if (value == false)
            {
                MRDialogNotifier.storeCompareWindows = false;
            }
        }

        /// <summary>
        /// Get the application crashed flag
        /// </summary>
        /// <returns></returns>
        public static bool GetApplicationCrashedFlag()
        {
            return GeneralConfiguration.applicationCrashed;        
        }

        /// <summary>
        /// Add windows to watch
        /// </summary>
        /// <param name="windowName">window title</param>
        /// <param name="captureWindow">captureWindow</param>
        /// <param name="captureScreen">captureScreen</param>
        public static void AddWindowToWatch (string windowName, bool captureWindow, bool captureScreen)
        {
            MRDialogNotifier.AddWindowToWatch(windowName, captureWindow, captureScreen);
        }

        public static void RemoveWindowFromBeingWatched (string windowName)
        {
            MRDialogNotifier.RemoveWindowFromBeingWatched(windowName);
        }

        public static string[] GetWindowsDisposed ()
        {
           return MRDialogNotifier.GetWindowsDisposed();
        }

        public static void StartCaptureAndCompare ()
        {
            MRDialogNotifier.storeCompareWindows = true;
        }
    }
}

#region Revision History
/*
 * 2-13-2011  : Added by Girish for Crash notifier component
 */
#endregion
