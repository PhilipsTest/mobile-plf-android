using System;
using System.Collections.Generic;
using System.Drawing;
using System.Runtime.InteropServices;
using System.Threading;  

namespace Philips.MRAutomation.Foundation.FrameworkCore.Common
{
    public class Mouse
    {
        [DllImport("user32", EntryPoint = "SendInput")]
        private static extern int SendInput(int numberOfInputs, ref NativeAPI.Input input, int structSize);

        [DllImport("kernel32", EntryPoint = "GetLastError")]
        private static extern int GetLastError();

        [DllImport("user32.dll")]
        private static extern IntPtr GetMessageExtraInfo();

        [DllImport("user32.dll")]
        private static extern IntPtr GetCursor();

        [DllImport("user32.dll")]
        private static extern bool GetCursorPos(ref Point cursorInfo);

        [DllImport("user32.dll")]
        private static extern bool SetCursorPos(int x, int y);

        [DllImport("user32.dll")]
        private static extern bool GetCursorInfo(ref NativeAPI.CursorInfo cursorInfo);

        [DllImport("user32.dll")]
        private static extern short GetDoubleClickTime();

        public static Mouse Instance = new Mouse();
        private DateTime lastClickTime = DateTime.Now;
        private readonly short doubleClickTime = GetDoubleClickTime();
        private Point lastClickLocation;
        private readonly int extraMillisecondsBecauseOfBugInWindows = 13;

        private Mouse() {}

        /// <summary>
        /// This will take the Screen Coordinates or it will give Screen Coordinates.
        /// </summary>
        public virtual Point Location
        {
            get
            {
                Point point = new Point();
                GetCursorPos(ref point);
                return point;
            }
            set { SetCursorPos(value.X, value.Y); }
        }

        public virtual NativeAPI.MouseCursor Cursor
        {
            get
            {
                NativeAPI.CursorInfo cursorInfo = NativeAPI.CursorInfo.New();
                GetCursorInfo(ref cursorInfo);
                int i = cursorInfo.handle.ToInt32();
                return (NativeAPI.MouseCursor)Enum.ToObject(typeof(NativeAPI.MouseCursor), i);
            }
        }

        public virtual void RightClick()
        {
            SendInput(NativeAPI.Input.Mouse(MouseInput(WindowsConstants.MOUSEEVENTF_RIGHTDOWN)));
            SendInput(NativeAPI.Input.Mouse(MouseInput(WindowsConstants.MOUSEEVENTF_RIGHTUP)));
        }

        public virtual void Click()
        {
            Point clickLocation = Location;
            if (lastClickLocation.Equals(clickLocation))
            {
                int timeout = doubleClickTime - DateTime.Now.Subtract(lastClickTime).Milliseconds;
                if (timeout > 0) Thread.Sleep(timeout + extraMillisecondsBecauseOfBugInWindows);
            }
            MouseLeftButtonUpAndDown();
            lastClickTime = DateTime.Now;
            lastClickLocation = Location;
        }

        public static void LeftUp()
        {
            SendInput(NativeAPI.Input.Mouse(MouseInput(WindowsConstants.MOUSEEVENTF_LEFTUP)));
        }

        public static void LeftDown()
        {
            SendInput(NativeAPI.Input.Mouse(MouseInput(WindowsConstants.MOUSEEVENTF_LEFTDOWN)));
        }
        public static void RightDown()
        {
            SendInput(NativeAPI.Input.Mouse(MouseInput(WindowsConstants.MOUSEEVENTF_RIGHTDOWN)));
        }
        public static void RightUp()
        {
            SendInput(NativeAPI.Input.Mouse(MouseInput(WindowsConstants.MOUSEEVENTF_RIGHTUP)));
        }
        public static void MiddleDown()
        {
            SendInput(NativeAPI.Input.Mouse(MouseInput(WindowsConstants.MOUSEEVENTF_MIDDLEDOWN)));
        }
        public static void MiddleUp()
        {
            SendInput(NativeAPI.Input.Mouse(MouseInput(WindowsConstants.MOUSEEVENTF_MIDDLEUP)));
        }
        public virtual void DoubleClick(Point point)
        {
            Location = point;
            MouseLeftButtonUpAndDown();
            MouseLeftButtonUpAndDown();
        }

        private static void SendInput(NativeAPI.Input input)
        {
            SendInput(1, ref input, Marshal.SizeOf(typeof(NativeAPI.Input)));
        }

        private static NativeAPI.MouseInput MouseInput(uint command)
        {
            return new NativeAPI.MouseInput(command, GetMessageExtraInfo());
        }

