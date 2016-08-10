#region Header
// ===================================================================================
// FileName    : MSAAHelper.cs
// 
// Namespace   : Telstra.IDA.Client.MSAAWrapper
//
// Author      : Travis Draper
// Date        : 20.Oct.2006
// QA Complete : 
// Summary     : This class provides a .NET wrapper to the MSAA functionality
//
// ===================================================================================
#endregion Header

using System;
using System.Text;
using System.Diagnostics;
using Accessibility;
using System.Threading;
using System.Collections;
using Philips.MRAutomation.Foundation.WinEventHook;


namespace MSAAWrapper
{
    /// <summary>
    /// This class provides a wrapper to using the MSAA functionality
    /// </summary>
    public class MSAAHelper
    {
        public MSAAHelper() {}

        /// <summary>
        /// Returns the first child element of the specified MSAA object
        /// </summary>
        /// <param name="accObj"></param>
        /// <returns></returns>
        public IAccessible GetFirstChild(IAccessible accObj)
        {
            return (IAccessible)accObj.accNavigate((int)NativeMethods.Navigation.NAVDIR_FIRSTCHILD, 
                                        (int)NativeMethods.NativeMsg.CHILDID_SELF);
        }

        /// <summary>
        /// Returns all the child elements of the specified MSAA object
        /// </summary>
        /// <param name="accObj"></param>
        /// <returns></returns>
        public object[] GetChildren(IAccessible accObj)
        {
            object[] children = null;

            int childCount = accObj.accChildCount;
            children = new object[childCount];
            int childrenOut;
            //*** osw *** the childCount was subtract for 1 (-1 removed)
            NativeMethods.AccessibleChildren(accObj, 0, childCount, children,out childrenOut); 

            return children;
        }


        /// <summary>
        /// Returns a child element of an MSAA object given the specified path.
        /// </summary>
        /// <param name="accObj">The MSAA object to retrieve the child from</param>
        /// <param name="path">A "/" delimited index list - e.g. 1/2 will retrieve the second child of the first child element </param>
        /// <returns>The desired child element or null</returns>
        public object GetSpecifiedChild(IAccessible accObj, string path)
        {
            string[] indexStrings = path.Split('/');
            IAccessible accParent = accObj;
            IAccessible accChild = null;
            object firstChild;
            
            //Process the specified path
            for(int i=0; i<indexStrings.Length; i++)
            {
                int index = Convert.ToInt32(indexStrings[i]);

                //The first child element of an object can be retrieved easily, so handle higher elements differently
                if (index > 1)
                {
                    //Get all the children for the element being processed
                    object[] children = GetChildren(accParent);

                    if (children[index-1] is IAccessible)
                    {
                        accChild = (IAccessible)children[index-1];
                        //Debug.WriteLine("Child " + index.ToString() + " name:" + GetName(accChild) + ", role: " + GetRole(accChild));

                        //Set the next element to be process to the element just received
                        accParent = accChild;
                    }
                    else
                    {
                        break;
                    }
                }
                else
                {
                    //Retrieve the first element
                    //Debug.WriteLine("Navigating to first child for parent " + GetName(accParent));
                    firstChild = accParent.accNavigate((int)NativeMethods.Navigation.NAVDIR_FIRSTCHILD, (int)NativeMethods.NativeMsg.CHILDID_SELF);

                    if (firstChild is IAccessible)
                    {
                        accChild = (IAccessible)firstChild;
                        //Debug.WriteLine("First child's name:" + GetName(accChild) + ", role: " + GetRole(accChild));

                        //Set the next element to be process to the element just received
                        accParent = accChild;
                    }
                    else
                    {
                        accChild = null;
                        //Debug.WriteLine("First child is not accessible");
                        break;
                    }
                }
            }


            return accChild;			
        }

        /// <summary>
        /// Executes the default action of the specified MSAA object
        /// </summary>
        /// <param name="accObj"></param>
        public void PerformDefaultAction(IAccessible accObj)
        {
            accObj.accDoDefaultAction((int)NativeMethods.NativeMsg.CHILDID_SELF);
        }

        /// <summary>
        /// Sets the text for the specified MSAA object
        /// </summary>
        /// <param name="accObj"></param>
        /// <param name="textValue"></param>
        public void SetTextValue(IAccessible accObj, string textValue)
        {
            accObj.set_accValue((int)NativeMethods.NativeMsg.CHILDID_SELF, textValue);
        }
        
        /// <summary>
        /// Gets the name of the specified MSAA object (if supported)
        /// </summary>
        /// <param name="accObj"></param>
        /// <returns>The name or "{Not implemented}"</returns>
        public string GetName(IAccessible accObj)
        {
            string name = string.Empty;

            try
            {
                name = accObj.get_accName((int)NativeMethods.NativeMsg.CHILDID_SELF);
            }
            catch (NotImplementedException)
            {
                name = "(Not implemented)";
            }
            catch (Exception e)
            {
                Console.WriteLine(e.InnerException);
            }
            return name;
        }

