#region Header
// ===================================================================================
// FileName    : WinEventHandler.cs
// 
// Namespace   : Telstra.IDA.Client.MSAAWrapper
//
// Author      : Travis Draper
// Date        : 20.Oct.2006
// QA Complete : 
// Summary     : This class allows .NET apps to listen for events triggered in other
//					applications using MSAA
//
// ===================================================================================
#endregion Header

using System;
using System.Collections;
using System.Runtime.InteropServices;
using System.Text;
using System.Diagnostics;
using Accessibility;
using Philips.MRAutomation.Foundation.WinEventHook;

namespace MSAAWrapper
{
    /// <summary>
    /// This is the struct that is returned as an argument when the desired event is triggered
    /// </summary>
    public struct WinEventArgs
    {
        public string Name;
        public string State;
        public IAccessible AccObj;
        public IntPtr hWnd;
    }

    public class WinEventHandler
    {
        public delegate void WinEventDelegate(WinEventArgs args);

        //The various events that consumers can subscribe to
        // public event WinEventDelegate ButtonStateChanged;
        public event WinEventDelegate ModalDialogPopup;
        public event WinEventDelegate WindowsDialogPopup;
        public event WinEventDelegate WindowReOrdered;
        public event WinEventDelegate WindowsDialogDestroyed;
        public event WinEventDelegate ObjectCreated;
        public event WinEventDelegate ObjectDestroyed;

        private GCHandle _gch;
        private long _hook;
        private int _processId = 0;
        public bool _listening;

        public WinEventHandler(int processId)
        {
            _processId = processId;
            _listening = false;
        }

        /// <summary>
        /// Start the EventHandler listening for events in the external process
        /// </summary>
        public void Listen()
        {
            //if(_processId==0) 
            //{
            //    throw new Exception("No process ID specified to listen for events in.");
            //}

            //Only setup the listener once even if Listen() is called repeateadly
            if (!_listening)
            {
                // Callback procedure for the Windows Event hook.
                NativeMethods.WinEventProc wep = new NativeMethods.WinEventProc(EventHandler);

                // Allocate a Garbage Collection Handle. This prevents .NET GCing the callback function
                // when it is still in use by unmanaged code (the WinEventHook).
                _gch = GCHandle.Alloc(wep);

                // Create the hook to detect the desired events.
                // Modify the eventMin and eventMax parameters to suit the events that need to be
                // monitored.
                // No thread ID is specified here, so the events for all threads in the process
                // will be monitored.
                _hook = NativeMethods.SetWinEventHook(NativeMethods.EVENT_SYSTEM_DIALOGSTART, 
                    NativeMethods.EVENT_OBJECT_STATECHANGE, 
                    (System.IntPtr)0, wep, _processId, 0, NativeMethods.WINEVENT_OUTOFCONTEXT);

                _listening = true;

                Debug.WriteLine("Set up WinEvent hook: " + _hook.ToString());
            }
            else
            {
                Debug.WriteLine("Already listening...");
            }
        }

        /// <summary>
        /// Stop the EventHandler listening for events in the external process
        /// </summary>
        public void Detach()
        {
            Unacquire();
        }

