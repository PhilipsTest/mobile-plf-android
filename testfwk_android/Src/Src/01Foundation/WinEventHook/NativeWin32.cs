using System;
using System.Runtime.InteropServices;
using System.Text;
using Accessibility;

namespace Philips.MRAutomation.Foundation.WinEventHook
{
    /// <summary>
    /// Prototypes for the various Win32 calls required for the MSAA Wrapper to work
    /// </summary>
    internal class NativeMethods
    {
        [DllImport("user32.dll")]
        public static extern long SetWinEventHook(
            uint eventMin,
            uint eventMax,
            IntPtr hmodWinEventProc,
            WinEventProc lpfnWinEventProc,
            int idProcess,
            int idThread,
            uint dwflags
        );

        [DllImport("user32.dll")]
        public static extern bool UnhookWinEvent(IntPtr hWinEventHook);

        /// <summary>
        /// The callback delegate that gets called when MSAA detects an event
        /// </summary>
        /// <param name="hWinEventHook"></param>
        /// <param name="iEvent"></param>
        /// <param name="hwnd"></param>
        /// <param name="idObject"></param>
        /// <param name="idChild"></param>
        /// <param name="dwEventThread"></param>
        /// <param name="dwmsEventTime"></param>
        public delegate void WinEventProc(
            IntPtr hWinEventHook,
            int iEvent,
            IntPtr hwnd,
            int idObject,
            int idChild,
            int dwEventThread,
            int dwmsEventTime
        );

        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        public static extern int GetWindowTextLength(IntPtr hWnd);

        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        public static extern int GetClassName(IntPtr hWnd, StringBuilder lpClassName, int nMaxCount);

        #region "MSAA Prototypes"
        [DllImport("Oleacc.dll")]
        public static extern int AccessibleObjectFromWindow(
            IntPtr hwnd,
            int dwObjectID,
            ref Guid refID,
            ref Accessibility.IAccessible ppvObject);

        [DllImport("oleacc.dll")]
        public static extern uint AccessibleObjectFromEvent( 
            IntPtr hwnd, 
            uint dwObjectID, 
            uint dwChildID, 
            out IAccessible ppacc, 
            [MarshalAs(UnmanagedType.Struct)] out object pvarChild);

        [DllImport("Oleacc.dll")]
        public static extern int AccessibleChildren(
            Accessibility.IAccessible paccContainer, 
            int iChildStart, 
            int cChildren, 
            [Out] object[] rgvarChildren, 
            out int pcObtained);

        [DllImport("oleacc.dll")]
        public static extern uint GetRoleText( uint dwRole, [Out] StringBuilder lpszRole, uint cchRoleMax);

        [DllImport("oleacc.dll")]
        public static extern uint GetStateText( uint dwStateBit, [Out] StringBuilder lpszStateBit, uint cchStateBitMax );

        [DllImport("oleacc.dll", PreserveSig=false)]
        [return: MarshalAs(UnmanagedType.Interface)]
        public static extern object ObjectFromLresult(UIntPtr lResult, 
            [MarshalAs(UnmanagedType.LPStruct)] Guid refiid, IntPtr wParam);
        #endregion

        public struct RECT {
            public int left;
            public int top;
            public int right;
            public int bottom; 
            } //RECT, *PRECT; 
        //public struct WINDOWINFO
        //{
        //    public const Int32 cbSize = 128; //sizeof(WINDOWINFO);
        //    // public RECT rcWindow;
        //    // public RECT rcClient;
        //    // public Int32 dwStyle;
        //    // public Int32 dwExStyle;
        //    // public Int32 dwWindowStatus;
        //    // public Int32 cxWindowBorders;
        //    // public Int32 cyWindowBorders;
        //    // public char atomWindowType;
        //    // public char wCreatorVersion;
        // }  //*PWINDOWINFO, *LPWINDOWINFO;
        //public struct POINT { 
        //          public UInt32 x;
        //          public UInt32 y; 
        //        } //* POINT, *PPOINT; 

