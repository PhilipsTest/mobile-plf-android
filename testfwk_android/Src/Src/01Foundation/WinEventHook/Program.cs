using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using MSAAWrapper;

namespace Philips.MRAutomation.Utilities.WinEventHook
{
    class Program
    {


        //static void Main(string[] args)
        //{
        //    WinEventHandler evnthndlr = new WinEventHandler(0);
        //    evnthndlr.WindowsDialogPopup += new WinEventHandler.WinEventDelegate(evnthndlr_WindowsDialogPopup);
        //    evnthndlr.Listen();
        //    evnthndlr.ModalDialogPopup += new WinEventHandler.WinEventDelegate(evnthndlr_ModalDialogPopup);

        //    evnthndlr.Detach(); 
        //}

        static void evnthndlr_ModalDialogPopup(WinEventArgs args)
        {
            Console.WriteLine(args.Name + "Poped up");
            
        }

        static void evnthndlr_WindowsDialogPopup(WinEventArgs args)
        {
            Console.WriteLine(args.Name + "Poped up");
            
        }



    }
}