        public virtual void RightClick(Point point)
        {
            Location = point;
            RightClickHere();
        }

        internal virtual void RightClickHere()
        {
            RightClick();
        }

       
        public virtual void Click(Point point)
        {
            Location = point;
            Click();
        }


     


       

        /// <summary>
        /// Function Replacing the obsolted 
        /// </summary>
        /// <param name="src">The clickable point of the element being dragged</param>
        /// <param name="dst">The point at which the element has to be dropped</param>
        public virtual void DragAndDrop(Point src, Point dst,int moveTime)
        {
            Location = src;
            MouseLeftButtonUpAndDown();
            LeftDown();
            Thread.Sleep(500);  
            Move(src, dst, moveTime);
            Thread.Sleep(500);
            LeftUp();
            Thread.Sleep(500);
            MouseLeftButtonUpAndDown();
        }
        /// <summary>
        /// Function to aid Drag and drop; Code by Shyam
        /// </summary>
        /// <param name="src"></param>
        /// <param name="dst"></param>
        /// <param name="delay"></param>
        private void Move(Point src, Point dst, int delay)
        {
            MouseExtension.move(src.X, src.Y, dst.X, dst.Y, delay);
        }
        public static int MakeLong(short lowPart, short highPart)
        {
            return (int)(((ushort)lowPart) | (uint)(highPart << 16));
        }

        public static void MouseLeftButtonUpAndDown()
        {
            LeftDown();
            LeftUp();
        }

        public void MoveOut()
        {
            Location = new Point(0,0);
        }

      
    }
    /// <summary>
    /// New class added to aid drag and drop;
    /// Code by Shyam.
    /// </summary>
    public class MouseExtension
    {
        [StructLayout(LayoutKind.Sequential)]
        public struct POINTAPI
        {
            public int x;
            public int y;
        };
        //Public Const WM_MOUSEMOVE = &H200;

        public const uint WM_MOUSEMOVE = 512;


        [DllImport("User32.dll")]
        public static extern int ClientToScreen(IntPtr hwnd, ref POINTAPI lpPoint);
        [DllImport("User32.dll")]
        public static extern int SendMessage(IntPtr hwnd, uint Msg, int wParam, int lParam);
        [DllImport("User32.dll")]
        public static extern int SetCursorPos(int x, int y);

        public static void move(int x1, int y1, int x2, int y2, int delay)
        {


            float m = (float)(y2 - y1) / (float)(x2 - x1);
            float c = (float)y2 - (m * (float)x2);
            if (y2 > y1)
            {
                //horizontal line
                if (m == 0)
                {
                    for (int x = x1; x <= x2; x += 1)
                    {
                        SetCursorPos(x, y1);
                        Thread.Sleep(delay);
                    }

                }
                else
                {
                    //vertical line
                    if (float.IsInfinity(m))
                    {
                        for (int y = y1; y <= y2; y += 1)
                        {
                            SetCursorPos(x1, y);
                            Thread.Sleep(delay);
                        }

                    }
                    else if (float.IsNaN(m))
                        SetCursorPos(x1, y1);
                    else
                    {
                        for (int y = y1; y <= y2; y += 1)
                        {
                            float x = (y - c) / m;
                            SetCursorPos((int)x, y);
                            Thread.Sleep(delay);
                        }
                    }
                }
            }
            else
            {
                //horizontal line
                if (m == 0)
                {
                    if (x1 < x2)
                    {
                        for (int x = x1; x <= x2; x += 1)
                        {
                            SetCursorPos(x, y1);
                            Thread.Sleep(delay);
                        }
                    }
                    else
                        for (int x = x1; x >= x2; x -= 1)
                        {
                            SetCursorPos(x, y1);
                            Thread.Sleep(delay);
                        }

                }
                else
                {
                    //vertical line
                    if (float.IsInfinity(m))
                    {
                        for (int y = y1; y >= y2; y -= 1)
                        {
                            SetCursorPos(x1, y);
                            Thread.Sleep(delay);
                        }

                    }
                    else if (float.IsNaN(m))
                        SetCursorPos(x1, y1);
                    else
                    {
                        for (int y = y1; y >= y2; y -= 1)
                        {
                            float x = (y - c) / m;
                            SetCursorPos((int)x, y);
                            Thread.Sleep(delay);
                        }
                    }
                }
            }

        }
    }
    public class Keyboard
    {
        [DllImport("user32", EntryPoint="SendInput")]
        private static extern int SendInput(uint numberOfInputs, ref NativeAPI.Input[] input, int structSize);

        [DllImport("user32", EntryPoint="SendInput")]
        private static extern int SendInput(uint numberOfInputs, ref NativeAPI.Input input, int structSize);