        /// <summary>
        /// This is the callback function that MSAA will call when events are raised in the external
        /// process
        /// </summary>
        /// <param name="hWinEventHook">Handle to an event hook function. This value is returned by 
        /// SetWinEventHook when the hook function is installed and is specific to each instance of 
        /// the hook function. </param>
        /// <param name="iEvent">Specifies the event that occurred. This value is one of the event 
        /// constants.</param>
        /// <param name="hwnd">Handle to the window that generates the event, or NULL if no window 
        /// is associated with the event.</param>
        /// <param name="idObject">Identifies the object associated with the event. This is one of the 
        /// MSAA object identifiers or a custom object ID. </param>
        /// <param name="idChild">Identifies whether the event was triggered by an object or a child 
        /// element of the object. If this value is CHILDID_SELF, the event was triggered by the object; 
        /// otherwise, this value is the child ID of the MSAA element that triggered the event.</param>
        /// <param name="dwEventThread">Identifies the thread that generated the event, or the thread that 
        /// owns the current window. </param>
        /// <param name="dwmsEventTime">Specifies the time, in milliseconds, that the event was generated.</param>
        private void EventHandler(System.IntPtr hWinEventHook, int iEvent, System.IntPtr hwnd, 
                                    int idObject, int idChild, int dwEventThread, int dwmsEventTime)
        {
            //Filter out any events triggered by objects we're not interested in
            if (idObject != NativeMethods.OBJID_CARET && idObject != NativeMethods.OBJID_CURSOR) 
            {
                //Only process the events we're interested in
                switch((uint)iEvent)
                {
                    case NativeMethods.EVENT_OBJECT_REORDER:
                    case NativeMethods.EVENT_OBJECT_SHOW:
                    case NativeMethods.EVENT_OBJECT_STATECHANGE:
                    case NativeMethods.EVENT_SYSTEM_DIALOGSTART:
                    case NativeMethods.EVENT_SYSTEM_DIALOGEND:
                    case NativeMethods.EVENT_OBJECT_DESTROY:
                    case NativeMethods.EVENT_OBJECT_CREATE:

                        StringBuilder strClassName = new StringBuilder(256);

                        int y = NativeMethods.GetClassName(hwnd,strClassName, 256);
                        string className = strClassName.ToString(); 
                        int length = NativeMethods.GetWindowTextLength(hwnd);
    StringBuilder sb = new StringBuilder(length + 1);
    int x=NativeMethods.GetWindowText(hwnd, sb, sb.Capacity);
    string text=sb.ToString();

                        //Try to get the MSAA object that triggered the event
                        
                        
                            //Get some details of the MSAA object
                            MSAAHelper helper = new MSAAHelper();
                        
                            //Debug.WriteLine(string.Format("{8} {0} - class: {3}, hwnd {4}, Object:{1}, Child:{2}, name:{5}, role:{6}, state:{7} ",
                            //Enum.GetName(typeof(NativeWin32.WinEvents), iEvent), idObject, idChild, className, 
                            //Conversion.IntToHex((int)hwnd, 4), name, role, state, DateTime.Now.ToLongTimeString()));

                            //Create a struct that we can use for the event parameter
                            WinEventArgs args = new WinEventArgs();
                            //args.AccObj = accObj;
                            args.Name = text;
                            //args.State = state;
                            args.hWnd = hwnd;

                            
                            //This combination checks if a child HTA window is displayed
                            //as a modal pop-up
                            if (iEvent == NativeMethods.EVENT_OBJECT_SHOW)
                            {
                                //Raise the event if required
                                if (ModalDialogPopup != null)
                                {
                                    ModalDialogPopup(args);
                                }
                            }

                            //This combination checks if a Windows message box is displayed
                            if (iEvent == NativeMethods.EVENT_SYSTEM_DIALOGSTART)
                            {
                                //Raise the event if required
                                if (WindowsDialogPopup != null)
                                {
                                    WindowsDialogPopup(args);
                                }
                            }

                            //There is a very specific requirement to check when a tree
                            //view was populated and this is the only consistent way to check
                            //when this happened.
                            if (iEvent == NativeMethods.EVENT_OBJECT_REORDER)
                            {
                                //Raise the event if required
                                if (WindowReOrdered != null)
                                {
                                    WindowReOrdered(args);
                                }
                            }
                            if (iEvent == NativeMethods.EVENT_OBJECT_DESTROY)
                            {
                                if (ObjectDestroyed != null)
                                {
                                    ObjectDestroyed(args);
                                }
                            }
                            if (iEvent == NativeMethods.EVENT_SYSTEM_DIALOGEND)
                            {
                                if (WindowsDialogDestroyed != null)
                                {
                                    WindowsDialogDestroyed(args);
                                }
                            }
                            if (iEvent == NativeMethods.EVENT_OBJECT_CREATE)
                            {
                                if (ObjectCreated != null)
                                {
                                    ObjectCreated(args);
                                }
                            }
                        
                        break;
                }
            }			
            

        }

        /// <summary>
        /// Returns the class name of the specified window
        /// </summary>
        /// <param name="hWnd"></param>
        /// <returns></returns>
        private string GetWindowClassName(IntPtr hWnd)
        {
            int result;
            StringBuilder className = new StringBuilder(100);
            result = NativeMethods.GetClassName(hWnd, className, className.Capacity);
            return className.ToString();
        }

        /// <summary>
        /// Unhooks the event listener from the process
        /// </summary>
        private void Unacquire()
        {
            // Free the WinEventHook handle and Garbage Collection handle.
            if(_gch.IsAllocated)
                _gch.Free();

            //If there is a valid hook, then remove it
            if (_hook != 0)
            {
                Debug.WriteLine("About to unhook " + _hook.ToString());

                bool result = NativeMethods.UnhookWinEvent((IntPtr)_hook);
                _hook = 0;
                _listening = false;

                Debug.WriteLine("Unhook result: " + result.ToString());
            }
            else
            {
                Debug.WriteLine("Nothing hooked up");
            }

        }
    }
}