        #region "User32 Prototypes"
        [DllImport("user32.dll", CharSet = CharSet.Auto)]
        public static extern IntPtr FindWindowEx(
            int parent,
            int next,
            string lpszClass,
            string sWindowTitle);

       
        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Unicode, ThrowOnUnmappableChar = true)]
        public static extern IntPtr FindWindow(string lpClassName, string lpWindowName);

        
        [DllImport("user32.dll")]
        public static extern bool GetClientRect(int hWnd, out RECT lpRect);

        [DllImport("user32.dll", SetLastError=true, CharSet=CharSet.Auto)]
        public static extern uint RegisterWindowMessage(string lpString);

        [DllImport("user32.dll", SetLastError=true, CharSet=CharSet.Auto)]
        public static extern IntPtr SendMessageTimeout(IntPtr hWnd, 
            uint Msg, 
            UIntPtr wParam, 
            IntPtr lParam, 
            SendMessageTimeoutFlags fuFlags, 
            uint uTimeout, 
            out UIntPtr lpdwResult);

        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        public static extern IntPtr SendMessageTimeout(IntPtr windowHandle,
            uint Msg,
            IntPtr wParam,
            IntPtr lParam,
            SendMessageTimeoutFlags flags,
            uint timeout,
            out IntPtr result);
        
        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        public static extern IntPtr SetCapture(IntPtr hWnd);

        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        public static extern IntPtr ReleaseCapture();
        
        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        public static extern bool SetForegroundWindow( IntPtr hWnd);

        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        public static extern bool LockSetForegroundWindow( LockSetCaptureValues ulock);

        //[DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        //public static extern bool GetWindowInfo(IntPtr hWnd, out WINDOWINFO pwi);

        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        public static extern int GetWindowText(IntPtr hWnd,
                                        StringBuilder lpString,
                                        int nMaxCount);



        //[DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        //public static extern bool GetCursorPos(out POINT p);

        //[DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        //public static extern IntPtr WindowFromPoint( POINT Point );

        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        public static extern IntPtr GetWindowDC(IntPtr hwnd); // ret HDC handle to window DC

        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        public static extern int ReleaseDC( IntPtr HWND /* handle to window */,
                                            IntPtr HDC   /* handle to DC*/ );
        [DllImport("gdi32.dll")]
        public static extern bool Rectangle(
           IntPtr hdc,
           int ulCornerX, int ulCornerY,
           int lrCornerX, int lrCornerY);
        [DllImport("user32.dll")]
        public static extern bool InvalidateRect(
          IntPtr hWnd,           // handle to window
          ref RECT lpRect,  // rectangle coordinates
          bool bErase          // erase state
        );
        [DllImport("user32.dll")]
        public static extern bool UpdateWindow(
            IntPtr hWnd   // handle to window
        );
        [DllImport("user32.dll")]
        public static extern bool RedrawWindow(
          IntPtr  hWnd,               // handle to window
          ref RECT    lprcUpdate,  // update rectangle
          IntPtr  hrgnUpdate,         // handle to update region
          uint flags               // array of redraw flags
        );


        #endregion

        #region Enums

        [Flags] 
            public enum SendMessageTimeoutFlags : uint
        {
            SMTO_NORMAL         = 0x0000,
            SMTO_BLOCK          = 0x0001,
            SMTO_ABORTIFHUNG    = 0x0002,
            SMTO_NOTIMEOUTIFNOTHUNG = 0x0008
        }

        public enum NativeMsg : int
        {
            CHILDID_SELF = 0,
            CHILDID_1 = 1,
            OBJID_CLIENT = -4, //0xFFFFFFC
        }

        public enum Navigation : int
        {
            NAVDIR_UP           = 1,
            NAVDIR_DOWN         = 2,
            NAVDIR_LEFT         = 3,
            NAVDIR_RIGHT        = 4,
            NAVDIR_NEXT         = 5,
            NAVDIR_PREVIOUS     = 6,
            NAVDIR_FIRSTCHILD   = 7,
            NAVDIR_LASTCHILD    = 8,
        }

        public enum Selection : int
        {
            SELFLAG_NONE            = 0,
            SELFLAG_TAKEFOCUS       = 1,
            SELFLAG_TAKESELECTION   = 2,
            SELFLAG_EXTENDSELECTION = 4,
            SELFLAG_ADDSELECTION    = 8,
            SELFLAG_REMOVESELECTION = 16
        } 

        public enum WinEvents: uint
        {
            EVENT_MIN = 1,  

            EVENT_SYSTEM_ALERT = 2,
            EVENT_SYSTEM_FOREGROUND = 3,
            EVENT_SYSTEM_MENUSTART = 4, 
            EVENT_SYSTEM_MENUEND = 5,   
            EVENT_SYSTEM_MENUPOPUPSTART = 6,                                                           
            EVENT_SYSTEM_MENUPOPUPEND = 7,		
            EVENT_SYSTEM_CAPTURESTART = 8,  
            EVENT_SYSTEM_CAPTUREEND = 9,  
            EVENT_SYSTEM_MOVESIZESTART = 10,
            EVENT_SYSTEM_MOVESIZEEND = 11,                                                                   
            EVENT_SYSTEM_CONTEXTHELPSTART = 12,
            EVENT_SYSTEM_CONTEXTHELPEND = 13,                                                                   
            EVENT_SYSTEM_DRAGDROPSTART = 14,  
            EVENT_SYSTEM_DRAGDROPEND = 15, 
            EVENT_SYSTEM_DIALOGSTART = 16,                                                 
            EVENT_SYSTEM_DIALOGEND = 17, 
            EVENT_SYSTEM_SCROLLINGSTART = 18,
            EVENT_SYSTEM_SCROLLINGEND = 19,  
            EVENT_SYSTEM_SWITCHSTART = 20, 
            EVENT_SYSTEM_SWITCHEND = 21,
            EVENT_SYSTEM_MINIMIZESTART = 22,
            EVENT_SYSTEM_MINIMIZEEND = 23,                                                                   
              
            EVENT_OBJECT_CREATE = 32768,                                                                                                                                                                     
            EVENT_OBJECT_DESTROY = 32769,   
            EVENT_OBJECT_SHOW = 32770,
            EVENT_OBJECT_HIDE = 32771, 
            EVENT_OBJECT_REORDER = 32772,
            EVENT_OBJECT_FOCUS = 32773,
            EVENT_OBJECT_SELECTION = 32774,
            EVENT_OBJECT_SELECTIONADD = 32775,                                                                 
            EVENT_OBJECT_SELECTIONREMOVE = 32776,                                                               
            EVENT_OBJECT_SELECTIONWITHIN = 32777,                                                                  
                                                        
            EVENT_OBJECT_STATECHANGE = 32778,
            EVENT_OBJECT_LOCATIONCHANGE = 32779,                                                                 
            EVENT_OBJECT_NAMECHANGE = 32780,
            EVENT_OBJECT_DESCRIPTIONCHANGE = 32781,
            EVENT_OBJECT_VALUECHANGE = 32782,
            EVENT_OBJECT_PARENTCHANGE = 32783,  
            EVENT_OBJECT_HELPCHANGE = 32784,
            EVENT_OBJECT_DEFACTIONCHANGE = 32785,
            EVENT_OBJECT_ACCELERATORCHANGE = 32786,
                                                                                                                                                        
            EVENT_MAX = 2147483647,                                                            
                                                                   
        };
        #endregion

        #region Constants
        public static Guid IID_IAccessible = new Guid("618736E0-3C3D-11CF-810C-00AA00389B71");

        public static IntPtr S_OK = IntPtr.Zero;

        public const int STATE_SYSTEM_NORMAL = 0;
        public const int STATE_SYSTEM_UNAVAILABLE = 1;
        public const int STATE_SYSTEM_SELECTED = 2;
        public const int STATE_SYSTEM_FOCUSED = 4;
        public const int STATE_SYSTEM_PRESSED = 8;
        public const int STATE_SYSTEM_CHECKED = 16;
        public const int STATE_SYSTEM_MIXED = 32;
        public const int STATE_SYSTEM_READONLY = 64;
        public const int STATE_SYSTEM_HOTTRACKED = 128;
        public const int STATE_SYSTEM_DEFAULT = 256;
        public const int STATE_SYSTEM_EXPANDED = 512;
        public const int STATE_SYSTEM_COLLAPSED = 1024;
        public const int STATE_SYSTEM_BUSY = 2048;
        public const int STATE_SYSTEM_FLOATING = 4096;
        public const int STATE_SYSTEM_MARQUEED = 8192;
        public const int STATE_SYSTEM_ANIMATED = 16384;
        public const int STATE_SYSTEM_INVISIBLE = 32768;
        public const int STATE_SYSTEM_OFFSCREEN = 65536;
        public const int STATE_SYSTEM_SIZEABLE = 131072;
        public const int STATE_SYSTEM_MOVEABLE = 262144;
        public const int STATE_SYSTEM_SELFVOICING = 524288;
        public const int STATE_SYSTEM_FOCUSABLE = 1048576;
        public const int STATE_SYSTEM_SELECTABLE = 2097152;
        public const int STATE_SYSTEM_LINKED = 4194304;
        public const int STATE_SYSTEM_TRAVERSED = 8388608;
        public const int STATE_SYSTEM_MULTISELECTABLE = 16777216;
        public const int STATE_SYSTEM_EXTSELECTABLE = 33554432;
        public const int STATE_SYSTEM_VALID = 536870911;

        public enum LockSetCaptureValues 
        { LSFW_LOCK = 1, LSFW_UNLOCK = 2}



        //MSAA WinEvent constants
        public const uint EVENT_MAX = 2147483647;                                                             
        public const uint EVENT_MIN = 1;                                                                      
        public const uint EVENT_OBJECT_ACCELERATORCHANGE = 32786;
        public const uint EVENT_OBJECT_CREATE = 32768;                                                      
        public const uint EVENT_OBJECT_DEFACTIONCHANGE = 32785;                                                                  
        public const uint EVENT_OBJECT_DESCRIPTIONCHANGE = 32781;                                                                  
        public const uint EVENT_OBJECT_DESTROY = 32769;                                                                  
        public const uint EVENT_OBJECT_FOCUS = 32773;                                                                  
        public const uint EVENT_OBJECT_HELPCHANGE = 32784;                                                                  
        public const uint EVENT_OBJECT_HIDE = 32771;                                                                  
        public const uint EVENT_OBJECT_LOCATIONCHANGE = 32779 ;                                                                 
        public const uint EVENT_OBJECT_NAMECHANGE = 32780;                                                                  
        public const uint EVENT_OBJECT_PARENTCHANGE = 32783;                                                                  
        public const uint EVENT_OBJECT_REORDER = 32772;                                                                  
        public const uint EVENT_OBJECT_SELECTION = 32774;                                                                  
        public const uint EVENT_OBJECT_SELECTIONADD = 32775;                                                                  
        public const uint EVENT_OBJECT_SELECTIONREMOVE = 32776 ;                                                                 
        public const uint EVENT_OBJECT_SELECTIONWITHIN = 32777;                                                                  
        public const uint EVENT_OBJECT_SHOW = 32770;                                                                  
        public const uint EVENT_OBJECT_STATECHANGE = 32778;                                                                  
        public const uint EVENT_OBJECT_VALUECHANGE = 32782;                                                                  
        public const uint EVENT_SYSTEM_ALERT = 2;                                                                      
        public const uint EVENT_SYSTEM_CAPTUREEND = 9;                                                                      
        public const uint EVENT_SYSTEM_CAPTURESTART = 8;                                                                      
        public const uint EVENT_SYSTEM_CONTEXTHELPEND = 13;                                                                     
        public const uint EVENT_SYSTEM_CONTEXTHELPSTART = 12;                                                                     
        public const uint EVENT_SYSTEM_DIALOGEND = 17;                                                                     
        public const uint EVENT_SYSTEM_DIALOGSTART = 16;                                                                    
        public const uint EVENT_SYSTEM_DRAGDROPEND = 15;                                                                     
        public const uint EVENT_SYSTEM_DRAGDROPSTART = 14;                                                                     
        public const uint EVENT_SYSTEM_FOREGROUND = 3;                                                                      
        public const uint EVENT_SYSTEM_MENUEND = 5;                                                                      
        public const uint EVENT_SYSTEM_MENUPOPUPEND = 7;                                                                      
        public const uint EVENT_SYSTEM_MENUPOPUPSTART = 6;                                                                      
        public const uint EVENT_SYSTEM_MENUSTART = 4;                                                                      
        public const uint EVENT_SYSTEM_MINIMIZEEND = 23;                                                                     
        public const uint EVENT_SYSTEM_MINIMIZESTART = 22;                                                                     
        public const uint EVENT_SYSTEM_MOVESIZEEND = 11;                                                                     
        public const uint EVENT_SYSTEM_MOVESIZESTART = 10;                                                                     
        public const uint EVENT_SYSTEM_SCROLLINGEND = 19;                                                                     
        public const uint EVENT_SYSTEM_SCROLLINGSTART = 18;                                                                     
        public const uint EVENT_SYSTEM_SOUND = 1;                                                                      
        public const uint EVENT_SYSTEM_SWITCHEND = 21;                                                                     
        public const uint EVENT_SYSTEM_SWITCHSTART = 20;          

        //MSAA object IDs
        public const int OBJID_WINDOW = 0;
        public const int OBJID_SYSMENU = -1;
        public const int OBJID_TITLEBAR = -2;
        public const int OBJID_MENU = -3;
        public const int OBJID_CLIENT = -4;
        public const int OBJID_VSCROLL = -5;
        public const int OBJID_HSCROLL = -6;
        public const int OBJID_SIZEGRIP = -7;
        public const int OBJID_CARET = -8;
        public const int OBJID_CURSOR = -9;
        public const int OBJID_ALERT = -10;
        public const int OBJID_SOUND = -11;

        public const int GCL_HICON = -14;
        public const int GWL_EXSTYLE = -20;
        public const int SW_HIDE = 0;
        public const int SW_NORMAL = 1;
        public const int SW_MAXIMIZE = 3;
        public const int SW_MINIMIZE = 6;
        public const int SW_SHOWDEFAULT = 10;
        public const int SW_RESTORE = 9;
        public const int WINEVENT_OUTOFCONTEXT = 0x0000;
        public const int WINEVENT_SKIPOWNTHREAD = 0x0001;
        public const int WINEVENT_SKIPOWNPROCESS = 0x0002;
        public const int WINEVENT_INCONTEXT = 0x0004;
        public const int WS_EX_APPWINDOW = 0x40000;
        public const int WS_MAXIMIZE = 0x1000000;
        public const int WS_MINIMIZE = 0x20000000;
        public const uint WS_POPUP = 0x80000000;

/*
 * RedrawWindow() flags
 */
        public const uint  RDW_INVALIDATE       =   0x0001;
        public const uint  RDW_INTERNALPAINT    =   0x0002;
        public const uint  RDW_ERASE            =   0x0004;

        public const uint  RDW_VALIDATE        =    0x0008;
        public const uint  RDW_NOINTERNALPAINT  =   0x0010;
        public const uint  RDW_NOERASE          =   0x0020;

        public const uint  RDW_NOCHILDREN       =   0x0040;
        public const uint  RDW_ALLCHILDREN      =   0x0080;

        public const uint  RDW_UPDATENOW        =   0x0100;
        public const uint  RDW_ERASENOW         =   0x0200;

        public const uint  RDW_FRAME            =   0x0400;
        public const uint RDW_NOFRAME           =   0x0800;

        #endregion 
    }
}