        [DllImport("user32.dll")]
        private static extern IntPtr GetMessageExtraInfo();

        [DllImport("user32.dll")]
        private static extern short VkKeyScan(char ch);

        [DllImport("user32.dll")]
        private static extern uint MapVirtualKey(uint uCode, uint uMapType);

        [DllImport("user32.dll")]
        private static extern ushort GetKeyState(uint virtKey);

        /// <summary>
        /// Use Window.Keyboard method to get handle to the Keyboard. Keyboard instance got using this method would not wait while the application
        /// is busy.
        /// </summary>
        public static Keyboard Instance = new Keyboard();

        private readonly List<int> keysHeld = new List<int>();

        private Keyboard()
        {
        }

        //public virtual void Send(string keysToType)
        //{
        //    Send(keysToType, new NullActionListener());
        //}

        public void Send(string keysToType)
        {
            _Send(keysToType);  
        }

        internal virtual void _Send(string keysToType)
        {
            CapsLockOn = false;
            foreach (char c in keysToType.ToCharArray())
            {
                short key = VkKeyScan(c);
                if (c.Equals('\r'))
                    continue;

                if (ShiftKeyIsNeeded(key))
                    SendKeyDown((short)NativeAPI.KeyboardInput.SpecialKeys.SHIFT, false);
                Press(key, false);
                if (ShiftKeyIsNeeded(key))
                    SendKeyUp((short)NativeAPI.KeyboardInput.SpecialKeys.SHIFT, false);
               
            }
        }

        public virtual void PressSpecialKey(NativeAPI.KeyboardInput.SpecialKeys key)
        {
            Send(key, true);
        }

        public virtual void HoldKey(NativeAPI.KeyboardInput.SpecialKeys key)
        {
            SendKeyDown((short)key, true);
        }

     

        public virtual void LeaveKey(NativeAPI.KeyboardInput.SpecialKeys key)
        {
            SendKeyUp((short)key, true);
        }

        

        private void Press(short key, bool specialKey)
        {
            SendKeyDown(key, specialKey);
            SendKeyUp(key, specialKey);
        }

        private void Send(NativeAPI.KeyboardInput.SpecialKeys key, bool specialKey)
        {
            Press((short)key, specialKey);
        }

        private static bool ShiftKeyIsNeeded(short key)
        {
            // Look at VkKeyScan in MSDN.
            return ((key >> 8) & 1) == 1;
        }

        private void SendKeyUp(short b, bool specialKey)
        {
            if (!keysHeld.Contains(b))
                throw new Exception("Cannot press the key " + b + " as its already pressed");
            keysHeld.Remove(b);
            NativeAPI.KeyboardInput.KeyUpDown keyUpDown = NativeAPI.KeyboardInput.KeyUpDown.KEYEVENTF_KEYUP;
            if (specialKey)
                keyUpDown |= NativeAPI.KeyboardInput.KeyUpDown.KEYEVENTF_EXTENDEDKEY;
            SendInput(GetInputFor(b, keyUpDown));
        }

        private void SendKeyDown(short b, bool specialKey)
        {
            if (keysHeld.Contains(b))
                throw new Exception("Cannot press the key " + b + " as its already pressed");
            keysHeld.Add(b);
            NativeAPI.KeyboardInput.KeyUpDown keyUpDown = NativeAPI.KeyboardInput.KeyUpDown.KEYEVENTF_KEYDOWN;
            if (specialKey)
                keyUpDown |= NativeAPI.KeyboardInput.KeyUpDown.KEYEVENTF_EXTENDEDKEY;
            SendInput(GetInputFor(b, keyUpDown));
        }

        private static void SendInput(NativeAPI.Input input)
        {
            SendInput(1, ref input, Marshal.SizeOf(typeof(NativeAPI.Input)));
        }

        private static NativeAPI.Input GetInputFor(short character, NativeAPI.KeyboardInput.KeyUpDown keyUpOrDown)
        {
            return NativeAPI.Input.Keyboard(new NativeAPI.KeyboardInput(character, keyUpOrDown, GetMessageExtraInfo()));
        }

        public virtual bool CapsLockOn
        {
            get
            {
                ushort state = GetKeyState((uint)NativeAPI.KeyboardInput.SpecialKeys.CAPS);
                return state != 0;
            }
            set
            {
                if (CapsLockOn != value)
                    Send(NativeAPI.KeyboardInput.SpecialKeys.CAPS, true);
            }
        }
    }
}
#region Revision History
/// 3-Sep-2009  Rossel
///             0002793: Cannot find the bone dicom folder : test case NM -03 
#endregion Revision History
