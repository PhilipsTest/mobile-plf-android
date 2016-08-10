using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing; 
using TCaptureXLib;
using System.Windows.Automation;
using SW=System.Windows;

namespace Philips.MRAutomation.Foundation.FrameworkCore.Common
{
   public class TextGetter
    {
       public static string GetTextFromWindow(AutomationElement element)
       {
           TextCaptureXClass textcapture=new TextCaptureXClass();
           string textfromAutomationElement=string.Empty;
           bool isTop=textcapture.BringWindowToTop;  
           textfromAutomationElement= textcapture.GetTextFromRect(element.Current.NativeWindowHandle,
                                                                  (int)element.Current.BoundingRectangle.Left,
                                                                  (int)element.Current.BoundingRectangle.Top,
                                                                  (int)element.Current.BoundingRectangle.Width,
                                                                  (int)element.Current.BoundingRectangle.Height);
           return textfromAutomationElement; 
       }

       public static string GetTextFromWindow(AutomationElement element,Rectangle r)
       {
           TextCaptureXClass textcapture=new TextCaptureXClass();
           string textfromAutomationElement=string.Empty;
           bool isTop=textcapture.BringWindowToTop;
           textfromAutomationElement= textcapture.GetTextFromRect(element.Current.NativeWindowHandle,
                                                                  r.Left,r.Top,r.Width,r.Height);
           return textfromAutomationElement;
       }
       
       public static Bitmap GetBitmap(AutomationElement element)
       {
           IntPtr hDesktopDC = NativeAPI.GetDC(new IntPtr(element.Current.NativeWindowHandle));
           IntPtr hCaptureDC = NativeAPI.CreateCompatibleDC(hDesktopDC);
           IntPtr hCaptureBitmap =NativeAPI.CreateCompatibleBitmap(hDesktopDC,
                                   Convert.ToInt32(element.Current.BoundingRectangle.Width),
                                   Convert.ToInt32(element.Current.BoundingRectangle.Height));
           NativeAPI.SelectObject(hCaptureDC, hCaptureBitmap);
           NativeAPI.BitBlt(hCaptureDC, 0, 0,
                            Convert.ToInt32(element.Current.BoundingRectangle.Width),
                            Convert.ToInt32(element.Current.BoundingRectangle.Height),
                            hDesktopDC, 0, 0, NativeAPI.SRCCOPY|NativeAPI.CAPTUREBLT);
           return Bitmap.FromHbitmap(hCaptureBitmap);
       }



    }
}