        /// <summary>
        /// Gets the name of the specified child of the MSAA object (if supported)
        /// </summary>
        /// <param name="accObj"></param>
        /// <returns>The name or "{Not implemented}"</returns>
        public string GetNameOfChildElement(IAccessible accObj, int index)
        {
            string name = string.Empty;
            
            try
            {
                name = accObj.get_accName(index);;
            }
            catch(NotImplementedException)
            {
                name = "(Not implemented)";
            }
            catch (Exception e)
            {
                Console.WriteLine(e.InnerException);
            }
            return name;
        }

        /// <summary>
        /// Selects the specified child element of the MSAA object
        /// </summary>
        /// <param name="accObj"></param>
        /// <param name="index"></param>
        public void SelectChildElement(IAccessible accObj, int index)
        {
            accObj.accSelect((int)NativeMethods.Selection.SELFLAG_TAKESELECTION, index);
        }

        /// <summary>
        /// Returns the state of the specified MSAA object
        /// </summary>
        /// <param name="accObj"></param>
        /// <param name="index"></param>
        /// <returns>A string containing the (multiple) state(s) of the object</returns>
        public string GetState(IAccessible accObj, int index)
        {
            string state = string.Empty;
            object stateObj = null;
            
            try
            {
                //Retrieve the flag indicating the current state(s) of the object
                stateObj = accObj.get_accState(index);
                int stateNum = (int)stateObj;

                StringBuilder sbState = new StringBuilder(20, 500);

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_ANIMATED))
                    sbState.Append("SYSTEM_ANIMATED, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_BUSY))
                    sbState.Append("SYSTEM_BUSY, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_CHECKED))
                    sbState.Append("SYSTEM_CHECKED, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_COLLAPSED))
                    sbState.Append("SYSTEM_COLLAPSED, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_DEFAULT))
                    sbState.Append("SYSTEM_DEFAULT, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_EXPANDED))
                    sbState.Append("SYSTEM_EXPANDED, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_EXTSELECTABLE))
                    sbState.Append("SYSTEM_EXTSELECTABLE, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_FLOATING))
                    sbState.Append("SYSTEM_FLOATING, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_FOCUSABLE))
                    sbState.Append("SYSTEM_FOCUSABLE, ");
                    
                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_FOCUSED))
                    sbState.Append("SYSTEM_FOCUSED, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_HOTTRACKED))
                    sbState.Append("SYSTEM_HOTTRACKED, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_INVISIBLE))
                    sbState.Append("SYSTEM_INVISIBLE, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_LINKED))
                    sbState.Append("SYSTEM_LINKED, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_MARQUEED))
                    sbState.Append("SYSTEM_MARQUEED, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_MIXED))
                    sbState.Append("SYSTEM_MIXED, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_MOVEABLE))
                    sbState.Append("SYSTEM_MOVEABLE, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_MULTISELECTABLE))
                    sbState.Append("SYSTEM_MULTISELECTABLE, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_OFFSCREEN))
                    sbState.Append("SYSTEM_OFFSCREEN, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_PRESSED))
                    sbState.Append("SYSTEM_PRESSED, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_READONLY))
                    sbState.Append("SYSTEM_READONLY, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_SELECTABLE))
                    sbState.Append("SYSTEM_SELECTABLE, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_SELECTED))
                    sbState.Append("SYSTEM_SELECTED, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_SELFVOICING))
                    sbState.Append("SYSTEM_SELFVOICING, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_SIZEABLE))
                    sbState.Append("SYSTEM_SIZEABLE, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_TRAVERSED))
                    sbState.Append("SYSTEM_TRAVERSED, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_UNAVAILABLE))
                    sbState.Append("SYSTEM_UNAVAILABLE, ");

                if (Compare(stateNum, NativeMethods.STATE_SYSTEM_NORMAL))
                    sbState.Append("SYSTEM_NORMAL, ");

                if (sbState.ToString() == string.Empty)
                    NativeMethods.GetStateText(Convert.ToUInt32(stateObj), sbState, Convert.ToUInt16(50));
                
                state = sbState.ToString();
            }
            catch(NotImplementedException)
            {
                state = "(Not implemented)";
            }
            catch (Exception e)
            {
                Console.WriteLine(e.InnerException);
            }

            return state;
        }

        private bool Compare(int valA, int mask)
        {
            int result = valA & mask;

            return (result == mask);	
        }

        /// <summary>
        /// Gets the role of the specified MSAA object (if supported)
        /// </summary>
        /// <param name="accObj"></param>
        /// <returns>The role or "{Not implemented}"</returns>
        public string GetRole(IAccessible accObj)
        {
            object roleObj = null;
            string role = string.Empty;
            
            try
            {
                roleObj = accObj.get_accRole((int)NativeMethods.NativeMsg.CHILDID_SELF);

                StringBuilder sbRole = new StringBuilder();
                NativeMethods.GetRoleText(Convert.ToUInt32(roleObj), sbRole, Convert.ToUInt16(50));

                role = sbRole.ToString();
            }
            catch(NotImplementedException)
            {
                role = "(Not implemented)";
            }
            catch (Exception e)
            {
                Console.WriteLine(e.InnerException);
            }
            return role;		
        }


        /// <summary>
        /// Gets the value of the specified MSAA object (if supported)
        /// </summary>
        /// <param name="accObj"></param>
        /// <returns>The valye or "{Not implemented}"</returns>
        public string GetValue(IAccessible accObj)
        {
            try
            {
                string result = accObj.get_accValue(0);

                if (result == null)
                    result = string.Empty;

                return result;
            }
            catch(Exception)
            {
                return string.Empty;
            }
        }
        
        /// <summary>
        /// Retrieve the MSAA object of the specified window
        /// </summary>
        /// <param name="windowTitle"></param>
        /// <param name="className"></param>
        /// <returns>The MSAA object or null</returns>
        public IAccessible GetObjectFromWindow(string windowTitle, string className)
        {
            //Try to get a handle to the specified window - wait a max of 50 seconds for it
            System.IntPtr hWndHTA = GetWindow(className, windowTitle, 50, IntPtr.Zero);

            if (hWndHTA != IntPtr.Zero)
            {
                IAccessible IAHtaFrame = null;

                NativeMethods.AccessibleObjectFromWindow(hWndHTA, 
                                                        (int)NativeMethods.NativeMsg.OBJID_CLIENT, 
                                                        ref NativeMethods.IID_IAccessible, 
                                                        ref IAHtaFrame);
                return IAHtaFrame;
            }
            else
            {
                throw new Exception(string.Format("Unable to find window with title '{0}' and class '{1}'", windowTitle, className));
            }
        }	

        /// <summary>
        /// Retrieves a reference to the HTML DOM when given a handle to an IE Window
        /// </summary>
        /// <param name="windowHandle"></param>
        /// <returns></returns>
        public static mshtml.IHTMLDocument2 GetHtmlDocumentForIEWindow(IntPtr windowHandle)
        {
            //Register the message
            uint lMsg = NativeMethods.RegisterWindowMessage("WM_HTML_GETOBJECT");

            //Get the object
            UIntPtr lRes;
            NativeMethods.SendMessageTimeout(windowHandle, lMsg, UIntPtr.Zero, IntPtr.Zero, 
                NativeMethods.SendMessageTimeoutFlags.SMTO_ABORTIFHUNG, 3000, out lRes);

            mshtml.IHTMLDocument2 htmlDoc = null;

            if(lRes != UIntPtr.Zero)
            {
                //Get the object from lRes
                htmlDoc= (mshtml.IHTMLDocument2)NativeMethods.ObjectFromLresult(lRes, typeof(mshtml.IHTMLDocument2).GUID, IntPtr.Zero);
            }
            else
            {
                throw new Exception("Unable to retrieve HTML document from window with handle " + windowHandle.ToString());
            }

            return htmlDoc;
        }

        /// <summary>
        /// Retrieves a window handle given it's title and class
        /// </summary>
        /// <param name="className"></param>
        /// <param name="title"></param>
        /// <param name="maxWaitInSeconds">Max amount of seconds to wait for the window to appear</param>
        /// <param name="avoidWindow"></param>
        /// <returns></returns>
        private IntPtr GetWindow(string className, string title, int maxWaitInSeconds, IntPtr avoidWindow)
        {
            IntPtr window = IntPtr.Zero;
            int i = 0, max = int.MaxValue;
            int quarterMax = 536870911;
            if (maxWaitInSeconds < quarterMax)
            {
                max = maxWaitInSeconds * 4;
            }
            while ( (((window = NativeMethods.FindWindow(className, title)) == IntPtr.Zero) || (window != IntPtr.Zero && window == avoidWindow)) && (i++ < max))
            {
                Thread.Sleep(250);
            }
            return window;
        }
        /// <summary>
        /// Repaint the HWND window
        /// </summary>
        /// <param name="hwndWindowToBeRefreshed"></param>
        /// <returns></returns>
        public static long RefreshWindow(IntPtr hwndWindowToBeRefreshed)
        {
            long lRet = 0;

            NativeMethods.RECT rect = new NativeMethods.RECT();
            NativeMethods.GetClientRect((int)hwndWindowToBeRefreshed, out rect);
            NativeMethods.InvalidateRect(hwndWindowToBeRefreshed, ref rect, true);
            NativeMethods.UpdateWindow(hwndWindowToBeRefreshed);
            NativeMethods.RedrawWindow(hwndWindowToBeRefreshed, ref rect , IntPtr.Zero, 
                NativeMethods.RDW_FRAME | NativeMethods.RDW_INVALIDATE |
                NativeMethods.RDW_UPDATENOW | NativeMethods.RDW_ALLCHILDREN);
            return lRet;
        }

    }
}

